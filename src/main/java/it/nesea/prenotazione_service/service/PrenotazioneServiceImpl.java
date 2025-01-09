package it.nesea.prenotazione_service.service;

import it.nesea.albergo.common_lib.dto.PrezzoCameraDTO;
import it.nesea.albergo.common_lib.exception.BadRequestException;
import it.nesea.albergo.common_lib.exception.NotFoundException;
import it.nesea.prenotazione_service.controller.feign.HotelExternalController;
import it.nesea.prenotazione_service.controller.feign.UserExternalController;
import it.nesea.prenotazione_service.dto.request.PrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.PreventivoRequest;
import it.nesea.prenotazione_service.dto.response.PrenotazioneResponse;
import it.nesea.prenotazione_service.dto.response.PreventivoResponse;
import it.nesea.prenotazione_service.mapper.PreventivoMapper;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.Preventivo;
import it.nesea.prenotazione_service.model.repository.PrenotazioneRepository;
import it.nesea.prenotazione_service.model.repository.PreventivoRepository;
import it.nesea.prenotazione_service.util.Util;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Data
@AllArgsConstructor
@Slf4j
public class PrenotazioneServiceImpl implements PrenotazioneService {

    private final PreventivoRepository preventivoRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final Util util;
    private final HotelExternalController hotelExternalController;
    private final EntityManager entityManager;
    private final PreventivoMapper preventivoMapper;
    private final UserExternalController userExternalController;


    @Override
    public PreventivoResponse richiediPreventivo(PreventivoRequest request) {
        log.debug("Richiesta preventivo con parametri: {}", request);

        util.isDateValid(request.getCheckIn(), request.getCheckOut());

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Preventivo> preventivoQuery = cb.createQuery(Preventivo.class);
        Root<Preventivo> preventivoRoot = preventivoQuery.from(Preventivo.class);

        Predicate dateOverlapWithPrenotato = cb.and(
                cb.lessThanOrEqualTo(preventivoRoot.get("checkIn"), request.getCheckOut()),
                cb.greaterThanOrEqualTo(preventivoRoot.get("checkOut"), request.getCheckIn()),
                cb.isTrue(preventivoRoot.get("prenotato")) // Considera solo preventivi prenotati
        );

        Predicate idTipoCameraPredicate = cb.conjunction();
        if (request.getIdTipoCamera() != null) {
            log.debug("Ricevuta richiesta con selezione idTipoCamera: {}", request.getIdTipoCamera());
            idTipoCameraPredicate = cb.equal(preventivoRoot.get("idTipoCamera"), request.getIdTipoCamera());
        }

        Predicate groupIdPredicate = cb.conjunction();
        if (request.getGroupId() != null) {
            log.debug("Ricevuta richiesta con selezione groupId: {}", request.getGroupId());
            groupIdPredicate = cb.equal(preventivoRoot.get("groupId"), request.getGroupId());
        }

        preventivoQuery.where(cb.and(dateOverlapWithPrenotato, idTipoCameraPredicate, groupIdPredicate));
        List<Preventivo> preventiviPrenotati = entityManager.createQuery(preventivoQuery).getResultList();

        if (request.getGroupId() != null && preventiviPrenotati.isEmpty()) {
            log.error("Group id {} non associato a nessuna prenotazione esistente.", request.getGroupId());
            throw new BadRequestException("Il groupId fornito non esiste. Se desideri utilizzarlo, assicurati che sia associato a una prenotazione esistente.");
        }

        List<PrezzoCameraDTO> prezzi = hotelExternalController.getListaPrezzoCamera(request.getListaIdFasciaEta()).getBody().getResponse();

        List<PrezzoCameraDTO> camereDisponibili = prezzi.stream()
                .filter(prezzo -> prezzo.getIdTipo() >= request.getListaIdFasciaEta().size())
                .toList();

        if (camereDisponibili.isEmpty()) {
            log.error("Nessuna camera disponibile con capacità sufficiente.");
            throw new NotFoundException("Nessuna camera disponibile con capacità sufficiente.");
        }

        PrezzoCameraDTO prezzoCamera = camereDisponibili.stream()
                .min(Comparator.comparingInt(camera -> Math.abs(camera.getIdTipo() - request.getListaIdFasciaEta().size())))
                .orElseThrow(() -> new NotFoundException("Nessuna camera disponibile con capacità sufficiente."));
        log.debug("Prezzario [{}] trovato per la richiesta [{}] ", prezzoCamera, request);

        BigDecimal prezzoTotale = prezzoCamera.getPrezzoTotale().multiply(
                BigDecimal.valueOf(request.getCheckOut().getDayOfMonth() - request.getCheckIn().getDayOfMonth())
        );

        // Creazione del preventivo
        Preventivo preventivo = preventivoMapper.fromPrezzoCameraDTOToPreventivo(prezzoCamera);
        preventivo.setCheckIn(request.getCheckIn());
        preventivo.setCheckOut(request.getCheckOut());
        preventivo.setListaIdFasciaEta(request.getListaIdFasciaEta());
        preventivo.setPrenotato(false);
        preventivo.setGroupId(request.getGroupId() != null ? request.getGroupId() : util.generaGroupId());

        preventivoRepository.save(preventivo);
        log.debug("Preventivo [{}] creato con successo", preventivo);

        return new PreventivoResponse.PreventivoResponseBuilder()
                .numeroOccupanti(preventivo.getListaIdFasciaEta().size())
                .idTipo(preventivo.getIdTipoCamera())
                .prezziAPersona(prezzoCamera.getPrezziAPersona())
                .numeroCamera(preventivo.getNumeroCamera())
                .prezzoTotale(prezzoTotale)
                .id(preventivo.getId())
                .build();
    }

    @Override
    public PrenotazioneResponse prenota(PrenotazioneRequest request) {
        log.debug("Oggetto request in input: [{}]", request);

        if (!userExternalController.checkUtente(request.getIdUtente()).getBody().getResponse()) {
            log.error("Utente {} non valido", request.getIdUtente());
            throw new NotFoundException("Utente non valido");
        }

        Optional<Preventivo> ricercaPreventivo = preventivoRepository.findById(request.getIdPreventivo());
        if (ricercaPreventivo.isEmpty()) {
            log.error("Preventivo {} non trovato", request.getIdPreventivo());
            throw new NotFoundException("Preventivo non trovato");
        }
        Preventivo preventivoEsistente = ricercaPreventivo.get();

        if (preventivoEsistente.getPrenotato()) {
            log.error("Preventivo {} già prenotato", request.getIdPreventivo());
            throw new BadRequestException("Il preventivo che stai cercando di prenotare è già stato prenotato");
        }

        //Creo una nuova prenotazione
        Prenotazione nuovaPrenotazione = new Prenotazione();
        nuovaPrenotazione.setIdMetodoPagamento(request.getIdMetodoPagamento());
        nuovaPrenotazione.setCodicePrenotazione(util.generaCodicePrenotazione());
        nuovaPrenotazione.setPreventivo(preventivoEsistente);
        nuovaPrenotazione.setIdUtente(request.getIdUtente());

        prenotazioneRepository.save(nuovaPrenotazione);
        util.prenotaPreventivo(preventivoEsistente);
        log.info("Preventivo [{}] prenotato con successo", preventivoEsistente);

        return new PrenotazioneResponse.PrenotazioneResponseBuilder()
                .codicePrenotazione(nuovaPrenotazione.getCodicePrenotazione())
                .groupId(preventivoEsistente.getGroupId()).prezzoCamera(preventivoEsistente.getPrezzoTotale()).build();
    }
}

package it.nesea.prenotazione_service.service;

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
import it.nesea.prenotazione_service.model.StagioneEntity;
import it.nesea.prenotazione_service.model.repository.PrenotazioneRepository;
import it.nesea.prenotazione_service.model.repository.PreventivoRepository;
import it.nesea.prenotazione_service.util.Util;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
                .groupId(preventivoEsistente.getGroupId())
                .prezzoCamera(preventivoEsistente.getPrezzoTotale())
                .build();
    }

    public PreventivoResponse richiediPreventivo(PreventivoRequest request) {

        LocalDate checkIn = request.getCheckIn();
        LocalDate checkOut = request.getCheckOut();

        util.isDateValid(checkIn, checkOut);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StagioneEntity> percentualeMaggiorazioneQuery = cb.createQuery(StagioneEntity.class);
        Root<StagioneEntity> stagioneRoot = percentualeMaggiorazioneQuery.from(StagioneEntity.class);
        percentualeMaggiorazioneQuery.select(stagioneRoot);

        List<StagioneEntity> stagioni = entityManager.createQuery(percentualeMaggiorazioneQuery).getResultList();

        int percentualeMaggiorazione = util.getPercentualeMaggiorazione(checkIn, checkOut, stagioni);

        List<BigDecimal> prezziAPersona = request.getPrezzarioCamera().getPrezziAPersona();
        prezziAPersona.replaceAll(aPersona -> aPersona.multiply(BigDecimal.valueOf(1 + (percentualeMaggiorazione / 100.0))));

        request.getPrezzarioCamera().setPrezziAPersona(prezziAPersona);

        BigDecimal prezzoNuoviOccupanti = request.getPrezzarioCamera().getPrezzoTotale()
                .multiply(BigDecimal.valueOf(util.calcolaNumeroGiorni(checkIn, checkOut)));

        BigDecimal prezzoNuoviOccupantiConMaggiorazione = prezzoNuoviOccupanti
                .multiply(BigDecimal.valueOf(1 + (percentualeMaggiorazione / 100.0)));  // Divisione con 100.0 per ottenere la parte decimale corretta

        request.getPrezzarioCamera().setPrezzoTotale(prezzoNuoviOccupantiConMaggiorazione);

        return preventivoMapper.fromPrezzoCameraDTOToPreventivoResponse(request.getPrezzarioCamera());
    }
}

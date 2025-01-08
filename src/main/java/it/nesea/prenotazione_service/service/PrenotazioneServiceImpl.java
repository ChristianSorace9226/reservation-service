package it.nesea.prenotazione_service.service;

import it.nesea.albergo.common_lib.dto.PrezzoCameraDTO;
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
import it.nesea.prenotazione_service.util.PrenotazioneUtil;
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
    private final PrenotazioneUtil prenotazioneUtil;
    private final HotelExternalController hotelExternalController;
    private final EntityManager entityManager;
    private final PreventivoMapper preventivoMapper;
    private final UserExternalController userExternalController;


    @Override
    public PreventivoResponse richiediPreventivo(PreventivoRequest request) {
        log.info("Richiesta preventivo con parametri: {}", request);

        // Costruisco il CriteriaBuilder per filtrare le prenotazioni
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> prenotazioneQuery = cb.createQuery(String.class);
        Root<Prenotazione> prenotazioneRoot = prenotazioneQuery.from(Prenotazione.class);

        // Predicati per controllare l'intervallo di date
        Predicate dateOverlap = cb.and(
                cb.lessThanOrEqualTo(prenotazioneRoot.get("preventivo").get("checkIn"), request.getCheckOut()),
                cb.greaterThanOrEqualTo(prenotazioneRoot.get("preventivo").get("checkOut"), request.getCheckIn())
        );

        Predicate idTipoCamera = cb.equal(prenotazioneRoot.get("preventivo").get("idTipoCamera"), request.getIdTipoCamera());
        Predicate groupId = cb.equal(prenotazioneRoot.get("preventivo").get("groupId"), request.getGroupId());

        // Seleziono tutte le camere coinvolte nelle prenotazioni che si sovrappongono
        prenotazioneQuery.select(prenotazioneRoot.get("preventivo").get("numeroCamera")).where(dateOverlap);
        if (idTipoCamera != null) {
            prenotazioneQuery.where(idTipoCamera);
        }
        String newGroupId = request.getGroupId();
        if (request.getGroupId() != null) {
            if (preventivoRepository.findPreventivoByGroupId(request.getGroupId()).isPresent()) {
                prenotazioneQuery.where(groupId);
            } else {
                newGroupId = prenotazioneUtil.generaCodicePrenotazione();
            }
        }

        List<String> camereNonDisponibili = entityManager.createQuery(prenotazioneQuery).getResultList();

        // Chiamo l'ExternalController per ottenere i prezzi delle camere
        List<PrezzoCameraDTO> prezzi = hotelExternalController.getListaPrezzoCamera(request.getListaIdFasciaEta()).getBody().getResponse();

        // Filtro i prezzi per includere solo le camere disponibili
        List<PrezzoCameraDTO> camereDisponibili = prezzi.stream()
                .filter(prezzo -> !camereNonDisponibili.contains(prezzo.getNumeroCamera()))
                .toList();

        // Verifico se esistono camere disponibili
        if (camereDisponibili.isEmpty()) {
            throw new NotFoundException("Nessuna camera disponibile per le date richieste.");
        }

        // Seleziona la camera il cui ID tipo è il più vicino alla dimensione della lista delle fasce d'età
        PrezzoCameraDTO prezzoCamera = camereDisponibili.stream()
                .min(Comparator.comparingInt(camera -> Math.abs(camera.getIdTipo() - request.getListaIdFasciaEta().size())))
                .orElseThrow(() -> new NotFoundException("Nessuna camera disponibile con capacità sufficiente per il numero di persone richiesto."));

        if (prezzoCamera == null) {
            throw new NotFoundException("Nessuna camera disponibile con capacità sufficiente per il numero di persone richiesto.");
        }

        BigDecimal prezzoTotale = prezzoCamera.getPrezzoTotale().multiply(BigDecimal.valueOf(request.getCheckOut().getDayOfMonth() - request.getCheckIn().getDayOfMonth()));

        Preventivo preventivo = preventivoMapper.fromPrezzoCameraDTOToPreventivo(prezzoCamera);
        preventivo.setCheckIn(request.getCheckIn());
        preventivo.setCheckOut(request.getCheckOut());
        preventivo.setListaIdFasciaEta(request.getListaIdFasciaEta());
        preventivo.setGroupId(newGroupId);
        preventivoRepository.save(preventivo);

        return new PreventivoResponse.PreventivoResponseBuilder()
                .numeroOccupanti(prezzoCamera.getNumeroOccupanti()).idTipo(prezzoCamera.getIdTipo()).prezziAPersona(prezzoCamera.getPrezziAPersona())
                .numeroCamera(prezzoCamera.getNumeroCamera()).prezzoTotale(prezzoTotale).id(preventivo.getId()).build();
    }

    @Override
    public PrenotazioneResponse prenota(PrenotazioneRequest request) {
        log.info("Oggetto request in input: [{}]", request);

        if (!userExternalController.checkUtente(request.getIdUtente()).getBody().getResponse()) {
            throw new NotFoundException("Utente non valido");
        }

        Optional<Preventivo> ricercaPreventivo = preventivoRepository.findById(request.getIdPreventivo());
        if (ricercaPreventivo.isEmpty()) {
            throw new NotFoundException("Preventivo non trovato");
        }
        Preventivo preventivoEsistente = ricercaPreventivo.get();

        //Creo una nuova prenotazione
        Prenotazione nuovaPrenotazione = new Prenotazione();
        nuovaPrenotazione.setIdMetodoPagamento(request.getIdMetodoPagamento());
        nuovaPrenotazione.setCodicePrenotazione(prenotazioneUtil.generaCodicePrenotazione());
        nuovaPrenotazione.setPreventivo(preventivoEsistente);
        nuovaPrenotazione.setIdUtente(request.getIdUtente());

        prenotazioneRepository.save(nuovaPrenotazione);

        return new PrenotazioneResponse.PrenotazioneResponseBuilder()
                .codicePrenotazione(nuovaPrenotazione.getCodicePrenotazione())
                .groupId(preventivoEsistente.getGroupId()).prezzoCamera(preventivoEsistente.getPrezzoTotale()).build();
    }
}

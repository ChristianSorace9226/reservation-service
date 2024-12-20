package it.nesea.prenotazione_service.service;

import it.nesea.albergo.common_lib.dto.PrezzoCameraDTO;
import it.nesea.albergo.common_lib.exception.NotFoundException;
import it.nesea.prenotazione_service.controller.feign.ExternalController;
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
    private final ExternalController externalController;
    private final EntityManager entityManager;
    private final PreventivoMapper preventivoMapper;


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

        // Seleziono tutte le camere coinvolte nelle prenotazioni che si sovrappongono
        prenotazioneQuery.select(prenotazioneRoot.get("preventivo").get("numeroCamera")).where(dateOverlap);
        List<String> camereNonDisponibili = entityManager.createQuery(prenotazioneQuery).getResultList();

        // Chiamo l'ExternalController per ottenere i prezzi delle camere
        List<PrezzoCameraDTO> prezzi = externalController.getListaPrezzoCamera(request.getListaIdFasciaEta()).getBody().getResponse();

        // Filtro i prezzi per includere solo le camere disponibili
        List<PrezzoCameraDTO> camereDisponibili = prezzi.stream()
                .filter(prezzo -> !camereNonDisponibili.contains(prezzo.getNumeroCamera()))
                .toList();

        // Verifico se esistono camere disponibili
        if (camereDisponibili.isEmpty()) {
            throw new NotFoundException("Nessuna camera disponibile per le date richieste.");
        }

        // Calcolo il prezzo totale per una camera disponibile
        PrezzoCameraDTO prezzoCamera = camereDisponibili.getFirst(); // Scelta della prima camera disponibile
        BigDecimal prezzoTotale = prezzoCamera.getPrezzoTotale().multiply(BigDecimal.valueOf(request.getCheckOut().getDayOfMonth() - request.getCheckIn().getDayOfMonth()));

        Preventivo preventivo = preventivoMapper.fromPrezzoCameraDTOToPreventivo(prezzoCamera);
        preventivo.setCheckIn(request.getCheckIn());
        preventivo.setCheckOut(request.getCheckOut());
        preventivo.setListaIdFasciaEta(request.getListaIdFasciaEta());
        preventivoRepository.save(preventivo);

        // Costruisco la risposta
        PreventivoResponse response = new PreventivoResponse();
        response.setNumeroOccupanti(prezzoCamera.getNumeroOccupanti());
        response.setIdTipo(prezzoCamera.getIdTipo());
        response.setPrezziAPersona(prezzoCamera.getPrezziAPersona());
        response.setNumeroCamera(prezzoCamera.getNumeroCamera());
        response.setPrezzoTotale(prezzoTotale);
        response.setId(preventivo.getId());
        log.info("Preventivo calcolato: {}", response);
        return response;
    }

    @Override
    public PrenotazioneResponse prenota(PrenotazioneRequest request) {
        log.info("Oggetto request in input: [{}]", request);
        PrenotazioneResponse response = new PrenotazioneResponse();

        Optional<Preventivo> ricercaPreventivo = preventivoRepository.findById(request.getIdPreventivoResponse());
        if (ricercaPreventivo.isEmpty()) {
            throw new NotFoundException("Preventivo non trovato");
        }
        Preventivo preventivoEsistente = ricercaPreventivo.get();

        Optional<Prenotazione> ricercaPrenotazionePerGroupId = prenotazioneRepository.findPrenotazioneByGroupId(request.getGroupId());
        //Creo una nuova prenotazione
        Prenotazione nuovaPrenotazione = new Prenotazione();
        if (ricercaPrenotazionePerGroupId.isPresent()) {

            //Aggiorno solo il metodo di pagamento dei nuovi arrivati
            nuovaPrenotazione.setIdMetodoPagamento(request.getIdMetodoPagamento());
            nuovaPrenotazione.setGroupId(request.getGroupId());
            nuovaPrenotazione.setPreventivo(preventivoEsistente);
            nuovaPrenotazione.setIdUtente(request.getIdUtente());
            nuovaPrenotazione.setCodicePrenotazione(prenotazioneUtil.generaCodicePrenotazione());

            response.setGroupId(nuovaPrenotazione.getGroupId());
            response.setPrezzoCamera(preventivoEsistente.getPrezzoTotale());
            response.setCodicePrenotazione(nuovaPrenotazione.getCodicePrenotazione());
            return response;
        }
        nuovaPrenotazione.setIdMetodoPagamento(request.getIdMetodoPagamento());
        nuovaPrenotazione.setCodicePrenotazione(prenotazioneUtil.generaCodicePrenotazione());
        nuovaPrenotazione.setPreventivo(preventivoEsistente);
        nuovaPrenotazione.setIdUtente(request.getIdUtente());
        nuovaPrenotazione.setGroupId(request.getGroupId());

        prenotazioneRepository.save(nuovaPrenotazione);

        response.setCodicePrenotazione(nuovaPrenotazione.getCodicePrenotazione());
        response.setPrezzoCamera(preventivoEsistente.getPrezzoTotale());
        response.setGroupId(nuovaPrenotazione.getGroupId());

        return response;
    }
}

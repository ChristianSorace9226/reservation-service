package it.nesea.prenotazione_service.service;

import it.nesea.albergo.common_lib.exception.BadRequestException;
import it.nesea.albergo.common_lib.exception.NotFoundException;
import it.nesea.prenotazione_service.controller.feign.HotelExternalController;
import it.nesea.prenotazione_service.controller.feign.UserExternalController;
import it.nesea.prenotazione_service.dto.request.PrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.PrenotazioneRequestSecondo;
import it.nesea.prenotazione_service.dto.request.PreventivoRequest;
import it.nesea.prenotazione_service.dto.response.PrenotazioneResponse;
import it.nesea.prenotazione_service.dto.response.PrenotazioneResponseSecondo;
import it.nesea.prenotazione_service.dto.response.PreventivoResponse;
import it.nesea.prenotazione_service.mapper.PrenotazioneMapper;
import it.nesea.prenotazione_service.mapper.PreventivoMapper;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.PrenotazioneSave;
import it.nesea.prenotazione_service.model.Preventivo;
import it.nesea.prenotazione_service.model.repository.PrenotazioneRepository;
import it.nesea.prenotazione_service.model.repository.PrenotazioneSaveRepository;
import it.nesea.prenotazione_service.model.repository.PreventivoRepository;
import it.nesea.prenotazione_service.util.Util;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.chrono.ChronoLocalDate;
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
    private final PrenotazioneMapper prenotazioneMapper;
    private final UserExternalController userExternalController;
    private final PrenotazioneSaveRepository prenotazioneSaveRepository;

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

//        prenotazioneRepository.save(nuovaPrenotazione);
        util.prenotaPreventivo(preventivoEsistente);
        log.info("Preventivo [{}] prenotato con successo", preventivoEsistente);

        return new PrenotazioneResponse.PrenotazioneResponseBuilder()
                .codicePrenotazione(nuovaPrenotazione.getCodicePrenotazione())
                .groupId(preventivoEsistente.getGroupId())
                .prezzoCamera(preventivoEsistente.getPrezzoTotale())
                .build();
    }


    @Override
    public PrenotazioneResponseSecondo prenotazione(PrenotazioneRequestSecondo request) {
        log.debug("Oggetto request in input: [{}]", request);

        util.isDateValid(request.getCheckIn(), request.getCheckOut());

        if (!userExternalController.checkUtente(request.getIdUtente()).getBody().getResponse()) {
            log.error("Utente {} non valido", request.getIdUtente());
            throw new NotFoundException("Utente non valido");
        }

        if (request.getGroupId() != null) {
            Optional<PrenotazioneSave> prenotazioneOptional = prenotazioneSaveRepository.findByGroupId(request.getGroupId());
            if (prenotazioneOptional.isEmpty()) {
                log.error("Prenotazione con groupId {} non trovata", request.getGroupId());
                throw new NotFoundException("Prenotazione con groupId non trovata");
            }
            PrenotazioneSave prenotazioneEsistente = prenotazioneOptional.get();
            if ((prenotazioneEsistente.getIdTipoCamera() - request.getListaEta().size() < 0)) {
                log.error("Il numero di persone da unire al gruppo eccede la capienza della camera");
                throw new BadRequestException("Il numero di persone da unire al gruppo eccede la capienza della camera");
            }
            if (request.getCheckOut().isAfter(ChronoLocalDate.from(prenotazioneEsistente.getCheckOut()))){
                log.error("La data di check-out non può essere successiva a quella della prenotazione esistente");
                throw new BadRequestException("La data di check-out non può essere successiva a quella della prenotazione esistente");
            }
            List<Integer> etaEsistenti = prenotazioneEsistente.getListaEta();
            etaEsistenti.addAll(request.getListaEta());
            request.setListaEta(etaEsistenti);
        }

        PreventivoRequest preventivoRequest = util.calcolaPrezzoFinale(request);
        request.setPrezzarioCamera(preventivoRequest.getPrezzarioCamera());
        prenotazioneSaveRepository.save(prenotazioneMapper.fromPrenotazioneRequestToPrenotazione(request));
        return prenotazioneMapper.fromPrenotazioneRequestToPrenotazioneResponse(request);
    }

    public PreventivoResponse richiediPreventivo(PreventivoRequest request) {

        request = util.calcolaPrezzoFinale(request);

        return preventivoMapper.fromPrezzoCameraDTOToPreventivoResponse(request.getPrezzarioCamera());
    }
}

package it.nesea.prenotazione_service.service;

import it.nesea.albergo.common_lib.dto.request.CheckDateStart;
import it.nesea.albergo.common_lib.exception.BadRequestException;
import it.nesea.albergo.common_lib.exception.NotFoundException;
import it.nesea.prenotazione_service.controller.feign.HotelExternalController;
import it.nesea.prenotazione_service.controller.feign.UserExternalController;
import it.nesea.prenotazione_service.dto.request.PrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.PreventivoRequest;
import it.nesea.prenotazione_service.dto.response.PrenotazioneResponse;
import it.nesea.prenotazione_service.dto.response.PreventivoResponse;
import it.nesea.prenotazione_service.mapper.PrenotazioneMapper;
import it.nesea.prenotazione_service.mapper.PreventivoMapper;
import it.nesea.prenotazione_service.model.PrenotazioneSave;
import it.nesea.prenotazione_service.model.repository.PrenotazioneSaveRepository;
import it.nesea.prenotazione_service.util.Util;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Data
@AllArgsConstructor
@Slf4j
public class PrenotazioneServiceImpl implements PrenotazioneService {

    private final Util util;
    private final HotelExternalController hotelExternalController;
    private final EntityManager entityManager;
    private final PreventivoMapper preventivoMapper;
    private final PrenotazioneMapper prenotazioneMapper;
    private final UserExternalController userExternalController;
    private final PrenotazioneSaveRepository prenotazioneSaveRepository;


    //todo: controllo su 1) numero camera con stesso groupId

    @Transactional
    @Override
    public PrenotazioneResponse prenotazione(PrenotazioneRequest request) {
        log.debug("Oggetto request in input: [{}]", request);

        util.isDateValid(request.getCheckIn(), request.getCheckOut());

        CheckDateStart checkDateStart = new CheckDateStart(request.getPrezzarioCamera().getNumeroCamera(),
                request.getCheckIn().atStartOfDay());


        if (!hotelExternalController.checkDisponibilita(checkDateStart).getBody().getResponse()) {
            log.error("Camera non ancora disponibile");
            throw new BadRequestException("Camera non ancora disponibile");
        }

        if (!userExternalController.checkUtente(request.getIdUtente()).getBody().getResponse()) {
            log.error("Utente {} non valido", request.getIdUtente());
            throw new NotFoundException("Utente non valido");
        }

        PrenotazioneSave prenotazioneSave = new PrenotazioneSave();

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
            if (request.getCheckOut().isAfter(ChronoLocalDate.from(prenotazioneEsistente.getCheckOut()))) {
                log.error("La data di check-out non può essere successiva a quella della prenotazione esistente");
                throw new BadRequestException("La data di check-out non può essere successiva a quella della prenotazione esistente");
            }
            List<Integer> etaEsistenti = prenotazioneEsistente.getListaEta();
            etaEsistenti.addAll(request.getListaEta());
            request.setListaEta(etaEsistenti);

            PreventivoRequest preventivoRequest = util.calcolaPrezzoFinale(request);
            request.setPrezzarioCamera(preventivoRequest.getPrezzarioCamera());
            prenotazioneSave = prenotazioneMapper.fromPrenotazioneRequestToPrenotazione(request);
            prenotazioneSave.setCodicePrenotazione(util.generaCodicePrenotazione());

            prenotazioneSaveRepository.save(prenotazioneSave);
            return prenotazioneMapper.fromPrenotazioneRequestToPrenotazioneResponse(prenotazioneSave);
        }

        PreventivoRequest preventivoRequest = util.calcolaPrezzoFinale(request);
        request.setPrezzarioCamera(preventivoRequest.getPrezzarioCamera());
        prenotazioneSave = prenotazioneMapper.fromPrenotazioneRequestToPrenotazione(request);
        prenotazioneSave.setCodicePrenotazione(util.generaCodicePrenotazione());
        prenotazioneSave.setGroupId(util.generaGroupId());

        prenotazioneSaveRepository.save(prenotazioneSave);
        return prenotazioneMapper.fromPrenotazioneRequestToPrenotazioneResponse(prenotazioneSave);
    }

    public PreventivoResponse richiediPreventivo(PreventivoRequest request) {

        request = util.calcolaPrezzoFinale(request);

        return preventivoMapper.fromPrezzoCameraDTOToPreventivoResponse(request.getPrezzarioCamera());
    }
}

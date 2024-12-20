package it.nesea.prenotazione_service.service;

import it.nesea.albergo.common_lib.exception.NotFoundException;
import it.nesea.prenotazione_service.dto.request.PrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.PreventivoRequest;
import it.nesea.prenotazione_service.dto.response.PrenotazioneResponse;
import it.nesea.prenotazione_service.dto.response.PreventivoResponse;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.Preventivo;
import it.nesea.prenotazione_service.model.repository.PrenotazioneRepository;
import it.nesea.prenotazione_service.model.repository.PreventivoRepository;
import it.nesea.prenotazione_service.util.PrenotazioneUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Data
@AllArgsConstructor
@Slf4j
public class PrenotazioneServiceImpl implements PrenotazioneService {

    private final PreventivoRepository preventivoRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final PrenotazioneUtil prenotazioneUtil;


    @Override
    public PreventivoResponse richiediPreventivo(PreventivoRequest request) {
        return null;
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
            response.setPrezzoCamera(preventivoEsistente.getPrezzoCamera());
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
        response.setPrezzoCamera(preventivoEsistente.getPrezzoCamera());
        response.setGroupId(nuovaPrenotazione.getGroupId());

        return response;
    }
}

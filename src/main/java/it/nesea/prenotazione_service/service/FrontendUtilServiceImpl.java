package it.nesea.prenotazione_service.service;

import it.nesea.albergo.common_lib.exception.NotFoundException;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.Preventivo;
import it.nesea.prenotazione_service.model.repository.PrenotazioneRepository;
import it.nesea.prenotazione_service.model.repository.PreventivoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrontendUtilServiceImpl implements FrontendUtilService {

    private final PreventivoRepository preventivoRepository;
    private final PrenotazioneRepository prenotazioneRepository;

    public FrontendUtilServiceImpl(PreventivoRepository preventivoRepository, PrenotazioneRepository prenotazioneRepository) {
        this.preventivoRepository = preventivoRepository;
        this.prenotazioneRepository = prenotazioneRepository;
    }

    public List<Preventivo> getAllPreventivi() {
        List<Preventivo> preventivi = preventivoRepository.findAll();
        if (preventivi.isEmpty()) {
            throw new NotFoundException("Nessun preventivo trovato");
        }
        return preventivi;
    }

    @Override
    public List<Prenotazione> getAllPrenotazioni() {
        List<Prenotazione> prenotazioni = prenotazioneRepository.findAll();
        if (prenotazioni.isEmpty()) {
            throw new NotFoundException("Nessun preventivo trovato");
        }
        return prenotazioni;
    }


}

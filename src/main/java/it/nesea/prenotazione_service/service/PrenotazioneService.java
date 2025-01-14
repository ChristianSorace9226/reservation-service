package it.nesea.prenotazione_service.service;

import it.nesea.prenotazione_service.dto.request.PrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.PrenotazioneRequestSecondo;
import it.nesea.prenotazione_service.dto.request.PreventivoRequest;
import it.nesea.prenotazione_service.dto.response.PrenotazioneResponse;
import it.nesea.prenotazione_service.dto.response.PrenotazioneResponseSecondo;
import it.nesea.prenotazione_service.dto.response.PreventivoResponse;

public interface PrenotazioneService {
    PreventivoResponse richiediPreventivo(PreventivoRequest request);

    PrenotazioneResponse prenota(PrenotazioneRequest request);

    public PrenotazioneResponseSecondo prenotazione(PrenotazioneRequestSecondo request);
}

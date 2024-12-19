package it.nesea.prenotazione_service.service;

import it.nesea.prenotazione_service.dto.request.PreventivoRequest;
import it.nesea.prenotazione_service.dto.response.PreventivoResponse;

public interface PrenotazioneService {
    PreventivoResponse richiediPreventivo(PreventivoRequest request);
}

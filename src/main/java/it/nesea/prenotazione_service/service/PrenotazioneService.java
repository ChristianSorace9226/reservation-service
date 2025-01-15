package it.nesea.prenotazione_service.service;

import it.nesea.prenotazione_service.dto.request.ModificaPrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.PrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.PreventivoRequest;
import it.nesea.prenotazione_service.dto.response.PrenotazioneResponse;
import it.nesea.prenotazione_service.dto.response.PreventivoResponse;

public interface PrenotazioneService {

    PreventivoResponse richiediPreventivo(PreventivoRequest request);

    PrenotazioneResponse prenotazione(PrenotazioneRequest request);

    PrenotazioneResponse modificaPrenotazione(ModificaPrenotazioneRequest request);
}

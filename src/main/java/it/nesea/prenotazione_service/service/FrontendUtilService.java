package it.nesea.prenotazione_service.service;

import it.nesea.prenotazione_service.dto.request.FrontendMaggiorazioneRequest;
import it.nesea.prenotazione_service.dto.request.FrontendPrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.FrontendStagioneRequest;
import it.nesea.prenotazione_service.model.MaggiorazioneEntity;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.StagioneEntity;

import java.util.List;

public interface FrontendUtilService {

    List<Prenotazione> getPrenotazioni(FrontendPrenotazioneRequest frontendPrenotazioneRequest);

    List<StagioneEntity> getStagioni(FrontendStagioneRequest frontendStagioneRequest);

    List<MaggiorazioneEntity> getMaggiorazioni(FrontendMaggiorazioneRequest frontendMaggiorazioneRequest);
}

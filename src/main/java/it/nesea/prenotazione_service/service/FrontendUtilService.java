package it.nesea.prenotazione_service.service;

import it.nesea.prenotazione_service.model.MaggiorazioneEntity;
import it.nesea.prenotazione_service.model.PrenotazioneSave;
import it.nesea.prenotazione_service.model.StagioneEntity;

import java.util.List;

public interface FrontendUtilService {

    List<PrenotazioneSave> getAllPrenotazioni();

    List<StagioneEntity> getAllStagioni();

    List<MaggiorazioneEntity> getAllMaggiorazioni();
}

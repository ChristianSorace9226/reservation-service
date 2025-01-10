package it.nesea.prenotazione_service.service;

import it.nesea.prenotazione_service.dto.response.PreventivoResponse;
import it.nesea.prenotazione_service.model.MaggiorazioneEntity;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.Preventivo;
import it.nesea.prenotazione_service.model.StagioneEntity;

import java.util.List;

public interface FrontendUtilService {
    List<PreventivoResponse> getAllPreventivi();
    List<Prenotazione> getAllPrenotazioni();

    List<StagioneEntity> getAllStagioni();

    List<MaggiorazioneEntity> getAllMaggiorazioni();
}

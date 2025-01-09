package it.nesea.prenotazione_service.service;

import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.Preventivo;

import java.util.List;

public interface FrontendUtilService {
    List<Preventivo> getAllPreventivi();
    List<Prenotazione> getAllPrenotazioni();
}

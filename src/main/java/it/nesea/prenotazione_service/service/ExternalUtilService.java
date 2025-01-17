package it.nesea.prenotazione_service.service;

import it.nesea.albergo.common_lib.dto.InfoPrenotazione;

import java.util.List;

public interface ExternalUtilService {

    List<String> getCamerePrenotateOggi();

    InfoPrenotazione getInfoPrenotazione(Integer idPrenotazione);
}

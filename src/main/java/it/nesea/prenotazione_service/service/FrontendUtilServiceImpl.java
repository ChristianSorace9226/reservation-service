package it.nesea.prenotazione_service.service;

import it.nesea.prenotazione_service.dto.request.FrontendMaggiorazioneRequest;
import it.nesea.prenotazione_service.dto.request.FrontendMetodoPagamentoRequest;
import it.nesea.prenotazione_service.dto.request.FrontendPrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.FrontendStagioneRequest;
import it.nesea.prenotazione_service.model.MaggiorazioneEntity;
import it.nesea.prenotazione_service.model.MetodoPagamentoEntity;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.StagioneEntity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static it.nesea.prenotazione_service.util.Util.filtraRichiesta;

@Service
@AllArgsConstructor
public class FrontendUtilServiceImpl implements FrontendUtilService {

    private final EntityManager entityManager;

    @Override
    public List<Prenotazione> getPrenotazioni(FrontendPrenotazioneRequest request) {
        return filtraRichiesta(request, Prenotazione.class, entityManager);
    }

    @Override
    public List<StagioneEntity> getStagioni(FrontendStagioneRequest request) {
        return filtraRichiesta(request, StagioneEntity.class, entityManager);
    }

    @Override
    public List<MaggiorazioneEntity> getMaggiorazioni(FrontendMaggiorazioneRequest request) {
        return filtraRichiesta(request, MaggiorazioneEntity.class, entityManager);
    }

    @Override
    public List<MetodoPagamentoEntity> getMetodiPagamento(FrontendMetodoPagamentoRequest request) {
        return filtraRichiesta(request, MetodoPagamentoEntity.class, entityManager);
    }
}

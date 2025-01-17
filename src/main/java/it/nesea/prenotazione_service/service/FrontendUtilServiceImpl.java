package it.nesea.prenotazione_service.service;

import it.nesea.prenotazione_service.dto.request.FrontendMaggiorazioneRequest;
import it.nesea.prenotazione_service.dto.request.FrontendPrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.FrontendStagioneRequest;
import it.nesea.prenotazione_service.model.MaggiorazioneEntity;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.StagioneEntity;
import it.nesea.prenotazione_service.model.repository.PrenotazioneRepository;
import it.nesea.prenotazione_service.util.Util;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.util.List;

import static it.nesea.prenotazione_service.util.Util.filtraRichiesta;

@Service
public class FrontendUtilServiceImpl implements FrontendUtilService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final EntityManager entityManager;

    public FrontendUtilServiceImpl(PrenotazioneRepository prenotazioneRepository, EntityManager entityManager) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<Prenotazione> getPrenotazioni(FrontendPrenotazioneRequest request) {
        List<Prenotazione> prenotazioni = prenotazioneRepository.findAll();
        return filtraRichiesta(request, prenotazioni, Prenotazione.class);
    }


    public List<StagioneEntity> getStagioni(FrontendStagioneRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StagioneEntity> query = cb.createQuery(StagioneEntity.class);
        Root<StagioneEntity> root = query.from(StagioneEntity.class);
        query.select(root);
        List<StagioneEntity> stagioni = entityManager.createQuery(query).getResultList();
        return filtraRichiesta(request, stagioni, StagioneEntity.class);
    }

    public List<MaggiorazioneEntity> getMaggiorazioni(FrontendMaggiorazioneRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MaggiorazioneEntity> query = cb.createQuery(MaggiorazioneEntity.class);
        Root<MaggiorazioneEntity> root = query.from(MaggiorazioneEntity.class);
        query.select(root);
        List<MaggiorazioneEntity> maggiorazioni = entityManager.createQuery(query).getResultList();
        return filtraRichiesta(request, maggiorazioni, MaggiorazioneEntity.class);
    }


}

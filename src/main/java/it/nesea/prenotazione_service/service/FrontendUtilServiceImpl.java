package it.nesea.prenotazione_service.service;

import it.nesea.albergo.common_lib.exception.NotFoundException;
import it.nesea.prenotazione_service.model.MaggiorazioneEntity;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.StagioneEntity;
import it.nesea.prenotazione_service.model.repository.PrenotazioneRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrontendUtilServiceImpl implements FrontendUtilService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final EntityManager entityManager;

    public FrontendUtilServiceImpl(PrenotazioneRepository prenotazioneRepository, EntityManager entityManager) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<Prenotazione> getAllPrenotazioni() {
        List<Prenotazione> prenotazioni = prenotazioneRepository.findAll();
        if (prenotazioni.isEmpty()) {
            throw new NotFoundException("Nessun preventivo trovato");
        }
        return prenotazioni;
    }

    public List<StagioneEntity> getAllStagioni() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StagioneEntity> query = cb.createQuery(StagioneEntity.class);
        Root<StagioneEntity> root = query.from(StagioneEntity.class);
        query.select(root);
        List<StagioneEntity> stagioni = entityManager.createQuery(query).getResultList();
        if (stagioni.isEmpty()) {
            throw new NotFoundException("Nessuna stagione trovata");
        }
        return stagioni;
    }

    public List<MaggiorazioneEntity> getAllMaggiorazioni() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MaggiorazioneEntity> query = cb.createQuery(MaggiorazioneEntity.class);
        Root<MaggiorazioneEntity> root = query.from(MaggiorazioneEntity.class);
        query.select(root);
        List<MaggiorazioneEntity> maggiorazioni = entityManager.createQuery(query).getResultList();
        if (maggiorazioni.isEmpty()) {
            throw new NotFoundException("Nessuna maggiorazione trovata");
        }
        return maggiorazioni;
    }


}

package it.nesea.prenotazione_service.service;

import it.nesea.albergo.common_lib.exception.NotFoundException;
import it.nesea.prenotazione_service.dto.response.PreventivoResponse;
import it.nesea.prenotazione_service.mapper.PreventivoMapper;
import it.nesea.prenotazione_service.model.MaggiorazioneEntity;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.Preventivo;
import it.nesea.prenotazione_service.model.StagioneEntity;
import it.nesea.prenotazione_service.model.repository.PrenotazioneRepository;
import it.nesea.prenotazione_service.model.repository.PreventivoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FrontendUtilServiceImpl implements FrontendUtilService {

    private final PreventivoRepository preventivoRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final EntityManager entityManager;
    private final PreventivoMapper preventivoMapper;

    public FrontendUtilServiceImpl(PreventivoRepository preventivoRepository, PrenotazioneRepository prenotazioneRepository, EntityManager entityManager, PreventivoMapper preventivoMapper) {
        this.preventivoRepository = preventivoRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.entityManager = entityManager;
        this.preventivoMapper = preventivoMapper;
    }

    public List<PreventivoResponse> getAllPreventivi() {
        List<Preventivo> preventivi = preventivoRepository.findAll();
        if (preventivi.isEmpty()) {
            throw new NotFoundException("Nessun preventivo trovato");
        }
        List<PreventivoResponse> preventiviResponse = new ArrayList<>();
        for (Preventivo preventivo : preventivi) {
            preventiviResponse.add(preventivoMapper.fromPreventivoToPreventivoResponse(preventivo));
        }
        return preventiviResponse;
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

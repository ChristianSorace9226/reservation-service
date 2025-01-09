package it.nesea.prenotazione_service.service;

import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.Preventivo;
import it.nesea.prenotazione_service.model.repository.PrenotazioneRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExternalUtilServiceImpl implements ExternalUtilService {

    private final EntityManager entityManager;

    public ExternalUtilServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<String> getCamerePrenotateOggi() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class); // Specifica il tipo di risultato
        Root<Preventivo> root = cq.from(Preventivo.class);

        Predicate prenotatoPredicate = cb.isTrue(root.get("prenotato"));
        Predicate dataInclusaPredicate = cb.and(
                cb.lessThanOrEqualTo(root.get("checkIn"), LocalDateTime.now()),
                cb.greaterThanOrEqualTo(root.get("checkOut"), LocalDateTime.now())
        );

        cq.select(root.get("numeroCamera")).where(cb.and(prenotatoPredicate, dataInclusaPredicate));

        return entityManager.createQuery(cq).getResultList();
    }

}
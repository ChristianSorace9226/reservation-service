package it.nesea.prenotazione_service.service;

import it.nesea.albergo.common_lib.dto.InfoPrenotazione;
import it.nesea.albergo.common_lib.exception.NotFoundException;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.repository.PrenotazioneRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ExternalUtilServiceImpl implements ExternalUtilService {

    private final EntityManager entityManager;
    private final PrenotazioneRepository prenotazioneRepository;


    public List<String> getCamerePrenotateOggi() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Prenotazione> root = cq.from(Prenotazione.class);

        Predicate dataInclusaPredicate = cb.and(
                cb.lessThanOrEqualTo(root.get("checkIn"), LocalDateTime.now()),
                cb.greaterThanOrEqualTo(root.get("checkOut"), LocalDateTime.now())
        );

        cq.select(root.get("numeroCamera")).where(dataInclusaPredicate);

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public InfoPrenotazione getInfoPrenotazione(Integer idPrenotazione) {
        log.info("Oggetto request in input: {}", idPrenotazione);
        Prenotazione prenotazione = prenotazioneRepository.findById(idPrenotazione).orElseThrow(
                () -> new NotFoundException("Prenotazione non trovata"));

        InfoPrenotazione infoPrenotazione = new InfoPrenotazione();
        infoPrenotazione.setPrezzoTotale(prenotazione.getPrezzoTotale());
        infoPrenotazione.setIdUtente(prenotazione.getIdUtente());
        infoPrenotazione.setIdMetodoPagamento(prenotazione.getIdMetodoPagamento());
        infoPrenotazione.setCheckIn(LocalDate.from(prenotazione.getCheckIn()));

        return infoPrenotazione;
    }

}

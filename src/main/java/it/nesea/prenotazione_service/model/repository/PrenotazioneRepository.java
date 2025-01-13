package it.nesea.prenotazione_service.model.repository;

import it.nesea.prenotazione_service.model.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Integer> {
    Optional<Prenotazione> findByGroupId(String groupId);
}

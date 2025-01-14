package it.nesea.prenotazione_service.model.repository;

import it.nesea.prenotazione_service.model.PrenotazioneSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrenotazioneSaveRepository extends JpaRepository<PrenotazioneSave, Integer> {
    Optional<PrenotazioneSave> findByGroupId(String groupId);
}

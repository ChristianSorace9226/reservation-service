package it.nesea.prenotazione_service.model.repository;

import it.nesea.prenotazione_service.model.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Integer> {
    List<Prenotazione> findByGroupId(String groupId);

    List<Prenotazione> findByNumeroCamera(String numeroCamera);

}

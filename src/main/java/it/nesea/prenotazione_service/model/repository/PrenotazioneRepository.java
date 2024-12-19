package it.nesea.prenotazione_service.model.repository;

import it.nesea.prenotazione_service.model.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione,Integer> {

}

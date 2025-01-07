package it.nesea.prenotazione_service.model.repository;

import it.nesea.prenotazione_service.model.Preventivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreventivoRepository extends JpaRepository<Preventivo, Integer> {
}

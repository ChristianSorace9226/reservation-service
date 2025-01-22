package it.nesea.prenotazione_service.model.repository;

import it.nesea.prenotazione_service.model.MetodoPagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetodoPagamentoRepository extends JpaRepository<MetodoPagamentoEntity, Integer> {
}

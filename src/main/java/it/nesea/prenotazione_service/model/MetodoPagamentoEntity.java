package it.nesea.prenotazione_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "METODO_PAGAMENTO", schema = "PRENOTAZIONE_SERVICE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MetodoPagamentoEntity {

    @Id
    @Column(name = "ID", nullable = false, precision = 4)
    Integer id;

    @Column(name = "METODO_PAGAMENTO", nullable = false)
    String metodoPagamento;

}
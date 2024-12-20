package it.nesea.prenotazione_service.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "PREVENTIVO", schema = "PRENOTAZIONE_SERVICE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Preventivo implements Serializable {

    @Serial
    private static final long serialVersionUID = -5369174195463103251L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "preventivoGenerator")
    @SequenceGenerator(name = "preventivoGenerator", schema = "prenotazione_service", sequenceName = "seq_preventivo", allocationSize = 1)
    @Column(name = "ID", nullable = false, precision = 4)
    Integer id;

    @Column(name = "ID_TIPO_CAMERA", nullable = false)
    Integer idTipoCamera;

    @Column(name = "LISTA_ID_FASCIA_ETA")
    List<Integer> listaIdFasciaEta;

    @Column(name = "CHECK_IN", nullable = false)
    LocalDateTime checkIn;

    @Column(name = "CHECK_OUT", nullable = false)
    LocalDateTime checkOut;

    @Column(name = "ID_PREZZO_CAMERA", nullable = false)
    BigDecimal prezzoCamera;
}

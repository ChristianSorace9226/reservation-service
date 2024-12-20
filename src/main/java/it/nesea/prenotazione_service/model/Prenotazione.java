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
@Table(name = "PRENOTAZIONE", schema = "PRENOTAZIONE_SERVICE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Prenotazione implements Serializable {
    @Serial
    private static final long serialVersionUID = 3049426558672950163L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prenotazioneGenerator")
    @SequenceGenerator(name = "prenotazioneGenerator", schema = "prenotazione_service", sequenceName = "seq_prenotazione", allocationSize = 1)
    @Column(name = "ID", nullable = false, precision = 4)
    Integer id;

    @Column(name = "CODICE_PRENOTAZIONE", nullable = false, length = 16)
    String codicePrenotazione;

    @Column(name = "ID_UTENTE", nullable = false, precision = 4)
    Integer idUtente;

    @Column(name = "GROUP_ID", nullable = false, length = 50)
    String groupId;

    @Column(name = "ID_METODO_PAGAMENTO", nullable = false, precision = 1)
    Integer idMetodoPagamento;

    @ManyToOne
    @JoinColumn(name = "ID_PREVENTIVO", referencedColumnName = "ID")
    Preventivo preventivo;
}

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
public class PrenotazioneSave implements Serializable {

    @Serial
    private static final long serialVersionUID = 108148518985424904L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prenotazioneGenerator")
    @SequenceGenerator(name = "prenotazioneGenerator", schema = "prenotazione_service", sequenceName = "seq_prenotazione", allocationSize = 1)
    @Column(name = "ID", nullable = false, precision = 4)
    Integer id;

    @Column(name = "ID_UTENTE", nullable = false, precision = 4)
    Integer idUtente;

    @Column(name = "ID_METODO_PAGAMENTO", nullable = false, precision = 1)
    Integer idMetodoPagamento;

    @Column(name = "GROUP_ID", nullable = false)
    String groupId;

    @Column(name = "NUMERO_CAMERA", nullable = false, length = 2)
    String numeroCamera;

    @Column(name = "ID_TIPO_CAMERA")
    Integer idTipoCamera;

    @Column(name = "CHECK_IN", nullable = false)
    LocalDateTime checkIn;

    @Column(name = "CHECK_OUT", nullable = false)
    LocalDateTime checkOut;

    @Column(name = "LISTA_ETA", nullable = false)
    List<Integer> listaEta;

    @Column(name = "PREZZO_TOTALE", nullable = false)
    BigDecimal prezzoTotale;

    @Column(name = "PREZZI_A_PERSONA", nullable = false)
    List<BigDecimal> prezziAPersona;

    @Column(name = "CODICE_PRENOTAZIONE", nullable = false)
    String codicePrenotazione;
}
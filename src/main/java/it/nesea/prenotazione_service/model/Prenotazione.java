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

    @Column(name = "CODICE_PRENOTAZIONE", nullable = false, precision = 16)
    String codicePrenotazione;

    @Column(name = "FASCIA_ETA", nullable = false)
    List<Integer> listaIdFasciaEta;

    @Column(name = "ID_TIPO_STANZA", nullable = false, precision = 1)
    Integer tipoStanza;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CHECK_IN", nullable = false)
    LocalDateTime checkIn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CHECK_OUT", nullable = false)
    LocalDateTime checkOut;

    @Column(name = "ID_UTENTE", nullable = false, precision = 4)
    Integer idUtente;

    @Column(name = "GROUP_ID", nullable = false)
    String groupId;

    @Column(name = "PREZZO_CAMERA", nullable = false)
    BigDecimal prezzoCamera;

    @Column(name = "METODO_PAGAMENTO", nullable = false, precision = 1)
    Integer idMetodoPagamento; //todo: fara riferimento alla tabella del microservizio "pagamento"












}

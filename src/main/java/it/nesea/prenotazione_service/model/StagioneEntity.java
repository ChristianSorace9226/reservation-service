package it.nesea.prenotazione_service.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "STAGIONE", schema = "PRENOTAZIONE_SERVICE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StagioneEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5609961195369560239L;

    @Id
    @Column(name = "ID", nullable = false, precision = 4)
    Integer id;

    @Column(name = "NOME", nullable = false, length = 30)
    String nome;

    @Column(name = "GIORNO_INIZIO", nullable = false)
    LocalDate giornoInizio;

    @Column(name = "GIORNO_FINE", nullable = false)
    LocalDate giornoFine;

    @OneToOne
    @JoinColumn(name = "MAGGIORAZIONE_ID", nullable = false, referencedColumnName = "ID")
    MaggiorazioneEntity maggiorazione;


}

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

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "MAGGIORAZIONE", schema = "PRENOTAZIONE_SERVICE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaggiorazioneEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -8503336457227457026L;

    @Id
    @Column(name = "ID", nullable = false, precision = 4)
    Integer id;

    @Column(name = "PERCENTUALE_MAGGIORAZIONE", nullable = false)
    Integer percentualeMaggiorazione;

}

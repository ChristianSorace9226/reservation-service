package it.nesea.prenotazione_service.dto.request;

import it.nesea.prenotazione_service.model.MaggiorazioneEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FrontendStagioneRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -4856244780155778889L;

    Integer id;
    String nome;
    LocalDate giornoInizio;
    LocalDate giornoFine;
    MaggiorazioneEntity maggiorazione;
}

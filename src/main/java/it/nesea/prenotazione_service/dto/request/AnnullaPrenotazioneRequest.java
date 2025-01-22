package it.nesea.prenotazione_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnnullaPrenotazioneRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1910298696306268278L;

    @NotNull(message = "è richiesto l'id della prenotazione da annullare")
    Integer idPrenotazione;

    @NotNull(message = "e richiesto l'id dell'utente che vuole annullare la prenotazione")
    Integer idUtente;

    @NotNull(message = "è necessario il motivo dell'annullamento")
    String motivoAnnullamento;

    Boolean cancellazioneLogica;

}

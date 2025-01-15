package it.nesea.prenotazione_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnnullaPrenotazioneRequest {

    @NotNull(message = "è richiesto l'id della prenotazione da annullare")
    Integer id;

    @NotNull(message = "e richiesto l'id dell'utente che vuole annullare la prenotazione")
    Integer idUtente;

    @NotNull(message = "è necessario il motivo dell'annullamento")
    String motivoAnnullamento;

    Boolean cancellazioneLogica;

}

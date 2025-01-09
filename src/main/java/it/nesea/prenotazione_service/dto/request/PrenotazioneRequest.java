package it.nesea.prenotazione_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class PrenotazioneRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1190685920919258409L;

    @NotNull(message = "devi specificare l'id utente per la prenotazione")
    Integer idUtente;

    @NotNull(message = "inserisci l'id del tuo preventivo")
    Integer idPreventivo;

    @Min(value = 1)
    @Max(value = 2)
    @NotNull(message = "scegli il metodo di pagamento tra 1 e 2")
    Integer idMetodoPagamento;

}

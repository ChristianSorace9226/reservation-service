package it.nesea.prenotazione_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrenotazioneRequestSecondo extends PreventivoRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 5454354981169351608L;

    @NotNull(message = "devi specificare l'id utente per la prenotazione")
    Integer idUtente;

    @Min(value = 1)
    @Max(value = 2)
    @NotNull(message = "scegli il metodo di pagamento tra 1 e 2")
    Integer idMetodoPagamento;

    String groupId;
}


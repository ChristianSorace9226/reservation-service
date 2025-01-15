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
public class ModificaPrenotazioneRequest extends PreventivoRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -4222705413418297092L;

    @NotNull(message = "L'id della prenotazione è necessario per modificare")
    Integer id;

    @Min(value = 1)
    @Max(value = 2)
    @NotNull(message = "scegli il metodo di pagamento tra 1 e 2")
    Integer idMetodoPagamento;


}

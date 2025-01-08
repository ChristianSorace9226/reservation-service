package it.nesea.prenotazione_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PreventivoRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -1036202629981122611L;

    @Min(value = 1)
    @Max(value = 4)
    Integer idTipoCamera;

    @NotNull(message = "le fasce età delle persone non devono essere null")
    @NotEmpty(message = "le fasce età delle persone devono essere fornite")
    List<Integer> listaIdFasciaEta;

    @NotNull(message = "è necessaria la data del check-in")
    LocalDateTime checkIn;

    @NotNull(message = "è necessaria la data del check-out")
    LocalDateTime checkOut;

    String groupId;

}

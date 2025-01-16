package it.nesea.prenotazione_service.dto.request;

import it.nesea.albergo.common_lib.dto.PrezzoCameraDTO;
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

    @NotNull(message = "è necessaria la data del check-in")
    LocalDateTime checkIn;

    @NotNull(message = "è necessaria la data del check-out")
    LocalDateTime checkOut;

    @NotNull(message = "è necessaria la lista di età")
    List<Integer> listaEta;

    @NotNull(message = "è richiesto il prezzario per poter procedere al preventivo")
    PrezzoCameraDTO prezzarioCamera;

}

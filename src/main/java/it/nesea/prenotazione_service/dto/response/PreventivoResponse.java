package it.nesea.prenotazione_service.dto.response;

import it.nesea.albergo.common_lib.dto.PrezzoCameraDTO;
import it.nesea.albergo.common_lib.exception.BadRequestException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class PreventivoResponse extends PrezzoCameraDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1564505570856586138L;

}

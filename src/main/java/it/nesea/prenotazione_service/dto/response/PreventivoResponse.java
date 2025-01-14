package it.nesea.prenotazione_service.dto.response;

import it.nesea.albergo.common_lib.dto.PrezzoCameraDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class PreventivoResponse extends PrezzoCameraDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1564505570856586138L;

}

package it.nesea.prenotazione_service.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrenotazioneResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -7256012000844387838L;


    String codicePrenotazione;
    String groupId;
    List<BigDecimal> prezziAPersona;
    BigDecimal prezzoCamera;
}

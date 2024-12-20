package it.nesea.prenotazione_service.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrenotazioneResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 5806744543180005840L;

    String codicePrenotazione;
    String groupId;
    BigDecimal prezzoCamera;


}

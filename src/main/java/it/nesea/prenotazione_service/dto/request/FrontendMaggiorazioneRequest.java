package it.nesea.prenotazione_service.dto.request;

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
public class FrontendMaggiorazioneRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 171939184456672044L;

    Integer id;
    Integer percentualeMaggiorazione;

}

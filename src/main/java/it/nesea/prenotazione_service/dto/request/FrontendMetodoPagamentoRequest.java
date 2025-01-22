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
public class FrontendMetodoPagamentoRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 597977585880972438L;

    Integer id;
    String metodoPagamento;


}

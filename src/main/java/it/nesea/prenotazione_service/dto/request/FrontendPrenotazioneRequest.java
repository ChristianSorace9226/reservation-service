package it.nesea.prenotazione_service.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FrontendPrenotazioneRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -7744186859067756206L;

    Integer id;
    Integer idUtente;
    Integer idMetodoPagamento;
    String groupId;
    String numeroCamera;
    Integer idTipoCamera;
    LocalDateTime checkIn;
    LocalDateTime checkOut;
    List<Integer> listaEta;
    BigDecimal prezzoTotale;
    List<BigDecimal> prezziAPersona;
    String codicePrenotazione;
    LocalDateTime dataCreazione;
    LocalDateTime dataAnnullamento;
    String motivoAnnullamento;

}

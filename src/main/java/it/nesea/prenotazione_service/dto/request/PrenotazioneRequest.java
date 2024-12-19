package it.nesea.prenotazione_service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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
public class PrenotazioneRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1190685920919258409L;

    @NotNull(message = "devi specificare l'id utente per la prenotazione")
    Integer idUtente;

    @NotNull(message = "devi specificare che tipo di stanza vuoi prenotare")
    Integer idTipoStanza;

    @NotNull(message = "le fasce età delle persone non devono essere null")
    @NotEmpty(message = "le fasce età delle persone devono essere fornite")
    List<Integer> listaIdFasciaEta;

    @Null
    String groupId;

    @NotNull(message = "è necessaria la data del check-in")
    LocalDateTime checkIn;

    @NotNull(message = "è necessaria la data del check-out")
    LocalDateTime checkOut;

    @NotNull(message = "scegli il metodo di pagamento")
    Integer idMetodoPagamento;




}

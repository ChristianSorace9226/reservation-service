package it.nesea.prenotazione_service.dto.response;

import it.nesea.albergo.common_lib.exception.BadRequestException;
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

    public static class PrenotazioneResponseBuilder {
        private String codicePrenotazione;
        private String groupId;
        private BigDecimal prezzoCamera;

        public PrenotazioneResponseBuilder groupId(String groupId) {
            if (groupId == null || groupId.isEmpty()) {
                throw new BadRequestException("Il dato fornito è null o vuoto");
            }
            this.groupId = groupId;
            return this;
        }

        public PrenotazioneResponseBuilder codicePrenotazione(String codicePrenotazione) {
            if (codicePrenotazione == null) {
                throw new BadRequestException("Il dato fornito è vuoto o null");
            }
            this.codicePrenotazione = codicePrenotazione;
            return this;
        }

        public PrenotazioneResponseBuilder prezzoCamera(BigDecimal prezzoCamera) {
            if (prezzoCamera == null) {
                throw new BadRequestException("Il dato fornito è null");
            }
            this.prezzoCamera = prezzoCamera;
            return this;
        }

        public PrenotazioneResponse build() {
            PrenotazioneResponse prenotazioneResponse = new PrenotazioneResponse();
            prenotazioneResponse.codicePrenotazione = this.codicePrenotazione;
            prenotazioneResponse.groupId = this.groupId;
            prenotazioneResponse.prezzoCamera = this.prezzoCamera;
            return prenotazioneResponse;
        }
    }

}

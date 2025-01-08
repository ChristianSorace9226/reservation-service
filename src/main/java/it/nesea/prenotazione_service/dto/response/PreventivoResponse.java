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
@AllArgsConstructor
@NoArgsConstructor
public class PreventivoResponse extends PrezzoCameraDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1564505570856586138L;

    Integer id;

    private PreventivoResponse(PreventivoResponseBuilder preventivoResponseBuilder) {
        super(preventivoResponseBuilder.numeroCamera, preventivoResponseBuilder.idTipo,
                preventivoResponseBuilder.prezzoTotale, preventivoResponseBuilder.prezziAPersona, preventivoResponseBuilder.numeroOccupanti);
        this.id = preventivoResponseBuilder.id;
    }

    public static class PreventivoResponseBuilder {
        private Integer id;
        private String numeroCamera;
        private Integer idTipo;
        private BigDecimal prezzoTotale;
        private List<BigDecimal> prezziAPersona;
        private Integer numeroOccupanti;

        public PreventivoResponseBuilder id(Integer id) {
            if (id == null) {
                throw new BadRequestException("Il dato fornito è null o vuoto");
            }
            this.id = id;
            return this;
        }

        public PreventivoResponseBuilder numeroCamera(String numeroCamera) {
            if (numeroCamera == null || numeroCamera.isEmpty()) {
                throw new BadRequestException("Il dato fornito è null o vuoto");
            }
            this.numeroCamera = numeroCamera;
            return this;
        }

        public PreventivoResponseBuilder idTipo(Integer idTipo) {
            if (idTipo == null) {
                throw new BadRequestException("Il dato fornito è null o vuoto");
            }
            this.idTipo = idTipo;
            return this;
        }

        public PreventivoResponseBuilder prezzoTotale(BigDecimal prezzoTotale) {
            if (prezzoTotale == null) {
                throw new BadRequestException("Il dato fornito è null o vuoto");
            }
            this.prezzoTotale = prezzoTotale;
            return this;
        }

        public PreventivoResponseBuilder prezziAPersona(List<BigDecimal> prezziAPersona) {
            if (prezziAPersona == null || prezziAPersona.isEmpty()) {
                throw new BadRequestException("La lista dei prezzi per persona è null o vuota");
            }
            this.prezziAPersona = prezziAPersona;
            return this;
        }

        public PreventivoResponseBuilder numeroOccupanti(Integer numeroOccupanti) {
            if (numeroOccupanti == null) {
                throw new BadRequestException("Il dato fornito è null o vuoto");
            }
            this.numeroOccupanti = numeroOccupanti;
            return this;
        }

        public PreventivoResponse build() {
            if (idTipo == null || numeroCamera == null || prezzoTotale == null ||
                    id == null || prezziAPersona == null || prezziAPersona.isEmpty() || numeroOccupanti == null) {
                throw new IllegalStateException("Campi obbligatori mancanti");
            }

            return new PreventivoResponse(this);
        }
    }
}

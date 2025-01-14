package it.nesea.prenotazione_service.mapper;

import it.nesea.albergo.common_lib.dto.PrezzoCameraDTO;
import it.nesea.prenotazione_service.dto.request.PrenotazioneRequest;
import it.nesea.prenotazione_service.dto.response.PrenotazioneResponse;
import it.nesea.prenotazione_service.model.PrenotazioneSave;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class PrenotazioneMapper {


    @Mapping(source = "prezzoTotale", target = "prezzoCamera")
    public abstract PrenotazioneResponse fromPrenotazioneRequestToPrenotazioneResponse(PrenotazioneSave prenotazioneSave);

    @Mapping(source = "prezzarioCamera", target = "idTipoCamera")
    @Mapping(source = "prezzarioCamera", target = "prezzoTotale")
    @Mapping(source = "prezzarioCamera", target = "prezziAPersona")
    @Mapping(source = "prezzarioCamera", target = "numeroCamera")
    public abstract PrenotazioneSave fromPrenotazioneRequestToPrenotazione(PrenotazioneRequest prenotazioneRequest);

    public Integer getIdTipoCamera(PrezzoCameraDTO prezzoCameraDTO) {
        return prezzoCameraDTO.getIdTipo();
    }

    public BigDecimal getPrezzoCamera(PrezzoCameraDTO prezzoCameraDTO) {
        return prezzoCameraDTO.getPrezzoTotale();
    }

    public List<BigDecimal> getPrezziAPersona(PrezzoCameraDTO prezzoCameraDTO) {
        return prezzoCameraDTO.getPrezziAPersona();
    }

    public String getNumeroCamera(PrezzoCameraDTO prezzoCameraDTO) {
        return prezzoCameraDTO.getNumeroCamera();
    }

}

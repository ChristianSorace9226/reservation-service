package it.nesea.prenotazione_service.mapper;

import it.nesea.albergo.common_lib.dto.InfoPrenotazione;
import it.nesea.albergo.common_lib.dto.PrezzoCameraDTO;
import it.nesea.prenotazione_service.dto.request.PrenotazioneRequest;
import it.nesea.prenotazione_service.dto.response.PrenotazioneResponse;
import it.nesea.prenotazione_service.model.Prenotazione;
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
    public abstract PrenotazioneResponse fromPrenotazioneToPrenotazioneResponse(Prenotazione prenotazione);

    @Mapping(source = "prezzarioCamera", target = "idTipoCamera")
    @Mapping(source = "prezzarioCamera", target = "prezzoTotale")
    @Mapping(source = "prezzarioCamera", target = "prezziAPersona")
    @Mapping(source = "prezzarioCamera", target = "numeroCamera")
    public abstract Prenotazione fromPrenotazioneRequestToPrenotazione(PrenotazioneRequest prenotazioneRequest);

    @Mapping(source = "id", target = "idPrenotazione")
    public abstract InfoPrenotazione fromPrenotazioneToInfoPrenotazione(Prenotazione prenotazione);

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

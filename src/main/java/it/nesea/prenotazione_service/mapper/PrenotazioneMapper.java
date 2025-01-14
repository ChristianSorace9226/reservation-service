package it.nesea.prenotazione_service.mapper;

import it.nesea.prenotazione_service.dto.request.PrenotazioneRequestSecondo;
import it.nesea.prenotazione_service.dto.request.PreventivoRequest;
import it.nesea.prenotazione_service.dto.response.PrenotazioneResponseSecondo;
import it.nesea.prenotazione_service.model.PrenotazioneSave;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class PrenotazioneMapper {
    public abstract PrenotazioneRequestSecondo fromPreventivoRequestToPrenotazioneRequest(PreventivoRequest preventivoRequest);

    public abstract PreventivoRequest fromPrenotazioneRequestSecondoToPreventivoRequest(PrenotazioneRequestSecondo prenotazioneRequest);

    public abstract PrenotazioneResponseSecondo fromPrenotazioneRequestToPrenotazioneResponse(PrenotazioneRequestSecondo prenotazioneRequest);

    public abstract PrenotazioneSave fromPrenotazioneRequestToPrenotazione(PrenotazioneRequestSecondo prenotazioneRequest);

}

package it.nesea.prenotazione_service.mapper;

import it.nesea.albergo.common_lib.dto.PrezzoCameraDTO;
import it.nesea.prenotazione_service.model.Preventivo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class PreventivoMapper {

    @Mapping(source = "idTipo", target = "idTipoCamera")
    public abstract Preventivo fromPrezzoCameraDTOToPreventivo(PrezzoCameraDTO prezzoCameraDTO);
}

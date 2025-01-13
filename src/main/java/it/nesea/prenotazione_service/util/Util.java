package it.nesea.prenotazione_service.util;

import it.nesea.albergo.common_lib.exception.BadRequestException;
import it.nesea.albergo.common_lib.exception.NotFoundException;
import it.nesea.prenotazione_service.dto.request.PreventivoRequest;
import it.nesea.prenotazione_service.model.Preventivo;
import it.nesea.prenotazione_service.model.StagioneEntity;
import it.nesea.prenotazione_service.model.repository.PreventivoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Component
public class Util {

    private final PreventivoRepository preventivoRepository;
    private final EntityManager entityManager;

    public Util(PreventivoRepository preventivoRepository, EntityManager entityManager) {
        this.preventivoRepository = preventivoRepository;
        this.entityManager = entityManager;
    }

    public String generaCodicePrenotazione() {
        return UUID.randomUUID().toString();
    }

    public String generaGroupId() {
        return UUID.randomUUID().toString().substring(0, 4);
    }

    public void isDateValid(LocalDate checkIn, LocalDate checkOut) {
        if (checkOut.isBefore(checkIn)) {
            throw new BadRequestException("Data check out antecedente a quella di check in");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new BadRequestException("Data check in non può essere nel futuro");
        }
    }

    public void prenotaPreventivo(Preventivo preventivoEsistente) {
        preventivoEsistente.setPrenotato(true);
        preventivoRepository.save(preventivoEsistente);
    }

    public long calcolaNumeroGiorni(LocalDate dataInizio, LocalDate dataFine) {
        if (dataInizio == null || dataFine == null) {
            throw new IllegalArgumentException("Le date di checkIn e checkOut non possono essere null");
        }
        return ChronoUnit.DAYS.between(dataInizio, dataFine);
    }

    public Integer getPercentualeMaggiorazione(LocalDate checkIn, LocalDate checkOut, List<StagioneEntity> stagioni) {

        int meseCheckIn = checkIn.getMonthValue();
        int giornoCheckIn = checkIn.getDayOfMonth();

        int meseCheckOut = checkOut.getMonthValue();
        int giornoCheckOut = checkOut.getDayOfMonth();

        Integer percentualeMaggiorazione = null;

        for (StagioneEntity stagione : stagioni) {
            int meseInizio = stagione.getGiornoInizio().getMonthValue();
            int giornoInizio = stagione.getGiornoInizio().getDayOfMonth();

            int meseFine = stagione.getGiornoFine().getMonthValue();
            int giornoFine = stagione.getGiornoFine().getDayOfMonth();

            boolean stagioneCoincidente = false;
            if (meseInizio < meseFine || (meseInizio == meseFine && giornoInizio <= giornoFine)) {
                if ((meseCheckIn > meseInizio || (meseCheckIn == meseInizio && giornoCheckIn >= giornoInizio)) &&
                        (meseCheckOut < meseFine || (meseCheckOut == meseFine && giornoCheckOut <= giornoFine))) {
                    stagioneCoincidente = true;
                }
            } else {
                if ((meseCheckIn > meseInizio || (meseCheckIn == meseInizio && giornoCheckIn >= giornoInizio)) ||
                        (meseCheckOut < meseFine || (meseCheckOut == meseFine && giornoCheckOut <= giornoFine))) {
                    stagioneCoincidente = true;
                }
            }

            if (stagioneCoincidente) {
                percentualeMaggiorazione = stagione.getMaggiorazione().getPercentualeMaggiorazione();
                break;
            }
        }

        if (percentualeMaggiorazione == null) {
            throw new NotFoundException("Nessuna stagione trovata per il periodo richiesto.");
        }
        return percentualeMaggiorazione;
    }


    public PreventivoRequest calcolaPrezzoFinale(PreventivoRequest request) {
        LocalDate checkIn = request.getCheckIn();
        LocalDate checkOut = request.getCheckOut();

        isDateValid(checkIn, checkOut);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StagioneEntity> percentualeMaggiorazioneQuery = cb.createQuery(StagioneEntity.class);
        Root<StagioneEntity> stagioneRoot = percentualeMaggiorazioneQuery.from(StagioneEntity.class);
        percentualeMaggiorazioneQuery.select(stagioneRoot);

        List<StagioneEntity> stagioni = entityManager.createQuery(percentualeMaggiorazioneQuery).getResultList();

        int percentualeMaggiorazione = getPercentualeMaggiorazione(checkIn, checkOut, stagioni);

        List<BigDecimal> prezziAPersona = request.getPrezzarioCamera().getPrezziAPersona();
        prezziAPersona.replaceAll(aPersona -> aPersona.multiply(BigDecimal.valueOf(1 + (percentualeMaggiorazione / 100.0))));

        request.getPrezzarioCamera().setPrezziAPersona(prezziAPersona);

        BigDecimal prezzoNuoviOccupanti = request.getPrezzarioCamera().getPrezzoTotale()
                .multiply(BigDecimal.valueOf(calcolaNumeroGiorni(checkIn, checkOut)));

        BigDecimal prezzoNuoviOccupantiConMaggiorazione = prezzoNuoviOccupanti
                .multiply(BigDecimal.valueOf(1 + (percentualeMaggiorazione / 100.0)));

        request.getPrezzarioCamera().setPrezzoTotale(prezzoNuoviOccupantiConMaggiorazione);
        return request;
    }
}

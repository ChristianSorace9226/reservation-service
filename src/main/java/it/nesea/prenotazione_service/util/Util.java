package it.nesea.prenotazione_service.util;

import it.nesea.albergo.common_lib.exception.BadRequestException;
import it.nesea.albergo.common_lib.exception.NotFoundException;
import it.nesea.prenotazione_service.model.Preventivo;
import it.nesea.prenotazione_service.model.StagioneEntity;
import it.nesea.prenotazione_service.model.repository.PreventivoRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Component
public class Util {

    private final PreventivoRepository preventivoRepository;

    public Util(PreventivoRepository preventivoRepository) {
        this.preventivoRepository = preventivoRepository;
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

    public long calcolaNumeroGiorni(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Le date di checkIn e checkOut non possono essere null");
        }
        return ChronoUnit.DAYS.between(checkIn, checkOut);
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


}

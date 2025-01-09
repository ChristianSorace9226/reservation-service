package it.nesea.prenotazione_service.util;

import it.nesea.albergo.common_lib.exception.BadRequestException;
import it.nesea.prenotazione_service.model.Preventivo;
import it.nesea.prenotazione_service.model.repository.PreventivoRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    public void isDateValid(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (checkOut.isBefore(checkIn)) {
            throw new BadRequestException("Data check out antecedente a quella di check in");
        }
        if (checkIn.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Data check in non può essere nel futuro");
        }
    }

    public void prenotaPreventivo(Preventivo preventivoEsistente) {
        preventivoEsistente.setPrenotato(true);
        preventivoRepository.save(preventivoEsistente);
    }


}

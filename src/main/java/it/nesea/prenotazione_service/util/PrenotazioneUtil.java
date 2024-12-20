package it.nesea.prenotazione_service.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PrenotazioneUtil {
    public String generaCodicePrenotazione() {
        return UUID.randomUUID().toString();
    }
}

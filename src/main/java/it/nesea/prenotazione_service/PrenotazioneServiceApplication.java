package it.nesea.prenotazione_service;

import it.nesea.prenotazione_service.controller.feign.HotelExternalController;
import it.nesea.prenotazione_service.controller.feign.UserExternalController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {HotelExternalController.class, UserExternalController.class})
public class PrenotazioneServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrenotazioneServiceApplication.class, args);
    }

}

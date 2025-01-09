package it.nesea.prenotazione_service.controller;

import it.nesea.albergo.common_lib.dto.response.CustomResponse;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.Preventivo;
import it.nesea.prenotazione_service.service.FrontendUtilService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/front-end-service")
public class FrontendServiceController {

    private final FrontendUtilService frontendUtilService;

    public FrontendServiceController(FrontendUtilService frontendUtilService) {
        this.frontendUtilService = frontendUtilService;
    }

    @GetMapping("/get-preventivi")
    public ResponseEntity<CustomResponse<List<Preventivo>>> getAllPreventivi() {
        return ResponseEntity.ok(CustomResponse.success(frontendUtilService.getAllPreventivi()));
    }

    @GetMapping("/get-prenotazioni")
    public ResponseEntity<CustomResponse<List<Prenotazione>>> getAllPrenotazioni() {
        return ResponseEntity.ok(CustomResponse.success(frontendUtilService.getAllPrenotazioni()));
    }

}

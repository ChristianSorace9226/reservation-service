package it.nesea.prenotazione_service.controller;

import it.nesea.albergo.common_lib.dto.response.CustomResponse;
import it.nesea.prenotazione_service.model.Preventivo;
import it.nesea.prenotazione_service.service.FrontendUtilService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FrontendServiceController {
    private final FrontendUtilService frontendUtilService;

    public FrontendServiceController(FrontendUtilService frontendUtilService) {
        this.frontendUtilService = frontendUtilService;
    }

    public ResponseEntity<CustomResponse<List<Preventivo>>> getAllPreventivi() {
        return ResponseEntity.ok(CustomResponse.success(frontendUtilService.getAllPreventivi()));
    }

}

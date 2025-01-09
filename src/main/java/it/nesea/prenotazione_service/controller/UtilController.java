package it.nesea.prenotazione_service.controller;

import it.nesea.albergo.common_lib.dto.response.CustomResponse;
import it.nesea.prenotazione_service.service.ExternalUtilService;
import it.nesea.prenotazione_service.service.ExternalUtilServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/util")
public class UtilController {
    private final ExternalUtilService externalUtilService;

    public UtilController(ExternalUtilService externalUtilService) {
        this.externalUtilService = externalUtilService;
    }

    @GetMapping("/get-camere-prenotate-oggi")
    public ResponseEntity<CustomResponse<List<String>>> getCamerePrenotateOggi(){
        return ResponseEntity.ok(CustomResponse.success(externalUtilService.getCamerePrenotateOggi()));
    }

}

package it.nesea.prenotazione_service.controller;

import it.nesea.albergo.common_lib.dto.InfoPrenotazione;
import it.nesea.albergo.common_lib.dto.response.CustomResponse;
import it.nesea.prenotazione_service.service.ExternalUtilService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/util")
public class ExternalUtilController {
    private final ExternalUtilService externalUtilService;

    public ExternalUtilController(ExternalUtilService externalUtilService) {
        this.externalUtilService = externalUtilService;
    }

    @GetMapping("/get-camere-prenotate-oggi")
    public ResponseEntity<CustomResponse<List<String>>> getCamerePrenotateOggi() {
        return ResponseEntity.ok(CustomResponse.success(externalUtilService.getCamerePrenotateOggi()));
    }

    @PostMapping("/get-info-prenotazione")
    public ResponseEntity<CustomResponse<InfoPrenotazione>> getInfoPrenotazione(@RequestBody Integer idPrenotazione) {
        return ResponseEntity.ok(CustomResponse.success(externalUtilService.getInfoPrenotazione(idPrenotazione)));
    }

}

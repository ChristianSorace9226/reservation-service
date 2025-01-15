package it.nesea.prenotazione_service.controller;

import it.nesea.albergo.common_lib.dto.response.CustomResponse;
import it.nesea.prenotazione_service.model.MaggiorazioneEntity;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.StagioneEntity;
import it.nesea.prenotazione_service.service.FrontendUtilService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/front-end-service")
@AllArgsConstructor
public class FrontendServiceController {

    private final FrontendUtilService frontendUtilService;


    @GetMapping("/get-prenotazioni")
    public ResponseEntity<CustomResponse<List<Prenotazione>>> getAllPrenotazioni() {
        return ResponseEntity.ok(CustomResponse.success(frontendUtilService.getAllPrenotazioni()));
    }

    @GetMapping("/get-stagioni")
    public ResponseEntity<CustomResponse<List<StagioneEntity>>> getAllStagioni() {
        return ResponseEntity.ok(CustomResponse.success(frontendUtilService.getAllStagioni()));
    }

    @GetMapping("/get-maggiorazioni")
    public ResponseEntity<CustomResponse<List<MaggiorazioneEntity>>> getAllMaggiorazioni() {
        return ResponseEntity.ok(CustomResponse.success(frontendUtilService.getAllMaggiorazioni()));
    }
}
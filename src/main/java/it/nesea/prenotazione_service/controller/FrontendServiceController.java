package it.nesea.prenotazione_service.controller;

import it.nesea.albergo.common_lib.dto.response.CustomResponse;
import it.nesea.prenotazione_service.dto.request.FrontendMaggiorazioneRequest;
import it.nesea.prenotazione_service.dto.request.FrontendMetodoPagamentoRequest;
import it.nesea.prenotazione_service.dto.request.FrontendPrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.FrontendStagioneRequest;
import it.nesea.prenotazione_service.model.MaggiorazioneEntity;
import it.nesea.prenotazione_service.model.MetodoPagamentoEntity;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.StagioneEntity;
import it.nesea.prenotazione_service.service.FrontendUtilService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/front-end-service")
@AllArgsConstructor
public class FrontendServiceController {

    private final FrontendUtilService frontendUtilService;

    @PostMapping("/get-prenotazioni")
    public ResponseEntity<CustomResponse<List<Prenotazione>>> getAllPrenotazioni(@RequestBody FrontendPrenotazioneRequest frontendPrenotazioneRequest) {
        return ResponseEntity.ok(CustomResponse.success(frontendUtilService.getPrenotazioni(frontendPrenotazioneRequest)));
    }

    @PostMapping("/get-stagioni")
    public ResponseEntity<CustomResponse<List<StagioneEntity>>> getAllStagioni(@RequestBody FrontendStagioneRequest frontendStagioneRequest) {
        return ResponseEntity.ok(CustomResponse.success(frontendUtilService.getStagioni(frontendStagioneRequest)));
    }

    @PostMapping("/get-maggiorazioni")
    public ResponseEntity<CustomResponse<List<MaggiorazioneEntity>>> getAllMaggiorazioni(@RequestBody FrontendMaggiorazioneRequest frontendMaggiorazioneRequest) {
        return ResponseEntity.ok(CustomResponse.success(frontendUtilService.getMaggiorazioni(frontendMaggiorazioneRequest)));
    }

    @PostMapping("/get-metodi-pagamento")
    public ResponseEntity<CustomResponse<List<MetodoPagamentoEntity>>> getAllMaggiorazioni(@RequestBody FrontendMetodoPagamentoRequest frontendMaggiorazioneRequest) {
        return ResponseEntity.ok(CustomResponse.success(frontendUtilService.getMetodiPagamento(frontendMaggiorazioneRequest)));
    }
}
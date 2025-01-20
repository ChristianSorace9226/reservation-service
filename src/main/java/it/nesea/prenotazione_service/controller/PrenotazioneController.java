package it.nesea.prenotazione_service.controller;


import it.nesea.albergo.common_lib.dto.response.CustomResponse;
import it.nesea.prenotazione_service.dto.request.AnnullaPrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.ModificaPrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.PrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.PreventivoRequest;
import it.nesea.prenotazione_service.dto.response.AnnullaPrenotazioneResponse;
import it.nesea.prenotazione_service.dto.response.PrenotazioneResponse;
import it.nesea.prenotazione_service.dto.response.PreventivoResponse;
import it.nesea.prenotazione_service.service.PrenotazioneService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/prenotazione")
@AllArgsConstructor
public class PrenotazioneController {

    private final PrenotazioneService prenotazioneService;

    @PostMapping(path = "/richiedi-preventivo")
    public ResponseEntity<CustomResponse<PreventivoResponse>> richiediPreventivo(@Valid @RequestBody PreventivoRequest request) {
        return ResponseEntity.ok(CustomResponse.success(prenotazioneService.richiediPreventivo(request)));
    }

    @PostMapping(path = "/prenota")
    public ResponseEntity<CustomResponse<PrenotazioneResponse>> prenota(@Valid @RequestBody PrenotazioneRequest request) {
        return ResponseEntity.ok(CustomResponse.success(prenotazioneService.prenotazione(request)));
    }

    @PutMapping(path = "/modifica-prenotazione")
    public ResponseEntity<CustomResponse<PrenotazioneResponse>> modificaPrenotazione(@Valid @RequestBody ModificaPrenotazioneRequest request) {
        return ResponseEntity.ok(CustomResponse.success(prenotazioneService.modificaPrenotazione(request)));
    }

    @DeleteMapping(path = "/annulla-prenotazione")
    public ResponseEntity<CustomResponse<AnnullaPrenotazioneResponse>> annullaPrenotazione(@Valid @RequestBody AnnullaPrenotazioneRequest request) {
        return ResponseEntity.ok(CustomResponse.success(prenotazioneService.annullaPrenotazione(request)));
    }

}

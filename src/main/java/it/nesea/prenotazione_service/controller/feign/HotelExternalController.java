package it.nesea.prenotazione_service.controller.feign;

import it.nesea.albergo.common_lib.dto.PrezzoCameraDTO;
import it.nesea.albergo.common_lib.dto.request.CheckDateStart;
import it.nesea.albergo.common_lib.dto.response.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "${external-call.name.hotel}", url = "${external-call.hotel.util.url}")
public interface HotelExternalController {

    @PostMapping("${external-call.hotel.util.getPrezzoCamera}")
    ResponseEntity<CustomResponse<List<PrezzoCameraDTO>>> getListaPrezzoCamera(@RequestBody List<Integer> listaEta);

    @PostMapping("${external-call.hotel.util.checkDisponibilita}")
    ResponseEntity<CustomResponse<List<Boolean>>> checkDisponibilita(@RequestBody CheckDateStart checkDateStart);

}

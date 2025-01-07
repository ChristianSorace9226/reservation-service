package it.nesea.prenotazione_service.controller.feign;

import it.nesea.albergo.common_lib.dto.PrezzoCameraDTO;
import it.nesea.albergo.common_lib.dto.response.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "${external-call.name.reservation}", url = "${external-call.util.url}")
public interface ExternalController {

    @PostMapping("${external-call.util.getPrezzoCamera}")
    ResponseEntity<CustomResponse<List<PrezzoCameraDTO>>> getListaPrezzoCamera(@RequestBody List<Integer> listaEta);

}

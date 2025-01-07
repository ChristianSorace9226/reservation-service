package it.nesea.prenotazione_service.controller.feign;

import it.nesea.albergo.common_lib.dto.response.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${external-call.name.user}", url = "${external-call.user.util.url}")
public interface UserExternalController {

    @GetMapping("${external-call.user.util.exists}")
    ResponseEntity<CustomResponse<Boolean>> checkUtente(@RequestParam Integer idUtente);
}

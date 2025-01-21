package it.nesea.prenotazione_service.controller.feign;

import it.nesea.albergo.common_lib.dto.request.RichiediRimborso;
import it.nesea.albergo.common_lib.dto.response.CustomResponse;
import it.nesea.albergo.common_lib.dto.response.RimborsoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${external-call.name.pagamento}", url = "${external-call.pagamento.url}")
public interface PagamentoExternalController {

    @PostMapping("${external-call.pagamento.acquisisciRimborso}")
    ResponseEntity<CustomResponse<RimborsoResponse>> richiediRimborso(@RequestBody RichiediRimborso rimborso);


}

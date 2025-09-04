package bo.edu.ucb.ms.sales.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "accounting")
public interface RegisterJournalService {
    @PostMapping("api/journal/sale")
    ResponseEntity<String> registerSale(Map<String, Object> saleData);
}

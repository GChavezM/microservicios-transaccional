package bo.edu.ucb.ms.sales.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "warehouse")
public interface ProductStockService {
    @GetMapping("api/stock/check")
    Boolean checkStock(@RequestParam("productId") Integer productId, @RequestParam("quantity") Integer quantity);

    @GetMapping("api/product/{productId}")
    Map<String, String> getProductById(@PathVariable("productId") Integer productId);

    @PostMapping("api/stock/decrease")
    void decreaseStock(@RequestParam("productId") Integer productId, @RequestParam("quantity") Integer quantity);
}

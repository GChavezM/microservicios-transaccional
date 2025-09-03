package bo.edu.ucb.ms.warehouse.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.ms.warehouse.bl.ProductStockBl;
import bo.edu.ucb.ms.warehouse.entity.Product;

@RestController
@RequestMapping("/api/stock")
public class ProductStockApi {

    @Autowired
    private ProductStockBl productStockBl;

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkStock(
            @RequestParam("productId") Integer productId,
            @RequestParam("quantity") Integer quantity) {
        
        boolean hasStock = productStockBl.hasStock(productId, quantity);
        return ResponseEntity.ok(hasStock);
    }

    @PostMapping("/decrease")
    public ResponseEntity<Product> decreaseStock(
            @RequestParam("productId") Integer productId,
            @RequestParam("quantity") Integer quantity) {
        try {
            Product updatedProduct = productStockBl.decreaseStock(productId, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

package bo.edu.ucb.ms.warehouse.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.ms.warehouse.bl.ProductStockBl;

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
    public ResponseEntity<?> decreaseStock(
            @RequestParam Integer productId,
            @RequestParam Integer quantity) {
        if (productId == null || productId <= 0) {
            return ResponseEntity.badRequest().body("Invalid product ID");
        }
        if (quantity == null || quantity <= 0) {
            return ResponseEntity.badRequest().body("Invalid quantity");
        }
        try {
            productStockBl.decreaseStock(productId, quantity);
            return ResponseEntity.ok("Stock decreased successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/increase")
    public ResponseEntity<?> increaseStock(
            @RequestParam Integer productId,
            @RequestParam Integer quantity) {
        if (productId == null || productId <= 0) {
            return ResponseEntity.badRequest().body("Invalid product ID");
        }
        if (quantity == null || quantity <= 0) {
            return ResponseEntity.badRequest().body("Invalid quantity");
        }
        try {
            productStockBl.increaseStock(productId, quantity);
            return ResponseEntity.ok("Stock increased successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}

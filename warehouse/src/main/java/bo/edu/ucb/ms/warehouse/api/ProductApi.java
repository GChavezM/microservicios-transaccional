package bo.edu.ucb.ms.warehouse.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.ms.warehouse.bl.ProductStockBl;
import bo.edu.ucb.ms.warehouse.entity.Product;

@RestController
@RequestMapping("/api/product")
public class ProductApi {

    @Autowired
    private ProductStockBl productStockBl;

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer productId) {
        Product product = productStockBl.getProductById(productId);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

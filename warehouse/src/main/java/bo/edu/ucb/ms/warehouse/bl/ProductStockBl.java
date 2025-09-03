package bo.edu.ucb.ms.warehouse.bl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.ucb.ms.warehouse.entity.Product;
import bo.edu.ucb.ms.warehouse.repository.ProductRepository;

@Service
public class ProductStockBl {
    
    @Autowired
    private ProductRepository productRepository;
    
    public Product getProductById(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        return product;
    }
    
    /**
     * Updates the stock information of a product
     * @param product The product with updated stock information
     * @return The updated Product entity
     * @throws IllegalArgumentException if product is null or has invalid data
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Product updateProductStock(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        
        // Verify the product exists in the database
        if (!productRepository.existsById(product.getId())) {
            throw new IllegalArgumentException("Product with ID " + product.getId() + " not found");
        }
        
        // Validate stock quantity is not negative
        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        
        // Save and return the updated product
        return productRepository.save(product);
    }

    public boolean hasStock(Integer productId, Integer quantity) {
        Product product = getProductById(productId);
        if (product == null) {
            return false;
        }
        return product.getStockQuantity() >= quantity;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Product decreaseStock(Integer productId, Integer quantity) {
        Product product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found");
        }
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock for product " + productId);
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
        return productRepository.save(product);
    }
    
}

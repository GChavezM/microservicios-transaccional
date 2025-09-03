package bo.edu.ucb.ms.sales.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.ms.sales.bl.CompleSaleBl;
import bo.edu.ucb.ms.sales.dto.SaleDto;
import bo.edu.ucb.ms.sales.entity.Sale;

@RestController
@RequestMapping("/api/sales")
public class SaleApi {
    
    
    private CompleSaleBl compleSaleBl;

    public SaleApi(CompleSaleBl compleSaleBl) {
        this.compleSaleBl = compleSaleBl;
    }

    @PostMapping
    public ResponseEntity<SaleDto> createSale(@RequestParam Integer productId, 
                                             @RequestParam(defaultValue = "1") Integer quantity) {
        // Create and save the sale using the business logic
        Sale savedSale = compleSaleBl.createAndSaveSale(productId, quantity);
        
        // Convert Sale entity to SaleDto
        SaleDto saleDto = convertToDto(savedSale);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(saleDto);
    }

    /**
     * Converts a Sale entity to SaleDto
     * @param sale The Sale entity to convert
     * @return SaleDto with all the sale information
     */
    private SaleDto convertToDto(Sale sale) {
        SaleDto dto = new SaleDto();
        dto.setId(sale.getId());
        dto.setSaleNumber(sale.getSaleNumber());
        dto.setProductId(sale.getProductId());
        dto.setQuantity(sale.getQuantity());
        dto.setUnitPrice(sale.getUnitPrice());
        dto.setTotalAmount(sale.getTotalAmount());
        dto.setDiscountPercentage(sale.getDiscountPercentage());
        dto.setDiscountAmount(sale.getDiscountAmount());
        dto.setFinalAmount(sale.getFinalAmount());
        dto.setSaleDate(sale.getSaleDate());
        dto.setCustomerId(sale.getCustomerId());
        dto.setCustomerName(sale.getCustomerName());
        dto.setSalesperson(sale.getSalesperson());
        dto.setPaymentMethod(sale.getPaymentMethod());
        dto.setPaymentStatus(sale.getPaymentStatus());
        dto.setNotes(sale.getNotes());
        dto.setCreatedAt(sale.getCreatedAt());
        dto.setUpdatedAt(sale.getUpdatedAt());
        return dto;
    }

}

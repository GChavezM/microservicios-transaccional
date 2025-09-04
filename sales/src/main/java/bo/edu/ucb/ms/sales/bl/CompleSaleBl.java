package bo.edu.ucb.ms.sales.bl;

import bo.edu.ucb.ms.sales.entity.Sale;
import bo.edu.ucb.ms.sales.repository.SaleRepository;
import bo.edu.ucb.ms.sales.service.ProductStockService;
import bo.edu.ucb.ms.sales.service.RegisterJournalService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
public class CompleSaleBl {

    private final SaleRepository saleRepository;
    private final ProductStockService productStockService;
    private final RegisterJournalService registerJournalService;

    public CompleSaleBl(ProductStockService productStockService, RegisterJournalService registerJournalService, SaleRepository saleRepository) {
        this.productStockService = productStockService;
        this.registerJournalService = registerJournalService;
        this.saleRepository = saleRepository;
    }

    /**
     * Creates a new Sale based on ProductDto information
     * @param productPrice The product price
     * @param productId The ID of the product being sold
     * @param quantity The quantity to sell
     * @return The created Sale entity
     */
    private Sale createSale(Integer productId, BigDecimal productPrice, Integer quantity) {
        if (productPrice == null || productPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("ProductDto cannot be null");
        }
        
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        
        // Generate a unique sale number
        String saleNumber = "SALE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Create the sale - assuming productId will be set separately or retrieved from a product service
        Sale sale = new Sale();
        sale.setCustomerId(1); // Set actual customerId if available
        sale.setSaleNumber(saleNumber);
        sale.setProductId(productId);
        sale.setQuantity(quantity);
        sale.setUnitPrice(productPrice);
        
        // Calculate total amount
        BigDecimal totalAmount = productPrice.multiply(BigDecimal.valueOf(quantity));
        sale.setTotalAmount(totalAmount);
        
        return sale;
    }
    
    /**
     * Creates and persists a new Sale based on ProductDto information
     * @param productId The ID of the product being sold
     * @param quantity The quantity to sell
     * @return The persisted Sale entity with generated ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Sale createAndSaveSale(Integer productId, Integer quantity, BigDecimal creditAmount) {
        
        // get product details from warehouse service
        Map<String, String> productMap = productStockService.getProductById(productId);
        if (productMap == null || productMap.isEmpty()) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found");
        }
        BigDecimal productPrice = new BigDecimal(productMap.get("price"));
        Integer stockQuantity = Integer.parseInt(productMap.get("stockQuantity"));
        
        if (stockQuantity < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + stockQuantity + ", Requested: " + quantity);
        }
        
        // Create the sale
        Sale sale = createSale(productId, productPrice, quantity);

        // Disminuye el stock del producto
        try {
            ResponseEntity<String> decreaseStockResponse = productStockService.decreaseStock(productId, quantity);
            if (!decreaseStockResponse.getStatusCode().is2xxSuccessful()) {
                saleRepository.delete(sale);
                throw new IllegalArgumentException("Failed to decrease stock for product ID " + productId);
            }
        } catch (Exception e) {
            saleRepository.delete(sale);
            throw new IllegalArgumentException("Failed to decrease stock for product ID " + productId);
        }

        // Registra el diario contable
        Map<String, Object> saleMap = Map.of(
            "customerId", sale.getCustomerId(),
            "amount", sale.getTotalAmount(),
            "description", "Venta - " + sale.getSaleNumber() + " - Producto ID: " + sale.getProductId(),
            "createdBy", "SISTEMA_VENTAS",
            "credit_amount", creditAmount
            );
        try {
            ResponseEntity<String> response = registerJournalService.registerSale(saleMap);
            if (!response.getStatusCode().is2xxSuccessful()) {
                // saleRepository.delete(sale);
                productStockService.increaseStock(productId, quantity);
                throw new IllegalArgumentException("Failed to register sale in journal for sale ID " + sale.getId());
            }
        } catch (Exception e) {
            // saleRepository.delete(sale);
            productStockService.increaseStock(productId, quantity);
            throw new IllegalArgumentException("Failed to register sale in journal for sale ID " + sale.getId());
        }

        // Persist the sale
        return saleRepository.save(sale);
    }
    

    /**
     * Registers accounting entries for a sale transaction
     * Creates debit entry for Accounts Receivable and credit entry for Sales Revenue
     * @param sale The sale to register in the journal
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private ResponseEntity<String> registerSaleInJournal(Sale sale, BigDecimal creditAmount) {

        Map<String, Object> saleMap = Map.of(
            "customerId", sale.getCustomerId(),
            "amount", sale.getTotalAmount(),
            "description", "Venta - " + sale.getSaleNumber() + " - Producto ID: " + sale.getProductId(),
            "createdBy", "SISTEMA_VENTAS",
            "credit_amount", creditAmount
        );
        try {
            ResponseEntity<String> response = registerJournalService.registerSale(saleMap);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalArgumentException("Failed to register sale in journal for sale ID " + sale.getId());
            }
            return response;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to register sale in journal for sale ID " + sale.getId());
        }

        // // Constants for account codes and names
        // final String ACCOUNTS_RECEIVABLE_CODE = "1200";
        // final String ACCOUNTS_RECEIVABLE_NAME = "Cuentas por Cobrar";
        // final String SALES_REVENUE_CODE = "4100";
        // final String SALES_REVENUE_NAME = "Ingresos por Ventas";
        // final String CREATED_BY = "SISTEMA_VENTAS";
        // final String DEPARTMENT = "VENTAS";
        
        // // Create debit entry for Accounts Receivable
        // JournalDto debitDto = new JournalDto(
        //     ACCOUNTS_RECEIVABLE_CODE,
        //     ACCOUNTS_RECEIVABLE_NAME,
        //     "Venta - " + sale.getSaleNumber() + " - Producto ID: " + sale.getProductId(),
        //     sale.getTotalAmount(),
        //     "D", // Debit
        //     CREATED_BY
        // );
        // debitDto.setReferenceNumber(sale.getSaleNumber());
        // debitDto.setDepartment(DEPARTMENT);
        // debitDto.setTransactionDate(LocalDate.now());
        // debitDto.setNotes("Registro automático por venta de producto");
        
        // // Create credit entry for Sales Revenue
        // JournalDto creditDto = new JournalDto(
        //     SALES_REVENUE_CODE,
        //     SALES_REVENUE_NAME,
        //     "Venta - " + sale.getSaleNumber() + " - Producto ID: " + sale.getProductId(),
        //     sale.getTotalAmount(),
        //     "C", // Credit
        //     CREATED_BY
        // );
        // creditDto.setReferenceNumber(sale.getSaleNumber());
        // creditDto.setDepartment(DEPARTMENT);
        // creditDto.setTransactionDate(LocalDate.now());
        // creditDto.setNotes("Registro automático por venta de producto");
        
        // // Register both journal entries
        // try {
        //     Journal debitJournal = registerJournal.registerJournal(debitDto);
        //     Journal creditJournal = registerJournal.registerJournal(creditDto);
            
        //     // Log successful registration (optional)
        //     System.out.println("Registered journal entries for sale " + sale.getSaleNumber() + 
        //                      ": Debit JE-" + debitJournal.getJournalEntryNumber() + 
        //                      ", Credit JE-" + creditJournal.getJournalEntryNumber());
        // } catch (Exception e) {
        //     // Log error but don't fail the sale transaction
        //     System.err.println("Error registering journal entries for sale " + sale.getSaleNumber() + ": " + e.getMessage());
        //     // In a production system, you might want to implement a compensation mechanism
        //     // or queue the journal entries for retry
        // }
    }

    /**
     * Finds a sale by its sale number
     * @param saleNumber The unique sale number
     * @return The Sale entity if found, null otherwise
     */
    public Sale findBySaleNumber(String saleNumber) {
        return saleRepository.findBySaleNumber(saleNumber).orElse(null);
    }
}

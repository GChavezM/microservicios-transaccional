package bo.edu.ucb.ms.sales.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bo.edu.ucb.ms.sales.bl.CompleSaleBl;
import bo.edu.ucb.ms.sales.repository.SaleRepository;
import bo.edu.ucb.ms.sales.service.ProductStockService;
import bo.edu.ucb.ms.sales.service.RegisterJournalService;

@Configuration
public class AppConfig {

    @Bean
    public CompleSaleBl compleSaleBl(ProductStockService productStockService, 
                                     RegisterJournalService registerJournalService, 
                                     SaleRepository saleRepository) {
        return new CompleSaleBl(productStockService, registerJournalService, saleRepository);
    }
}
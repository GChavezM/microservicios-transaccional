package bo.edu.ucb.ms.accounting.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.ms.accounting.bl.RegisterJournal;

@RestController
@RequestMapping("/api/journal")
public class RegisterJournalApi {

    @Autowired
    private RegisterJournal registerJournal;

    @PostMapping("/sale")
    public ResponseEntity<?> registerSale(@RequestBody Map<String, String> saleData) {
        try {
            registerJournal.registerSale(saleData);
            return ResponseEntity.ok("Sale registered successfully in journal");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid data provided: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering sale in journal: " + e.getMessage());
        }
    }
}

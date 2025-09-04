package bo.edu.ucb.ms.accounting.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Boolean> registerSale(@RequestBody Map<String, String> saleData) {
        try {
            registerJournal.registerSale(saleData);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}

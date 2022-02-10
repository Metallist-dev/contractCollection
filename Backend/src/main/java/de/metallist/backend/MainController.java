package de.metallist.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.Optional;

import static de.metallist.backend.ReasonCodes.RC_DELETE_MISSING;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Controller, which handles the requests to the REST-API
 *
 * @author Metallist-dev
 * @version 0.1
 */
@Controller
@Slf4j
@RequestMapping(path = "/demo")
public class MainController {
    private final Repository contractRepository;

    @Autowired
    public MainController(Repository contractRepository) {
        this.contractRepository = contractRepository;
    }

    /**
     * adds a new contract
     * @param category          category
     * @param name              readable name
     * @param expenses          costs per payment
     * @param cycle             cycle of payment (see {@link Contract#cycle})
     * @param customerNr        ID of customer
     * @param contractNr        ID of contract
     * @param startDate         start of the contract in format "YYYY-MM-DD"
     * @param contractPeriod    length of a contractual period
     * @param periodOfNotice    length of a period of notice in weeks
     * @param description       short description of content
     * @param documentPath      path to folder with documents
     * @return added contract or empty contract
     */
    @SuppressWarnings("JavadocReference")
    @PostMapping(path = "/add")
    public ResponseEntity<Contract> addContract(
            @RequestParam String category,
            @RequestParam String name,
            @RequestParam String expenses,
            @RequestParam String cycle,
            @RequestParam String customerNr,
            @RequestParam String contractNr,
            @RequestParam String startDate,
            @RequestParam String contractPeriod,
            @RequestParam String periodOfNotice,
            @RequestParam String description,
            @RequestParam String documentPath
            ) {
        try {
            Contract contract = new Contract();
            contract.newContract(
                    decode(category), decode(name), decode(expenses), decode(cycle), decode(customerNr),
                    decode(contractNr), decode(startDate), decode(contractPeriod), decode(periodOfNotice),
                    decode(description), decode(documentPath)
            );

            int maxID = 1;
            for (Contract v : contractRepository.findAll()) {
                if (v.getId() >= maxID) maxID = v.getId() + 1;
            }
            contract.setId(maxID);

            contractRepository.save(contract);

            return ResponseEntity.ok(contract);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(NOT_FOUND).body(new Contract());
        }
    }

    /**
     * deletes a specified contract
     * @param id   primary key of the contract
     * @param name name for double-check
     * @return     message telling about success/failure
     */
    @PostMapping(path = "/delete")
    public ResponseEntity<String> deleteContract(@RequestParam String id, @RequestParam String name) {
        try {
            log.info("Try to delete the contract with ID " + id + ".");
            if (new Contract().deleteContract(contractRepository, id, name)) {
                log.info("Successfully deleted.");
                return ResponseEntity.ok("successful");
            }
            else {
                log.error("Something went wrong during deletion of the contract with ID " + id + ".");
                return ResponseEntity.badRequest().body("Something went wrong.");
            }

        } catch (NullPointerException npe) {
            String message = RC_DELETE_MISSING + ": the contract with ID " + id + " and name " +  name + " is unavailable.";
            System.out.println(message);
            return ResponseEntity.unprocessableEntity().body("Contract with ID " + id + " and name " +  name + " is unavailable.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * fetches a list of all contracts
     * @return list of all contracts
     */
    @GetMapping(path = "/all")
    public ResponseEntity<Iterable<Contract>> getAllContracts() {
        log.info("GET-Request for all contracts.");
        return ResponseEntity.ok(contractRepository.findAll());
    }

    /**
     * fetches a specified contract
     * @param id   contract ID (primary key)
     * @return     contract or null
     */
    @GetMapping(path = "/get")
    public ResponseEntity<Contract> getSingleContract(@RequestParam String id) { return ResponseEntity.ok(new Contract().getContract(contractRepository, id)); }

    @PatchMapping(path = "/change/{id}")
    public ResponseEntity<Contract> updateContract(@PathVariable int id, @RequestParam String key, @RequestParam String value) {
        Optional<Contract> contractCandidate = contractRepository.findById(id);
        if (contractCandidate.isEmpty()) return null;
        Contract contract = contractCandidate.get();

        Contract newContract = contract.updateContract(key, value);
        //if (newContract==null) return "Es gab einen Fehler bei Änderung des Key " + key + "\n";
        //if (newContract==null) return ResponseEntity.status(CONFLICT).body("Es gab einen Fehler bei Änderung des Key " + key + "\n");
        if (newContract ==null) return ResponseEntity.status(CONFLICT).body(new Contract());
        contractRepository.save(contract);
        //return ResponseEntity.ok("Der Contract " + id + " wurde im Wert " + key + " geändert. Neuer Wert: " + value + "\n");
        return ResponseEntity.ok(contract);
    }


    /// Helpers
    private String decode(String input) {
        return URLDecoder.decode(input, UTF_8);
    }
}

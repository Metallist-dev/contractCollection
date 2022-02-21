package de.metallist.backend;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

import static de.metallist.backend.ReasonCodes.*;
import static org.springframework.http.HttpStatus.*;

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
     * @param contractJson      holds all data of the new contract
     * @return added contract or empty contract
     */
    @PostMapping(path = "/add")
    public ResponseEntity<JsonNode> addContract(@RequestBody JsonNode contractJson) {
        try {
            String category = contractJson.get("category").textValue();
            String name = contractJson.get("name").textValue();
            float expenses = contractJson.get("expenses").floatValue();
            int cycle = contractJson.get("cycle").intValue();
            String customerNr = contractJson.get("customerNr").textValue();
            String contractNr = contractJson.get("contractNr").textValue();
            String startDate = contractJson.get("startDate").textValue();
            int contractPeriod = contractJson.get("contractPeriod").intValue();
            int periodOfNotice = contractJson.get("periodOfNotice").intValue();
            String description = contractJson.get("description").asText();
            String documentPath = contractJson.get("documentPath").asText();

            if (cycle < 1) throw new RuntimeException("The given cycle is below 1 month. Please check the input.");

            int maxID = 1;
            for (Contract v : contractRepository.findAll()) {
                if (v.getId() == maxID) maxID = v.getId() + 1;
            }

            Contract contract = new Contract(
                    maxID, category, name, expenses, cycle, customerNr, contractNr, startDate, contractPeriod,
                    periodOfNotice, description, documentPath
            );

            contractRepository.save(contract);

            return ResponseEntity.ok(HttpResponse.requestSingleContract(RC_CREATE_SUCCESS, contract));
        } catch (Exception e) {
            log.error(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(HttpResponse.requestSingleContract(RC_CREATE_ERROR, new Contract()));
        }
    }

    /**
     * deletes a specified contract
     * @param request holds all data of the request
     * @return        message telling about success/failure
     */
    @PostMapping(path = "/delete")
    public ResponseEntity<JsonNode> deleteContract(@RequestBody JsonNode request) {
        int id = request.get("id").intValue();
        String name = request.get("name").textValue();

        try {
            log.info("Try to delete the contract with ID " + id + ".");
            if (new Contract().deleteContract(contractRepository, id, name)) {
                log.info("Successfully deleted.");
                return ResponseEntity.status(OK).body(HttpResponse.requestDeleteContract(RC_DELETE_SUCCESS));
            }
            else throw new RuntimeException("Something went wrong during deletion of the contract with ID \" + id + \".");

        } catch (NullPointerException npe) {
            String message = RC_DELETE_MISSING + ": the contract with ID " + id + " and name " +  name + " is unavailable.";
            log.error(message);
            return ResponseEntity.status(NOT_FOUND).body(HttpResponse.requestDeleteContract(RC_DELETE_MISSING));
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(HttpResponse.requestDeleteContract(RC_DELETE_ERROR));
        }
    }

    /**
     * fetches a list of all contracts
     * @return list of all contracts
     */
    @GetMapping(path = "/all")
    public ResponseEntity<JsonNode> getAllContracts() {
        log.info("GET-Request for all contracts.");
        return ResponseEntity.status(OK).body(HttpResponse.requestGetAllContracts(contractRepository.findAll()));
    }

    /**
     * fetches a specified contract
     * @param id   id of the requested contract
     * @return     contract or null
     */
    @GetMapping(path = "/get/{id}")
    public ResponseEntity<JsonNode> getSingleContract(@PathVariable int id) {
        log.info("GET-Request for single contract with id " + id);
        //int id = jsonNode.intValue();
        Contract contract = new Contract().getContract(contractRepository, id);
        if (contract != null) return ResponseEntity.ok(HttpResponse.requestSingleContract(RC_GENERAL_SUCCESS, contract));
        else return ResponseEntity.status(NOT_FOUND).body(HttpResponse.requestSingleContract(RC_GENERAL_ERROR, new Contract()));
    }

    /**
     * changes some information on a contract (only one key per request!)
     * @param id      id of the contract
     * @param request key and value which will be updated
     * @return        updated contract or empty contract
     */
    @PatchMapping(path = "/change/{id}")
    public ResponseEntity<JsonNode> updateContract(@PathVariable int id, @RequestBody JsonNode request) {
        log.info("PATCH-Request for single contract with id " + id);
        String key = request.get("key").textValue();
        String value = request.get("value").asText();
        Optional<Contract> contractCandidate = contractRepository.findById(id);
        if (contractCandidate.isEmpty()) return null;
        Contract contract = contractCandidate.get();

        Contract newContract = contract.updateContract(key, value);
        if (newContract == null) {
            log.error("Es gab einen Fehler bei Änderung des Key " + key);
            log.debug("key = " + key + ", value = " + value);
            return ResponseEntity.status(CONFLICT).body(HttpResponse.requestSingleContract(RC_UPDATE_ERROR, new Contract()));
        }
        contractRepository.save(contract);
        log.info("Der Contract " + id + " wurde im Wert " + key + " geändert. Neuer Wert: " + value);
        return ResponseEntity.ok(HttpResponse.requestSingleContract(RC_UPDATE_SUCCESS, contract));
    }
}

package de.metallistdev.contractcollection.application;

import com.fasterxml.jackson.databind.JsonNode;
import de.metallistdev.contractcollection.application.utilities.HttpResponse;
import de.metallistdev.contractcollection.application.utilities.SessionUtil;
import de.metallistdev.contractcollection.commons.Contract;
import de.metallistdev.contractcollection.application.utilities.ReasonCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@Slf4j
@RequestMapping(path = "/")
public class MainController {

    private final SessionUtil session;

    @Autowired
    public MainController(SessionUtil session) {
        this.session = session;
    }

    @GetMapping(path = "/status")
    public ResponseEntity<String> getStatus() {
        log.info("Requested status message");
        return ResponseEntity.ok("Backend running.\n");
    }

    /**
     * adds a new contract
     * @param contractJson      holds all data of the new contract
     * @return added contract or empty contract
     */
    @PostMapping(path = "/add")
    public ResponseEntity<JsonNode> addContract(@RequestBody JsonNode contractJson) {
        log.info("Add a contract.");
        log.debug(contractJson.toPrettyString());

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

            if (cycle < 1) throw new IllegalArgumentException("The given cycle is below 1 month. Please check the input.");

            int maxID = 1;
            for (Contract v : session.getContracts()) {
                if (v.getId() == maxID) maxID = v.getId() + 1;
            }

            Contract contract = new Contract(
                    maxID, category, name, expenses, cycle, customerNr, contractNr, startDate, contractPeriod,
                    periodOfNotice, description, documentPath
            );

            if (!session.addContract(contract)) throw new IllegalStateException("Failed to add contract.");

            return ResponseEntity.ok(HttpResponse.requestSingleContract(ReasonCodes.RC_CREATE_SUCCESS, contract));
        } catch (Exception e) {
            log.error(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(HttpResponse.requestSingleContract(ReasonCodes.RC_CREATE_ERROR, new Contract()));
        }
    }

    /**
     * deletes a specified contract
     * @param request holds all data of the request
     * @return        message telling about success/failure
     */
    @PostMapping(path = "/delete")
    public ResponseEntity<JsonNode> deleteContract(@RequestBody JsonNode request) {
        log.info("Delete a contract with ID {}", request.get("id").intValue());
        log.debug(request.toPrettyString());

        int id = request.get("id").intValue();
        String name = request.get("name").textValue();
        Contract contract = session.getSingleContract(id);

        try {
            log.info("Try to delete the contract with ID {}.", id);
            if (session.removeContract(id)) {
                log.info("Successfully deleted by ID.");
                return ResponseEntity.status(OK).body(HttpResponse.requestDeleteContract(ReasonCodes.RC_DELETE_SUCCESS));
            } else if (session.removeContract(contract)) {
                log.info("Successfully deleted by object.");
                return ResponseEntity.status(OK).body(HttpResponse.requestDeleteContract(ReasonCodes.RC_DELETE_SUCCESS));
            } else
                throw new IllegalStateException("Something went wrong during deletion of the contract with ID " + id + ".");

        } catch (NullPointerException npe) {
            String message = ReasonCodes.RC_DELETE_MISSING + ": the contract with ID " + id + " and name " +  name + " is unavailable.";
            log.error(message);
            return ResponseEntity.status(NOT_FOUND).body(HttpResponse.requestDeleteContract(ReasonCodes.RC_DELETE_MISSING));
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().body(HttpResponse.requestDeleteContract(ReasonCodes.RC_DELETE_ERROR));
        }
    }

    /**
     * fetches a list of all contracts
     * @return list of all contracts
     */
    @GetMapping(path = "/all")
    public ResponseEntity<JsonNode> getAllContracts() {
        log.info("GET-Request for all contracts.");
        return ResponseEntity.status(OK).body(HttpResponse.requestGetAllContracts(ReasonCodes.RC_GENERAL_SUCCESS, session.getContracts()));
    }

    /**
     * fetches a specified contract
     * @param id   id of the requested contract
     * @return     contract or null
     */
    @GetMapping(path = "/get/{id}")
    public ResponseEntity<JsonNode> getSingleContract(@PathVariable int id) {
        log.info("GET-Request for single contract with id {}", id);
        Contract contract = session.getSingleContract(id);
        if (contract != null) return ResponseEntity.ok(HttpResponse.requestSingleContract(ReasonCodes.RC_GENERAL_SUCCESS, contract));
        else return ResponseEntity.status(NOT_FOUND).body(HttpResponse.requestSingleContract(ReasonCodes.RC_GENERAL_ERROR, new Contract()));
    }

    /**
     * changes some information on a contract (only one key per request!)
     * @param id      id of the contract
     * @param request key and value which will be updated
     * @return        updated contract or empty contract
     */
    @PatchMapping(path = "/change/{id}")
    public ResponseEntity<JsonNode> updateContract(@PathVariable int id, @RequestBody JsonNode request) {
        log.info("PATCH-Request for single contract with id {}", id);
        log.debug(request.toPrettyString());

        String key = request.get("key").textValue();
        String value = request.get("value").asText();

        Contract newContract = session.updateContract(id, key, value);
        if (newContract == null) {
            log.error("An error occurred during the update of key {}", key);
            log.debug("key = {}, value = {}", key, value);
            return ResponseEntity.status(CONFLICT).body(HttpResponse.requestSingleContract(ReasonCodes.RC_UPDATE_ERROR, new Contract()));
        }
        log.info("The Contract number {} was update for key {}. New value: {}", id, key, value);
        return ResponseEntity.ok(HttpResponse.requestSingleContract(ReasonCodes.RC_UPDATE_SUCCESS, newContract));
    }

    /**
     * imports data from a json-file
     * @param request contains the path of the file and whether to append or overwrite existing data
     * @return        status json with the imported or existing contracts
     */
    @PutMapping(path = "/import")
    public ResponseEntity<JsonNode> importFile(@RequestBody JsonNode request) {
        log.info("Import contracts from file.");
        log.debug(request.toPrettyString());

        boolean overwrite = request.get("overwrite").booleanValue();
        if (overwrite) session.removeAllContracts();
        String filepath = request.get("filepath").asText();
        String password = request.get("password").asText();

        List<Contract> result = session.loadFile(filepath, password);

        if (result == null || result.isEmpty())
            return ResponseEntity.badRequest().body(HttpResponse.requestGetAllContracts(ReasonCodes.RC_IMPORT_FAILED, session.getContracts()));
        else return ResponseEntity.ok(HttpResponse.requestGetAllContracts(ReasonCodes.RC_IMPORT_SUCCESS, result));
    }

    /**
     * exports given contracts to json-file
     * @param request contains path and contracts
     * @return        status json
     */
    @PostMapping(path = "/export")
    public ResponseEntity<JsonNode> exportToFile(@RequestBody JsonNode request) {
        log.info("Export given contracts to file.");
        log.debug(request.toPrettyString());

        String filepath = request.get("filepath").textValue();
        String password = request.get("password").textValue();
        log.debug("contracts = {}", request.get("contracts").toPrettyString());

        if (session.writeFile(filepath, password)) return ResponseEntity.ok(HttpResponse.requestDeleteContract(ReasonCodes.RC_EXPORT_SUCCESS));
        else return ResponseEntity.badRequest().body(HttpResponse.requestDeleteContract(ReasonCodes.RC_EXPORT_FAILED));
    }

    /**
     * prepares a shutdown
     * @return  status 200 or 500
     */
    @GetMapping("/shutdown")
    public ResponseEntity<JsonNode> prepareShutdown() {
        log.info("Shutdown requested");
        boolean success = session.prepareShutdown();
        if (success) {
            log.info("data export successful");
            return ResponseEntity.ok(HttpResponse.requestShutdown(ReasonCodes.RC_SHUTDOWN_SUCCESS));
        }
        else {
            log.error("data export not successful!");
            return ResponseEntity.internalServerError().body(HttpResponse.requestShutdown(ReasonCodes.RC_SHUTDOWN_FAILED));
        }
    }
}

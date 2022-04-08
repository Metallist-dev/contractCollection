package de.metallist.backend.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.metallist.backend.Contract;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * holds the most recent status
 *
 * @author Metallist-dev
 * @version 0.1
 */
@Slf4j
@Component
public class SessionUtil {
    @Getter
    private ArrayList<Contract> contracts;

    @Getter @Setter
    private User user;

    private Preferences preferences;

    public SessionUtil() {
        this.contracts = new ArrayList<>();
        this.user = new User();
        this.preferences = Preferences.userNodeForPackage(de.metallist.backend.utilities.SessionUtil.class);

        this.startup();
    }


    /**
     * searches for a given contract in the database
     * @param id           ID of the contract (primary key)
     * @return             object of the contract
     */
    public Contract getSingleContract(int id) {
        return contracts.get(id);
    }

    /**
     * adds a contract to the recent session
     * @param contract      the contract to be added
     * @return              boolean whether successful or not
     */
    public boolean addContract(Contract contract) {
        return contracts.add(contract);
    }

    /**
     * deletes a given contract by object
     * @param contract      the contract to be removed
     * @return              boolean whether successful or not
     */
    public boolean removeContract(Contract contract) {
        return contracts.remove(contract);
    }

    /**
     * deletes a given contract by id
     * @param id           ID of the contract (primary key)
     * @return             boolean, which informs about success/failure
     */
    public boolean removeContract(int id) {
        Contract toRemove = contracts.get(id);
        return contracts.remove(id).equals(toRemove);
    }

    /**
     * clears the list of contracts
     * <b>important: use with caution!</b>
     * basically only used with overwriting-import
     */
    public void removeAllContracts() {
        contracts = new ArrayList<>();
    }

    /**
     * changes a given attribute of the contract
     * @param id    id of the contract to be changed
     * @param key   name of the attribute, which is supposed to be changed
     * @param value new value
     * @return      object of the contract or null
     */
    public Contract updateContract(int id, String key, String value) {
        Contract newContract = this.getSingleContract(id);

        switch (key) {
            case "category": {
                newContract.setCategory(value);
                break;
            }
            case "name": {
                newContract.setName(value);
                break;
            }
            case "expenses": {
                newContract.setExpenses(Float.parseFloat(value));
                break;
            }
            case "cycle": {
                newContract.setCycle(Integer.parseInt(value));
                break;
            }
            case "customerNr": {
                newContract.setCustomerNr(value);
                break;
            }
            case "contractNr": {
                newContract.setContractNr(value);
                break;
            }
            case "contractPeriod": {
                newContract.setContractPeriod(Integer.parseInt(value));
                break;
            }
            case "periodOfNotice": {
                newContract.setPeriodOfNotice(Integer.parseInt(value));
                break;
            }
            case "startDate": {
                newContract.setStartDate(value);
                break;
            }
            case "description": {
                newContract.setDescription(value);
                break;
            }
            case "documentPath": {
                newContract.setDocumentPath(value);
                break;
            }
            default: return null;
        }
        return newContract;
    }

    /**
     * imports a list of contracts to th current session
     * @param importJson    json, which contains a list of contracts
     * @return              all imported contracts
     */
    public ArrayList<Contract> importContracts(JsonNode importJson) {
        if (importJson.getNodeType() != JsonNodeType.ARRAY) return null;

        for (JsonNode contractJson : importJson) {
            Contract contract = new Contract();

            contract.setCategory(contractJson.get("category").textValue());
            contract.setName(contractJson.get("name").textValue());
            contract.setExpenses(contractJson.get("expenses").floatValue());
            contract.setCycle(contractJson.get("cycle").intValue());
            contract.setCustomerNr(contractJson.get("customerNr").textValue());
            contract.setContractNr(contractJson.get("contractNr").textValue());
            contract.setStartDate(contractJson.get("startDate").textValue());
            contract.setContractPeriod(contractJson.get("contractPeriod").intValue());
            contract.setPeriodOfNotice(contractJson.get("periodOfNotice").intValue());
            contract.setDescription(contractJson.get("description").textValue());
            contract.setDocumentPath(contractJson.get("documentPath").textValue());

            int maxID = 1;
            for (Contract v : this.getContracts()) {
                if (v.getId() == maxID) maxID++;
            }

            contract.setId(maxID);
            if (this.addContract(contract)) log.info("contract added");
            else {
                log.error("Failed to add contract.");
                log.debug(String.valueOf(contract));
            }
        }

        return this.getContracts();
    }

    /**
     * imports a list of contracts to th current session
     * @param filepath      path of file with json
     * @return              all imported contracts
     */
    public ArrayList<Contract> importContracts(String filepath) {
        log.info("Import contracts from file.");

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        JsonNode importData = mapper.createObjectNode();

        log.info("Load file " + filepath);

        try {
            String content = Files.readString(Paths.get(filepath));
            System.out.println(content);
            importData = mapper.readTree(content);
        } catch (IOException exception) {
            log.error("Failed to load file from " + filepath);
            log.debug(Arrays.toString(exception.getStackTrace()));
        }

        this.removeAllContracts();

        ArrayList<Contract> result = this.importContracts(importData);

        return this.getContracts();
    }

    /**
     * exports all contracts to the specified file
     * @param filepath  path to where the file should be stored
     * @return          status whether successfully written or not
     */
    public boolean export(String filepath) {
        boolean written = false;

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
            writer.write(this.contractsToPrettyString());
            writer.close();
            written = true;
        } catch (Exception e) {
            log.error("Failed exporting contracts with " + e.getClass());
            log.debug(e.getMessage());
        }

        return written;
    }

    /**
     * puts the list of contracts into a json
     * @return  string of json
     */
    private String contractsToPrettyString() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.valueToTree(contracts);
        return arrayNode.toPrettyString();
    }

    protected boolean startup() {
        System.out.println(System.getenv("PWD"));
        try {
            if (preferences.keys().length == 0) {
                String filepath = System.getenv("PWD") + "/contracts.json";
                preferences.put("Filepath", filepath);
            } else {
                String fallbackpath = System.getenv("PWD") + "/contracts.json";
                String filepath = preferences.get("Filepath", fallbackpath);
                contracts = this.importContracts(filepath);
            }
        } catch (BackingStoreException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean prepareShutdown() {
        String filepath = preferences.get("Filepath", "");
        return this.export(filepath);
    }
}

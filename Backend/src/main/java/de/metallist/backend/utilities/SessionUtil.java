package de.metallist.backend.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import de.metallist.backend.Contract;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * holds the most recent status
 *
 * @author Metallist-dev
 * @version 0.3
 */
@Slf4j
@Component
public class SessionUtil {
    @Getter
    private ArrayList<Contract> contracts;

    private SecretKey key;

    private IvParameterSpec iv;

    private final Preferences preferences;

    private String password;

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private final Base64.Encoder encoder = Base64.getEncoder();
    private final Base64.Decoder decoder = Base64.getDecoder();

    public SessionUtil() {
        this.contracts = new ArrayList<>();
        this.password = "";
        this.preferences = Preferences.userNodeForPackage(de.metallist.backend.utilities.SessionUtil.class);
    }


    /**
     * searches for a given contract in the database
     * @param id           ID of the contract (primary key)
     * @return             object of the contract
     */
    public Contract getSingleContract(int id) {
        for (Contract c : contracts) {
            if (c.getId() == id) return c;
        }
        return null;
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
        for (Contract c : contracts) {
            if (c.getId() == id) return removeContract(c);
        }
        return false;
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
        log.debug("request-data\n  id:    "+ id + "\n  key:   " + key + "\n  value: " + value);
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
            default: {
                log.error("Something went wrong.");
                return null;
            }
        }
        return newContract;
    }

    /**
     * imports a list of contracts to th current session
     * @param importJson    json, which contains a list of contracts
     * @return              all imported contracts
     */
    private ArrayList<Contract> importContracts(JsonNode importJson) {
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
     * puts the list of contracts into a json
     * @return  string of json
     */
    private String contractsToString() {
        ArrayNode arrayNode = mapper.valueToTree(contracts);
        return arrayNode.toString();
    }


    /**
     * unlocks and loads the file
     * @param filecontent path of file to open
     * @param password password to unlock the file
     */
    public List<Contract> loadFile(String filecontent, String password) {
        log.info("Try to unlock and load file.");
        log.debug("file contents: " + filecontent);

        String[] content;

        // split file content
        // syntax: "<pass hash>:<salt>:<IV>:<filecontent>"
        content = filecontent.split(":");
        if (content.length != 4) {
            log.error("Failed to load file.");
            log.debug(Arrays.toString(content));
            throw new RuntimeException("Failed to load file - Syntax error");
        }

        // check password
        byte[] passHash = decoder.decode(content[0]);
        byte[] salt = decoder.decode(content[1]);
        if (!EncryptionUtil.compareKeys(password, salt, passHash)) {
            log.error("Wrong password.");
            throw new RuntimeException("Wrong password.");
        }

        try {
            this.key = EncryptionUtil.generateKeyFromPassword(password, salt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.iv = new IvParameterSpec(decoder.decode(content[2]));


        // decrypt filecontent
        String encryptedData = content[3];
        String decryptedData;
        JsonNode json;
        try {
            decryptedData = EncryptionUtil.decrypt(encryptedData, key, iv);
            json = mapper.readTree(decryptedData);
        } catch (JsonProcessingException e) {
            log.error("Couldn't read data.");
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Couldn't read data.");
        } catch (Exception e) {
            log.error("Couldn't decrypt data.");
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Couldn't decrypt data.");
        }
        contracts = this.importContracts(json);
        //preferences.put("Filepath", content);     // DEPRECATED: react-fe forced to send content instead of path
        this.password = password;
        return contracts;
    }

    /**
     * writes the current content including password hash and salt into a file
     * @param filepath path where file gets stored
     * @param password plaintext password
     * @return boolean, whether successful or not
     */
    public boolean writeFile(String filepath, String password) {
        log.info("Creation of new file requested.");
        log.debug("Path: " + filepath);

        String content;
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        // prepare content
        try {
            this.key = EncryptionUtil.generateKeyFromPassword(password, salt);
            this.iv = EncryptionUtil.generateIV();
            content = EncryptionUtil.encrypt(this.contractsToString(), this.key, this.iv);
        } catch (Exception e) {
            log.error("Error during key generation.");
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Error during key generation.");
        }

        // actually write content
        // syntax: "<pass hash>:<salt>:<IV>:<content>"
        try {
            String writableContent = encoder.encodeToString(this.key.getEncoded()) +
                    ":" +
                    encoder.encodeToString(salt) +
                    ":" +
                    encoder.encodeToString(this.iv.getIV()) +
                    ":" +
                    content
            ;
            File myFile = new File(filepath);
            if (myFile.createNewFile()) {
                log.info("File " + filepath + " created.");
                FileWriter writer = new FileWriter(myFile);
                writer.write(writableContent);
                writer.close();
                log.info("Successfully written to file.");
            } else {
                Files.deleteIfExists(Path.of(filepath));
                if (!myFile.createNewFile()) {
                    log.error("Existing file " + filepath + " was not deleted.");
                    throw new RuntimeException("Existing file " + filepath + " was not deleted.");
                }
                log.info("File " + filepath + " created.");
                FileWriter writer = new FileWriter(myFile);
                writer.write(writableContent);
                writer.close();
                log.info("Successfully written to file.");
            }
        } catch (Exception e) {
            log.error("Error while writing file.");
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Error while writing file.");
        }
        return true;
    }

    /**
     * tries to export the current state into a file
     * @return true if export successful
     */
    public boolean prepareShutdown() {
        String filepath = preferences.get("Filepath", "");
        return this.writeFile(filepath, this.password);
    }
}

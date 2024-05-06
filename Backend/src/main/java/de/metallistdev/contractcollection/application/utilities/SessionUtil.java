package de.metallistdev.contractcollection.application.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import de.metallistdev.contractcollection.commons.Contract;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

@Slf4j
@Component
public class SessionUtil {

    @Getter
    private ArrayList<Contract> contracts;

    private final Preferences preferences;

    private String password;

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public SessionUtil() {
        this.contracts = new ArrayList<>();
        this.password = "";
        this.preferences = Preferences.userNodeForPackage(SessionUtil.class);
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
        Contract newContract = this.getSingleContract(id);

        switch (key) {
            case "category"         -> newContract.setCategory(value);
            case "name"             -> newContract.setName(value);
            case "expenses"         -> newContract.setExpenses(Float.parseFloat(value));
            case "cycle"            -> newContract.setCycle(Integer.parseInt(value));
            case "customerNr"       -> newContract.setCustomerNr(value);
            case "contractNr"       -> newContract.setContractNr(value);
            case "contractPeriod"   -> newContract.setContractPeriod(Integer.parseInt(value));
            case "periodOfNotice"   -> newContract.setPeriodOfNotice(Integer.parseInt(value));
            case "startDate"        -> newContract.setStartDate(value);
            case "description"      -> newContract.setDescription(value);
            case "documentPath"     -> newContract.setDocumentPath(value);
            default                 -> { return null; }
        }
        return newContract;
    }

    /**
     * imports a list of contracts to th current session
     * @param importJson    json, which contains a list of contracts
     * @return              all imported contracts
     */
    private ArrayList<Contract> importContracts(JsonNode importJson) {
        if (importJson.getNodeType() != JsonNodeType.ARRAY) return new ArrayList<>();

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
     * @param filepath path of file to open
     * @param password password to unlock the file
     */
    public List<Contract> loadFile(String filepath, String password) {
        log.info("Try to unlock and load file: {}", filepath);

        String fileContent;
        byte[] cipherdata;

        // read file content
        try {
            fileContent = Files.readString(Paths.get(filepath));
        } catch (Exception e) {
            log.error("Failed to load file from {}", filepath);
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            throw new IllegalArgumentException("Failed to load file.");
        }

        String[] bytevals = fileContent.substring(1, fileContent.length() - 1).split(",");
        byte[] data = new byte[bytevals.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = Byte.parseByte(bytevals[i].trim());
        }
        cipherdata = data;
        log.debug("content load: {}", Arrays.toString(cipherdata));

        // decrypt content
        String decryptedData;
        JsonNode json;
        try {
            decryptedData = EncryptionUtil.decrypt(cipherdata, password);
            json = mapper.readTree(decryptedData);
        } catch (JsonProcessingException e) {
            log.error("Couldn't read data.");
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            throw new IllegalArgumentException("Failed to read data.");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | InvalidKeySpecException e) {
            log.error("Couldn't decrypt data.");
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            throw new IllegalStateException("Failed to decrypt data.");
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            log.error("Couldn't decrypt data.");
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            throw new IllegalArgumentException("Failed to decrypt data.");
        } catch (Exception e) {
            log.error(e.getMessage());
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            if (e.getMessage().contains("IV length"))
                throw new IllegalStateException("decryption failed");
            else throw new IllegalStateException("Something undefined went wrong whilst decryption.");
        }
        log.debug(json.toPrettyString());
        contracts = this.importContracts(json);
        preferences.put("Filepath", filepath);
        this.password = password;
        return contracts;
    }

    /**
     * encrypts and writes the current content into a file
     * @param filepath path where file gets stored
     * @param password plaintext password
     * @return boolean, whether successful or not
     */
    public boolean writeFile(String filepath, String password) {
        log.info("Creation of new file requested.");
        log.debug("Path: {}", filepath);

        String content;

        // prepare content
        try {
            byte[] encryptOutput = EncryptionUtil.encrypt(this.contractsToString(), password);
            log.debug("encrypted write: {}", Arrays.toString(encryptOutput));
            content = Arrays.toString(encryptOutput);
        } catch (Exception e) {
            log.error("Error during key generation.");
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            throw new IllegalStateException("Error during key generation.");
        }

        // actually write content
        try {
            File myFile = new File(filepath);
            if (myFile.createNewFile()) {
                log.info("File {} created.", filepath);
                FileWriter writer = new FileWriter(myFile);
                writer.write(content);
                writer.close();
                log.info("Successfully written to file.");
            } else {
                Files.deleteIfExists(Path.of(filepath));
                if (!myFile.createNewFile()) {
                    log.error("Existing file {} was not deleted.", filepath);
                    throw new IllegalStateException("Existing file " + filepath + " was not deleted.");
                }
                log.info("File {} created.", filepath);
                FileWriter writer = new FileWriter(myFile);
                writer.write(content);
                writer.close();
                log.info("Successfully written to file.");
            }
        } catch (Exception e) {
            log.error("Error while writing file.");
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
            throw new IllegalStateException("Error while writing file.");
        }
        return true;
    }

    /**
     * tries to export the current state into a file
     * @return true if export successful
     */
    public boolean prepareShutdown() {
        String filepath = preferences.get("Filepath", System.getProperty("user.home"));
        return this.writeFile(filepath, this.password);
    }
}

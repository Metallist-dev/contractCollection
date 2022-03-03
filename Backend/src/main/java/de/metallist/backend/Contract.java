package de.metallist.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.*;


/**
 * Model, which represents the contract with their most important attributes
 *
 * @author Metallist-dev
 * @version 0.1
 *
 */
@Entity
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    @Id
    @Getter @Setter
    private int id;

    @Getter @Setter
    private String category;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private float expenses;

    /** number of payments per year (1, 2, 4, 12) */
    @Getter @Setter
    private int cycle;

    @Getter @Setter
    private String customerNr;

    @Getter @Setter
    private String contractNr;

    /** start of the contract in format "YYYY-MM-DD" */
    @Getter @Setter
    private String startDate;

    /** length of a contractual period in months */
    @Getter @Setter
    private int contractPeriod;

    /** length of a period of notice in weeks */
    @Getter @Setter
    private int periodOfNotice;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private String documentPath;


    /**
     * deletes a given contract
     * @param contractRepo holds the operations for the sql-database
     * @param id           ID of the contract (primary key)
     * @param name         name for second check
     * @return             boolean, which informs about success/failure
     * @throws NullPointerException if contract not available
     */
    public boolean deleteContract(Repository contractRepo, int id, String name) throws NullPointerException{
        Optional<Contract> vertrag = contractRepo.findById(id);
        if (vertrag.isEmpty() || !Objects.equals(vertrag.get().getName(), name)) throw new NullPointerException();
        if (vertrag.get().getName().equals(name)) {
            contractRepo.delete(vertrag.get());
            return true;
        } else {
            return false;
        }
    }

    /**
     * searches for a given contract in the database
     * @param contractRepo holds the operations for the sql-database
     * @param id           ID of the contract (primary key)
     * @return             object of the contract or null
     */
    public Contract getContract(Repository contractRepo, int id) {
        Optional<Contract> vertrag = contractRepo.findById(id);
        if (vertrag.isEmpty()) return null;
        else return vertrag.get();
    }

    /**
     * changes a given attribute of the contract
     * @param key   name of the attribute, which is supposed to be changed
     * @param value new value
     * @return      object of the contract or null
     */
    public Contract updateContract(String key, String value) {
        switch (key) {
            case "category": {
                this.category = value;
                break;
            }
            case "name": {
                this.name = value;
                break;
            }
            case "expenses": {
                this.expenses = Float.parseFloat(value);
                break;
            }
            case "cycle": {
                this.cycle = Integer.parseInt(value);
                break;
            }
            case "customerNr": {
                this.customerNr = value;
                break;
            }
            case "contractNr": {
                this.contractNr = value;
                break;
            }
            case "contractPeriod": {
                this.contractPeriod = Integer.parseInt(value);
                break;
            }
            case "periodOfNotice": {
                this.periodOfNotice = Integer.parseInt(value);
                break;
            }
            case "startDate": {
                this.startDate = value;
                break;
            }
            case "description": {
                this.description = value;
                break;
            }
            case "documentPath": {
                this.documentPath = value;
                break;
            }
            default: return null;
        }
        return this;
    }

    /**
     * creates a JSON from the instance
     * @return contract json
     */
    public JsonNode toJson() {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode node = mapper.createObjectNode();

        try {
            log.debug("Used fallback method to assemble response for addContract.");

            node.put("id", this.id);
            node.put("category", this.category);
            node.put("name", this.name);
            node.put("expenses", this.expenses);
            node.put("cycle", this.cycle);
            node.put("customerNr", this.customerNr);
            node.put("contractNr", this.contractNr);
            node.put("startDate", this.startDate);
            node.put("contractPeriod", this.contractPeriod);
            node.put("periodOfNotice", this.periodOfNotice);
            node.put("description", this.description);
            node.put("documentPath", this.documentPath);
        } catch (Exception e) {
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
        }
        return node;
    }

    public static Iterable<Contract> importContracts(Repository repo, JsonNode importJson) {
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
            for (Contract v : repo.findAll()) {
                if (v.getId() == maxID) maxID++;
            }

            contract.setId(maxID);
            try { repo.save(contract); }
            catch (IllegalArgumentException ex) {
                log.error(ex.getMessage());
                log.debug(Arrays.toString(ex.getStackTrace()));
            }
        }

        return repo.findAll();
    }
}


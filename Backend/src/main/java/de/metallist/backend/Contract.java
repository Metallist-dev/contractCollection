package de.metallist.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;


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
            case "typ": {
                this.category = value;
                break;
            }
            case "name": {
                this.name = value;
                break;
            }
            case "kosten": {
                this.expenses = Float.parseFloat(value);
                break;
            }
            case "turnus": {
                this.cycle = Integer.parseInt(value);
                break;
            }
            case "beschreibung": {
                this.description = value;
                break;
            }
            case "dokumentpfad": {
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
}


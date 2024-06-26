package de.metallistdev.contractcollection.commons;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Model, which represents the contract with their most important attributes
 *
 * @author Metallist-dev
 * @version 0.1
 *
 */
@Getter
@Setter
@Slf4j
@AllArgsConstructor
public class Contract {

    private int id;

    private String category;

    private String name;

    private float expenses;

    /** number of payments per year (1, 2, 4, 12) */
    private int cycle;

    private String customerNr;

    private String contractNr;

    /** start of the contract in format "YYYY-MM-DD" */
    private String startDate;

    /** length of a contractual period in months */
    private int contractPeriod;

    /** length of a period of notice in weeks */
    private int periodOfNotice;

    private String description;

    private String documentPath;


    public Contract() {
        this.id = 0;
        this.category = "";
        this.name = "";
        this.expenses = 0.0f;
        this.cycle = 0;
        this.customerNr = "";
        this.contractNr = "";
        this.startDate = "";
        this.contractPeriod = 0;
        this.periodOfNotice = 0;
        this.description = "";
        this.documentPath = "";
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
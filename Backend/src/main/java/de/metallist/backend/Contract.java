package de.metallist.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
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


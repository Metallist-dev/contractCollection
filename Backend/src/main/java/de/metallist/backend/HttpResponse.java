package de.metallist.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Arrays;

import static de.metallist.backend.ReasonCodes.*;

/**
 * implements a bunch of helper methods to fill response jsons
 *
 * @author Metallist-dev
 * @version 0.1
 */
@Slf4j
public class HttpResponse {
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private static final String BASEPATH = "static/templates/responses/";

    /**
     * fill response body for requesting a single contract (add, get, update)
     *
     * @param reasonCode reason code with message
     * @param contract   contract object to send in body
     * @return RC and message in head and requested or empty contract in body
     */
    public static JsonNode requestSingleContract(ReasonCodes reasonCode, Contract contract) {
        try {
            InputStream stream = JsonNode.class.getClassLoader().getResourceAsStream(BASEPATH + "singleContract.json");
            JsonNode node = mapper.readTree(stream);

            String jsonString = mapper.writeValueAsString(node);

            String newJsonString = jsonString
                    .replace("#MESSAGE#", reasonCode.getDescription())
                    .replace("#REASON-CODE#", reasonCode.getCodenumber())
                    .replace("#CATEGORY#", contract.getCategory())
                    .replace("#NAME#", contract.getName())
                    .replace("\"expenses\": 0.00", "\"expenses\": " + contract.getExpenses())
                    .replace("\"cycle\": 0", "\"cycle\": " + contract.getCycle())
                    .replace("#CUSTOMER#", contract.getCustomerNr())
                    .replace("#CONTRACT#", contract.getContractNr())
                    .replace("#STARTDATE#", contract.getStartDate())
                    .replace("\"contractPeriod\": 0", "\"cycle\": " + contract.getContractPeriod())
                    .replace("\"periodOfNotice\": 0", "\"periodOfNotice\": " + contract.getPeriodOfNotice())
                    .replace("#DESCRIPTION#", contract.getDescription())
                    .replace("#DOCUMENTS PATH#", contract.getDocumentPath());

            return mapper.readTree(newJsonString);
        } catch (Exception exception) {
            log.debug(exception.getMessage());
            log.debug(Arrays.toString(exception.getStackTrace()));
            exception.printStackTrace();

            ObjectNode root = mapper.createObjectNode();
            ObjectNode head = mapper.createObjectNode();

            head.put("message", reasonCode.getDescription());
            head.put("reasonCode", reasonCode.getCodenumber());

            root.set("head", head);
            root.set("body", contract.toJson());

            return root;
        }
    }

    /**
     * fills the response body for contract deletion
     *
     * @param reasonCode RC which has to be returned
     * @return Json with RC and message in head and empty body
     */
    public static JsonNode requestDeleteContract(ReasonCodes reasonCode) {
        try {
            InputStream stream = JsonNode.class.getClassLoader().getResourceAsStream(BASEPATH + "singleContract.json");
            JsonNode node = mapper.readTree(stream);

            String jsonString = mapper.writeValueAsString(node);

            String newJsonString = jsonString
                    .replace("#MESSAGE#", reasonCode.getDescription())
                    .replace("#REASON-CODE#", reasonCode.getCodenumber());

            return mapper.readTree(newJsonString);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            log.debug(Arrays.toString(exception.getStackTrace()));
            exception.printStackTrace();

            ObjectNode root = mapper.createObjectNode();
            ObjectNode head = mapper.createObjectNode();
            ObjectNode body = mapper.createObjectNode();

            head.put("message", reasonCode.getDescription());
            head.put("reasonCode", reasonCode.getCodenumber());

            root.set("head", head);
            root.set("body", body);

            return root;
        }
    }

    /**
     * fills the response for returning all contracts
     *
     * @param contracts list of contracts
     * @return RC and message in head and list of contracts in body
     */
    public static JsonNode requestGetAllContracts(ReasonCodes reasonCode, Iterable<Contract> contracts) {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode body = mapper.createArrayNode();

        try {
            if (reasonCode == RC_IMPORT_FAILED) throw new RuntimeException(RC_IMPORT_FAILED.getDescription());
            InputStream stream = JsonNode.class.getClassLoader().getResourceAsStream(BASEPATH + "allContracts.json");
            JsonNode node = mapper.readTree(stream);
            String jsonString = mapper.writeValueAsString(node);

            String newJsonString = jsonString
                    .replace("#MESSAGE#", reasonCode.getDescription())
                    .replace("#REASON-CODE#", reasonCode.getCodenumber());

            root.set("head", mapper.readTree(newJsonString).get("head"));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            log.debug(Arrays.toString(exception.getStackTrace()));
            exception.printStackTrace();

            ObjectNode head = mapper.createObjectNode();

            head.put("message", reasonCode.getDescription());
            head.put("reasonCode", reasonCode.getCodenumber());

            root.set("head", head);
        } finally {
            for (Contract contract : contracts) body.add(contract.toJson());
            root.set("body", body);
        }
        return root;
    }
}

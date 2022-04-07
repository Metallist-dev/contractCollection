package de.metallist.backend;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

import static de.metallist.backend.utilities.ReasonCodes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.*;

/**
 * Test class for contract requests
 *
 * @author Metallist-dev
 * @version 0.1
 */
@SpringBootTest(classes = BackendApplication.class)
public class TestContract extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private int smallestID = -1;

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void test_00_newContract() throws Exception {
        ObjectNode requestJson = mapper.createObjectNode();
        requestJson.put("category", "insurance");
        requestJson.put("name", "health insurance");
        requestJson.put("expenses", 100);
        requestJson.put("cycle", 12);
        requestJson.put("customerNr", "12345");
        requestJson.put("contractNr", "67890");
        requestJson.put("startDate", "2022-01-01");
        requestJson.put("contractPeriod", 1);
        requestJson.put("periodOfNotice", 2);
        requestJson.put("description", "public health insurance - student tariff");
        requestJson.put("documentPath", "/home/mhenke/Schreibtisch");

        mockMvc.perform(post("/demo/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestJson))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String answer = result.getResponse().getContentAsString();
                    JsonNode answerJson = mapper.readTree(answer);
                    assertEquals(answerJson.get("head").get("reasonCode").textValue(), RC_CREATE_SUCCESS.getCodenumber());
                    assertFalse(answerJson.get("body").isEmpty());
                });
    }

    @Test
    public void test_01_deleteContract() throws Exception {
        this.test_00_newContract();

        AtomicReference<JsonNode> contractJson = new AtomicReference<>();

        mockMvc.perform(get("/demo/all"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String answer = result.getResponse().getContentAsString();
                    contractJson.set(mapper.readTree(answer).get("body"));
                    assertFalse(answer.isEmpty());
                });

        ObjectNode requestJson = mapper.createObjectNode();
        while (contractJson.get() == null) {wait(1000);}
        JsonNode json = contractJson.get();
        requestJson.put("id", contractJson.get().get(0).get("id").asInt());
        requestJson.put("name", contractJson.get().get(0).get("name").asText());

        mockMvc.perform(post("/demo/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestJson))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    JsonNode responseJson = mapper.readTree(result.getResponse().getContentAsString());
                    assertEquals(responseJson.get("head").get("reasonCode").asText(), RC_DELETE_SUCCESS.getCodenumber());
                    assertFalse(responseJson.get("body").isEmpty());
                });
    }

    @Test
    public void test_02_readAllContracts() throws Exception {

        mockMvc.perform(get("/demo/all"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    JsonNode answer = mapper.readTree(result.getResponse().getContentAsString());
                    assertEquals(answer.get("head").get("reasonCode").asText(), RC_GENERAL_SUCCESS.getCodenumber());
                    assertFalse(answer.get("body").isEmpty());

                    smallestID = answer.get("body").get(0).get("id").asInt();
                });
    }

    @Test
    public void test_03_readSingleContract() throws Exception {
        this.test_02_readAllContracts();

        mockMvc.perform(get("/demo/get/" + smallestID))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    JsonNode answer = mapper.readTree(result.getResponse().getContentAsString());
                    assertEquals(answer.get("head").get("reasonCode").asText(), RC_GENERAL_SUCCESS.getCodenumber());
                    assertFalse(answer.get("body").isEmpty());
                });
    }

    @Test
    public void test_04_updateContract() throws Exception {
        if (smallestID == -1) this.test_02_readAllContracts();
        String newValue = "Testversicherung";

        ObjectNode requestJson = mapper.createObjectNode();
        requestJson.put("id", smallestID);
        requestJson.put("key", "name");
        requestJson.put("value", newValue);

        mockMvc.perform(patch("/demo/change/"+ smallestID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestJson))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    JsonNode answer = mapper.readTree(result.getResponse().getContentAsString());
                    assertEquals(answer.get("head").get("reasonCode").asText(), RC_UPDATE_SUCCESS.getCodenumber());
                    assertFalse(answer.get("body").isEmpty());
                    assertEquals(answer.get("body").get("name").textValue(), newValue);
                });
    }

    @Test
    public void test_05_importContracts() throws Exception {
        String filepath = "/home/mhenke/Programmierung/ContractCollection/Backend/src/test/resources/testImport.json";

        ObjectNode requestJson = mapper.createObjectNode();
        requestJson.put("filepath", filepath);
        requestJson.put("overwrite", true);

        mockMvc.perform(put("/demo/import")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestJson))
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    JsonNode answer = mapper.readTree(result.getResponse().getContentAsString());
                    assertEquals(answer.get("head").get("reasonCode").textValue(), RC_IMPORT_SUCCESS.getCodenumber());
                    assertFalse(answer.get("body").isEmpty());
                    assertEquals(answer.get("body").get(0).get("id").intValue(), 1);
                });
    }

    @Test
    public void test_06_exportContracts() throws Exception {
        this.test_05_importContracts();

        String filePath = "/home/mhenke/Programmierung/ContractCollection/Backend/src/test/resources/testExport.json";

        Files.deleteIfExists(Paths.get(filePath));

        ObjectNode requestJson = mapper.createObjectNode();
        requestJson.put("filepath", filePath);
        JsonNode contracts = mapper.readTree("[{\n    \"id\": 1,\n    \"category\": \"insurance\",\n    \"name\": \"health insurance\",\n    \"expenses\": 100,\n    \"cycle\": 12,\n    \"customerNr\": \"12345\",\n    \"contractNr\": \"67890\",\n    \"startDate\": \"2022-01-01\",\n    \"contractPeriod\": 1,\n    \"periodOfNotice\": 2,\n    \"description\": \"public health insurance - student tariff\",\n    \"documentPath\": \"/home/user/example\"\n  },\n  {\n    \"id\": 2,\n    \"category\": \"insurance\",\n    \"name\": \"health insurance\",\n    \"expenses\": 100,\n    \"cycle\": 12,\n    \"customerNr\": \"12345\",\n    \"contractNr\": \"67890\",\n    \"startDate\": \"2022-01-01\",\n    \"contractPeriod\": 1,\n    \"periodOfNotice\": 2,\n    \"description\": \"public health insurance - student tariff\",\n    \"documentPath\": \"/home/user/example\"\n  }]");
        requestJson.set("contracts", contracts);

        mockMvc.perform(post("/demo/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestJson))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    JsonNode answer = mapper.readTree(result.getResponse().getContentAsString());
                    assertEquals(answer.get("head").get("reasonCode").textValue(), RC_EXPORT_SUCCESS.getCodenumber());
                    assertTrue(Files.exists(Paths.get(filePath)));
                    String content = Files.readString(Paths.get(filePath));
                    assertFalse(content.isEmpty());
                });
    }
}

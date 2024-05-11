package de.metallistdev.contractcollection.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.metallistdev.contractcollection.application.utilities.SessionUtil;
import de.metallistdev.contractcollection.commons.Contract;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;

import static de.metallistdev.contractcollection.application.utilities.ReasonCodes.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Test class for backend controller
 *
         * @author Metallist-dev
 * @version 0.2
        */
@SpringBootTest
public class MainControllerTest extends AbstractTestNGSpringContextTests {

    @Mock
    SessionUtil session;

    @InjectMocks
    MainController controller;

    MockHttpServletRequest request;

    private Contract testContract;
    private Contract testContract2;

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @BeforeClass
    public void setup() {
        this.testContract = new Contract(1000, "insurance", "test insurance", 100.0f, 1,
                "123abc", "789xyz", "2022-01-01",12, 2,
                "test description", "/path/to/stuff");
        this.testContract2 = new Contract(1000, "insurance", "test insurance #2", 200.0f,
                1, "123abc", "456qwe", "2022-03-01",12, 2,
                "test description", "/path/to/other/stuff");
    }

    @BeforeMethod
    public void init() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        controller = new MainController(session);
    }

    @Test
    public void test_00_newContract() {
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

        when(session.addContract(any(Contract.class))).thenReturn(true);

        ResponseEntity<JsonNode> response = controller.addContract(requestJson);

        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().get("head").get("reasonCode").asText(), RC_CREATE_SUCCESS.getCodenumber());


        when(session.addContract(any(Contract.class))).thenReturn(false);

        response = controller.addContract(requestJson);

        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(response.getBody().get("head").get("reasonCode").asText(), RC_CREATE_ERROR.getCodenumber());
    }

    @Test
    public void test_01_deleteContract() throws JsonProcessingException {
        System.out.println(session.getContracts());
        JsonNode requestJson = mapper.readTree("{\"id\": 1, \"name\": \"health insurance\"}");

        when(session.removeContract(anyInt())).thenReturn(true);
        when(session.removeContract(any(Contract.class))).thenReturn(true);
        ResponseEntity<JsonNode> response = controller.deleteContract(requestJson);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().get("head").get("reasonCode").asText(), RC_DELETE_SUCCESS.getCodenumber());

        when(session.removeContract(anyInt())).thenReturn(false);
        when(session.removeContract(any())).thenReturn(true);
        response = controller.deleteContract(requestJson);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().get("head").get("reasonCode").asText(), RC_DELETE_SUCCESS.getCodenumber());

        when(session.removeContract(anyInt())).thenReturn(false);
        when(session.removeContract(any())).thenReturn(false);
        response = controller.deleteContract(requestJson);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(response.getBody().get("head").get("reasonCode").asText(), RC_DELETE_ERROR.getCodenumber());

        when(session.removeContract(anyInt())).thenThrow(NullPointerException.class);
        response = controller.deleteContract(requestJson);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(response.getBody().get("head").get("reasonCode").asText(), RC_DELETE_MISSING.getCodenumber());

    }

    @Test
    public void test_02_readAllContracts() {
        String filepath = new File("src/test/resources/testImport.json").getAbsolutePath();
        ArrayList<Contract> contracts = new ArrayList<>();
        JsonNode json;
        try {
            json = mapper.readTree(new File(filepath));
            if (json.isArray()) {
                for (JsonNode node : json) {
                    Contract contract = mapper.convertValue(node, Contract.class);
                    contracts.add(contract);
                }
            } else throw new RuntimeException("Input json is no array.");
        } catch (Exception ignore) {
            fail();
        }

        when(session.getContracts()).thenReturn(contracts);

        ResponseEntity<JsonNode> response = controller.getAllContracts();

        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().get("head").get("reasonCode").asText(), RC_GENERAL_SUCCESS.getCodenumber());
    }

    @Test
    public void test_03_readSingleContract() {

        when(session.getSingleContract(anyInt())).thenReturn(testContract);
        ResponseEntity<JsonNode> response = controller.getSingleContract(1000);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().get("head").get("reasonCode").asText(), RC_GENERAL_SUCCESS.getCodenumber());

        when(session.getSingleContract(anyInt())).thenReturn(null);
        response = controller.getSingleContract(0);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(response.getBody().get("head").get("reasonCode").asText(), RC_GENERAL_ERROR.getCodenumber());
    }

    @Test
    public void test_04_updateContract() {
        String newValue = "testing insurance";

        ObjectNode requestJson = mapper.createObjectNode();
        requestJson.put("id", 1000);
        requestJson.put("key", "name");
        requestJson.put("value", newValue);

        Contract updatedContract = testContract;
        updatedContract.setName(newValue);

        when(session.updateContract(anyInt(), anyString(), anyString())).thenReturn(updatedContract);
        ResponseEntity<JsonNode> response = controller.updateContract(1000, requestJson);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().get("body").get("name").asText(), newValue);

        when(session.updateContract(anyInt(), anyString(), anyString())).thenReturn(null);
        response = controller.updateContract(1000, requestJson);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(response.getBody().get("head").get("reasonCode").asText(), RC_UPDATE_ERROR.getCodenumber());
    }

    @Test
    public void test_05_importContracts() {
        ArrayList<Contract> mockList = new ArrayList<>();
        mockList.add(testContract);
        mockList.add(testContract2);
        String filepath = new File("src/test/resources/testExportedFile.txt").getAbsolutePath();

        ObjectNode requestJson = mapper.createObjectNode();
        requestJson.put("filepath", filepath);
        requestJson.put("password", "123superSecret!");
        requestJson.put("overwrite", true);

        doNothing().when(session).removeAllContracts();
        when(session.loadFile(anyString(), anyString())).thenReturn(mockList);
        ResponseEntity<JsonNode> response = controller.importFile(requestJson);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().get("body").get(0).get("name").asText(), "testing insurance");

        doNothing().when(session).removeAllContracts();
        when(session.loadFile(anyString(), anyString())).thenReturn(null);
        response = controller.importFile(requestJson);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(response.getBody().get("head").get("reasonCode").asText(), RC_IMPORT_FAILED.getCodenumber());
    }

    @Test
    public void test_06_exportContracts() throws JsonProcessingException {
        String filepath = new File("src/test/resources/testExportedFile.txt").getAbsolutePath();

        ObjectNode requestJson = mapper.createObjectNode();
        requestJson.put("filepath", filepath);
        requestJson.put("password", "123superSecret!");
        JsonNode contracts = mapper.readTree("[{    \"id\": 1,    \"category\": \"insurance\",    \"name\": \"health insurance\",    \"expenses\": 100,    \"cycle\": 12,    \"customerNr\": \"12345\",    \"contractNr\": \"67890\",    \"startDate\": \"2022-01-01\",    \"contractPeriod\": 1,    \"periodOfNotice\": 2,    \"description\": \"public health insurance - student tariff\",    \"documentPath\": \"/home/user/example\"  },  {    \"id\": 2,    \"category\": \"insurance\",    \"name\": \"health insurance\",    \"expenses\": 100,    \"cycle\": 12,    \"customerNr\": \"12345\",    \"contractNr\": \"67890\",    \"startDate\": \"2022-01-01\",    \"contractPeriod\": 1,    \"periodOfNotice\": 2,    \"description\": \"public health insurance - student tariff\",    \"documentPath\": \"/home/user/example\"  }]");
        requestJson.set("contracts", contracts);

        when(session.writeFile(anyString(), anyString())).thenReturn(true);
        ResponseEntity<JsonNode> response = controller.exportToFile(requestJson);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().get("head").get("reasonCode").asText(), RC_EXPORT_SUCCESS.getCodenumber());

        when(session.writeFile(anyString(), anyString())).thenReturn(false);
        response = controller.exportToFile(requestJson);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(response.getBody().get("head").get("reasonCode").asText(), RC_EXPORT_FAILED.getCodenumber());
    }

    @Test
    public void test_07_shutdown() {
        when(session.prepareShutdown()).thenReturn(true);
        ResponseEntity<JsonNode> response = controller.prepareShutdown();
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        when(session.prepareShutdown()).thenReturn(false);
        response = controller.prepareShutdown();
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

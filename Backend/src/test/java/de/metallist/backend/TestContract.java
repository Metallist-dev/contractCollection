package de.metallist.backend;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.*;

@SpringBootTest(classes = BackendApplication.class)
public class TestContract extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private int smallestID = -1;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void test_00_newContract() throws Exception {
        String category = "category=Versicherung";
        String name = "&name=Krankenversicherung";
        String expenses = "&expenses=108";
        String cycle = "&cycle=4";
        String customerNr = "&customerNr=12345";
        String contractNr = "&contractNr=56789";
        String startDate = "&startDate=2020-11-16";
        String contractPeriod = "&contractPeriod=1";
        String periodOfNotice = "&periodOfNotice=2";
        String description = "&description=gesetzliche+Krankenversicherung+-+Studententarif";
        String documentPath = "&documentPath=%2Fhome%2Fmhenke%2FSchreibtisch";

        //mockMvc.perform(post("/demo/add?typ=Versicherung&name=Krankenversicherung&kosten=108&turnus=4&beschreibung=Test&dokumentpfad=Test"))
        mockMvc.perform(post("/demo/add?" + category + name + expenses + cycle + customerNr + contractNr +
                        startDate + contractPeriod + periodOfNotice + description + documentPath))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String answer = result.getResponse().getContentAsString();
                    System.out.println(answer);
                    assertFalse(answer.contains("Error"));
                });
    }

    @Test
    public void test_01_deleteContract() throws Exception {
        this.test_00_newContract();

        ObjectMapper mapper = new ObjectMapper();
        AtomicReference<String> dataJson = new AtomicReference<>("");
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        mockMvc.perform(get("/demo/all"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String answer = result.getResponse().getContentAsString();
                    String outputJson = mapper.writeValueAsString(mapper.readTree(answer));
                    dataJson.set(outputJson);
                    System.out.println(outputJson);
                    assertFalse(answer.isEmpty());
                });

        JsonNode idNode = mapper.readValue(dataJson.get(), JsonNode.class).get(0).get("id");
        String id = "&id=" + idNode.asText();
        String name = "&name=Krankenversicherung";

        mockMvc.perform(post("/demo/delete?" + id + name))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    boolean fehler = result.getResponse().getContentAsString().contains("Error");
                    if (fehler) fail();
                });
    }

    @Test
    public void test_02_readAllContracts() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        mockMvc.perform(get("/demo/all"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String answer = result.getResponse().getContentAsString();
                    String outputJson = mapper.writeValueAsString(mapper.readTree(answer));
                    System.out.println(outputJson);
                    assertFalse(answer.isEmpty());

                    JsonNode root = mapper.readTree(answer);
                    smallestID = Integer.parseInt(root.get(0).get("id").asText());
                });
    }

    @Test
    public void test_03_readSingleContract() throws Exception {
        if (smallestID == -1) this.test_02_readAllContracts();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        mockMvc.perform(get("/demo/get?id=" + smallestID))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String answer = result.getResponse().getContentAsString();
                    String outputJson = mapper.writeValueAsString(mapper.readTree(answer));
                    System.out.println(outputJson);
                    assertFalse(answer.isEmpty());
                    assertFalse(answer.contains("Error"));
                });
    }

    @Test
    public void test_04_updateContract() throws Exception {
        if (smallestID == -1) this.test_02_readAllContracts();

        mockMvc.perform(patch("/demo/change/"+ smallestID +"?key=name&value=Testversicherung"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String answer = result.getResponse().getContentAsString();
                    System.out.println(answer);
                    assertFalse(answer.isEmpty());
                    assertFalse(answer.contains("Error"));
                });
    }
}

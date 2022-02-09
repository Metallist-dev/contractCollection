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
public class TestVertrag extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private int smallestID = -1;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void test_00_neuerVertrag() throws Exception {
        String typ = "typ=Versicherung";
        String name = "&name=Krankenversicherung";
        String kosten = "&kosten=108";
        String turnus = "&turnus=4";
        String beschreibung = "&beschreibung=gesetzliche+Krankenversicherung+-+Studententarif";
        String dokumentpfad = "&dokumentpfad=%2Fhome%2Fmhenke%2FSchreibtisch";

        //mockMvc.perform(post("/demo/add?typ=Versicherung&name=Krankenversicherung&kosten=108&turnus=4&beschreibung=Test&dokumentpfad=Test"))
        mockMvc.perform(post("/demo/add?" + typ + name + kosten + turnus + beschreibung + dokumentpfad))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String answer = result.getResponse().getContentAsString();
                    System.out.println(answer);
                    assertFalse(answer.contains("Fehler"));
                });
    }

    @Test
    public void test_01_vertragLoeschen() throws Exception {
        this.test_00_neuerVertrag();

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
                    boolean fehler = result.getResponse().getContentAsString().contains("Fehler");
                    if (fehler) fail();
                });
    }

    @Test
    public void test_02_vertraegeLesen() throws Exception {
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
    public void test_03_vertragLesen() throws Exception {
        if (smallestID == -1) this.test_02_vertraegeLesen();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        mockMvc.perform(get("/demo/get?id=" + smallestID))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String answer = result.getResponse().getContentAsString();
                    String outputJson = mapper.writeValueAsString(mapper.readTree(answer));
                    System.out.println(outputJson);
                    assertFalse(answer.isEmpty());
                    assertFalse(answer.contains("Fehler"));
                });
    }

    @Test
    public void test_04_datenAendern() throws Exception {
        if (smallestID == -1) this.test_02_vertraegeLesen();

        mockMvc.perform(patch("/demo/change/"+ smallestID +"?key=name&value=Testversicherung"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String answer = result.getResponse().getContentAsString();
                    System.out.println(answer);
                    assertFalse(answer.isEmpty());
                    assertFalse(answer.contains("Fehler"));
                });
    }
}

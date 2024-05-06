package de.metallistdev.contractcollection.javafx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.metallistdev.contractcollection.commons.Contract;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MainView {

    Logger logger;

    @FXML
    private MenuBar menuBar;

    @FXML
    private TableView<Contract> table;

    @FXML
    private TableColumn<Contract,String> nameColumn;

    @FXML
    private TableColumn<Contract,String> typeColumn;

    @FXML
    private TableColumn<Contract,String> expensesColumn;

    @FXML
    private TableColumn<Contract,String> actionsColumn;

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);


    public void initialize() {
        logger = LoggerFactory.getLogger(MainView.class);

        final OkHttpClient client = new OkHttpClient();

        String body = "<empty>";
        JsonNode json;
        ObservableList<Contract> contracts = FXCollections.observableArrayList();

        Request request = new Request.Builder()
                .url("http://localhost:8080/all")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("An error occurred: " + response);

            if (response.body() != null) {
                body = response.body().string();
            }
            logger.debug("response from backend: {}", body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            json = mapper.readTree(body).get("body");
            if (json.isArray()) {
                for (JsonNode node : json) {
                    Contract contract = mapper.convertValue(node, Contract.class);
                    contracts.add(contract);
                }
            } else {
                logger.debug("{}", json);
                throw new RuntimeException("Input json is no array.");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        expensesColumn.setCellValueFactory(new PropertyValueFactory<>("expenses"));

        table.getColumns().addAll(nameColumn, typeColumn, expensesColumn);
        table.setItems(contracts);
    }
}

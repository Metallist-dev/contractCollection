package de.metallistdev.contractcollection.javafx;

import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.*;

import javafx.stage.Stage;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoginView {

    Logger logger;

    @FXML
    private TextField filepathInput;

    @FXML
    private PasswordField passwordInput;

    public void initialize() {
        logger = LoggerFactory.getLogger(LoginView.class);
    }

    public void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(filepathInput.getScene().getWindow());
        if (file == null)
            new Preloader.ErrorNotification(null, "File not loaded", new Throwable("File not loaded."));
        else
            filepathInput.setText(file.getPath());
    }

    public void handlePasswordCommit2 () {
        final MediaType mediaType = MediaType.parse("application/json");
        final OkHttpClient client = new OkHttpClient();

        String filepath = filepathInput.getText();
        String password = passwordInput.getText();
        String reqBody = "{\"filepath\": \"" + filepath + "\", " +
                "\"password\": \"" + password + "\", \"overwrite\": true}";

        Request request = new Request.Builder()
                .url("http://localhost:8080/import")
                .put(RequestBody.create(reqBody, mediaType))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("An error occurred: " + response);
            logger.debug("{}", response.body());

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ContractCollectionApplication.class.getResource("main-view.fxml"));
                Parent parent = (Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(parent));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package de.metallistdev.contractcollection.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContractCollectionApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(ContractCollectionApplication.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ContractCollectionApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            primaryStage.setTitle("Contract Collection");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            log.debug("Error loading FXML: {}", e.getMessage());
        }
    }
}

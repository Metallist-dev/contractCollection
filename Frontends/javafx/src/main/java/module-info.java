module de.metallist.contractcollection.javafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires okhttp3;
    requires kotlin.stdlib;

    requires de.metallist.contractcollection.commons;
    requires org.slf4j;

    opens de.metallistdev.contractcollection.javafx to javafx.fxml;
    exports de.metallistdev.contractcollection.javafx;
}
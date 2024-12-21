module org.example.tetetete {
    requires org.slf4j;
    //requires org.logback;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires spring.security.crypto;
    requires spring.security.core;


    opens org.example.tetetete.client to javafx.fxml;
    opens org.example.tetetete.server to javafx.fxml;
    exports org.example.tetetete.server;
    exports org.example.tetetete.client;
}
package org.example.tetetete.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChatClient extends Application {
    private static final Logger logger = LoggerFactory.getLogger(ChatClient.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загружаем FXML файл логина и получаем корневой элемент
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tetetete/login.fxml"));
        Parent root = loader.load();

        // Получаем контроллер логина
        LoginController loginController = loader.getController();
        loginController.setPrimaryStage(primaryStage);

        // Настраиваем и отображаем основное окно приложения
        primaryStage.setTitle("Chat Client - Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        logger.info("Chat client started");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

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
    private static String username;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загружаем FXML файл выбора и получаем корневой элемент
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tetetete/choice.fxml"));
        Parent root = loader.load();

        // Получаем контроллер выбора
        ChoiceController choiceController = loader.getController();
        choiceController.setPrimaryStage(primaryStage);

        // Настраиваем и отображаем основное окно приложения
        primaryStage.setTitle("Chat Client - Choice");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        logger.info("Chat client started");
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setUsername(String username) {
        ChatClient.username = username;
    }

    public static String getUsername() {
        return username;
    }
}

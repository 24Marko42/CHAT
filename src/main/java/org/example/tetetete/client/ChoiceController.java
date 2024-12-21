package org.example.tetetete.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChoiceController {
    private static final Logger logger = LoggerFactory.getLogger(ChoiceController.class);

    @FXML
    private Button loginButton; // Кнопка для перехода к логину

    @FXML
    private Button registerButton; // Кнопка для перехода к регистрации

    private Stage primaryStage; // Основное окно приложения

    @FXML
    public void initialize() {
        // Устанавливаем обработчик событий для кнопки логина
        loginButton.setOnAction(event -> openLoginWindow());

        // Устанавливаем обработчик событий для кнопки регистрации
        registerButton.setOnAction(event -> openRegisterWindow());
    }

    // Метод для открытия окна логина
    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tetetete/login.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setPrimaryStage(primaryStage);

            Stage loginStage = new Stage();
            loginStage.setTitle("Chat Client - Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();

            // Закрываем окно выбора
            primaryStage.close();
        } catch (IOException e) {
            logger.error("Error while opening login window", e); // Логируем ошибку при открытии окна логина
        }
    }

    // Метод для открытия окна регистрации
    private void openRegisterWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tetetete/register.fxml"));
            Parent root = loader.load();
            RegisterController registerController = loader.getController();
            registerController.setPrimaryStage(primaryStage);

            Stage registerStage = new Stage();
            registerStage.setTitle("Chat Client - Register");
            registerStage.setScene(new Scene(root));
            registerStage.show();

            // Закрываем окно выбора
            primaryStage.close();
        } catch (IOException e) {
            logger.error("Error while opening register window", e); // Логируем ошибку при открытии окна регистрации
        }
    }

    // Метод для установки основного окна приложения
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}

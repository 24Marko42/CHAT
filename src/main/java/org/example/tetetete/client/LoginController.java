package org.example.tetetete.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField usernameField; // Поле для ввода имени пользователя

    @FXML
    private PasswordField passwordField; // Поле для ввода пароля

    @FXML
    private Button loginButton; // Кнопка для входа

    @FXML
    private Button registerButton; // Кнопка для перехода к регистрации

    private Stage primaryStage; // Основное окно приложения

    @FXML
    public void initialize() {
        // Устанавливаем обработчик событий для кнопки входа
        loginButton.setOnAction(event -> login());

        // Устанавливаем обработчик событий для текстового поля ввода имени пользователя
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });

        // Устанавливаем обработчик событий для текстового поля ввода пароля
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });

        // Устанавливаем обработчик событий для кнопки регистрации
        registerButton.setOnAction(event -> openRegisterWindow());
    }

    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (authenticate(username, password)) {
            ChatClient.setUsername(username); // Сохраняем имя пользователя
            openChatWindow(); // Открываем окно чата после успешного входа
            primaryStage.close(); // Закрываем окно логина
        } else {
            logger.warn("Authentication failed for user: {}", username); // Логируем неудачную попытку входа
        }
    }

    // Метод для аутентификации пользователя (пример)
    private boolean authenticate(String username, String password) {
        // Здесь можно реализовать логику аутентификации
        // Например, можно использовать UserService для аутентификации
        UserService userService = new UserService();
        try {
            userService.authenticate(username, password);
            return true;
        } catch (InvalidCredentialsException e) {
            logger.error("Invalid credentials", e);
            return false;
        }
    }

    // Метод для открытия окна чата
    private void openChatWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tetetete/chat.fxml"));
            Parent root = loader.load();
            ChatController chatController = loader.getController();

            // Создаем ClientSocketHandler и передаем ему контроллер чата
            ClientSocketHandler socketHandler = new ClientSocketHandler("localhost", 8080, chatController);
            chatController.setSocketHandler(socketHandler);

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat Client");
            chatStage.setScene(new Scene(root));
            chatStage.show();
        } catch (IOException e) {
            logger.error("Error while opening chat window", e); // Логируем ошибку при открытии окна чата
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

            // Закрываем окно логина
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

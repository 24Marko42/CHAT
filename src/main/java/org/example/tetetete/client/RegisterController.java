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
import org.example.tetetete.common.exception.UserAlreadyExistsException;
import org.example.tetetete.server.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @FXML
    private TextField usernameField; // Поле для ввода имени пользователя

    @FXML
    private PasswordField passwordField; // Поле для ввода пароля

    @FXML
    private Button registerButton; // Кнопка для регистрации

    @FXML
    private Button backToLoginButton; // Кнопка для возврата к логину

    private Stage primaryStage; // Основное окно приложения

    @FXML
    public void initialize() {
        // Устанавливаем обработчик событий для кнопки регистрации
        registerButton.setOnAction(event -> register());

        // Устанавливаем обработчик событий для текстового поля ввода имени пользователя
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                register();
            }
        });

        // Устанавливаем обработчик событий для текстового поля ввода пароля
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                register();
            }
        });

        // Устанавливаем обработчик событий для кнопки возврата к логину
        backToLoginButton.setOnAction(event -> openLoginWindow());
    }

    private void register() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (register(username, password)) {
            ChatClient.setUsername(username); // Сохраняем имя пользователя
            openLoginWindow(); // Открываем окно логина после успешной регистрации
            primaryStage.close(); // Закрываем окно регистрации
        } else {
            logger.warn("Registration failed for user: {}", username); // Логируем неудачную попытку регистрации
        }
    }

    // Метод для регистрации пользователя (пример)
    private boolean register(String username, String password) {
        // Здесь можно реализовать логику регистрации
        // Например, можно использовать UserService для регистрации
        UserService userService = new UserService();
        try {
            userService.register(username, password);
            return true;
        } catch (UserAlreadyExistsException e) {
            logger.error("User already exists", e);
            return false;
        }
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
        } catch (IOException e) {
            logger.error("Error while opening login window", e); // Логируем ошибку при открытии окна логина
        }
    }

    // Метод для установки основного окна приложения
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}

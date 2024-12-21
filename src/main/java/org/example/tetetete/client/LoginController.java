package org.example.tetetete.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

        // Обработка клавиши ENTER в полях ввода
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });

        // Переход к окну регистрации
        registerButton.setOnAction(event -> openRegisterWindow());
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (!validateInputs(username, password)) {
            showError("Ошибка входа", "Имя пользователя и пароль не могут быть пустыми.");
            return;
        }

        if (authenticate(username, password)) {
            ChatClient.setUsername(username); // Сохраняем имя пользователя
            openChatWindow(); // Открываем окно чата после успешного входа
            primaryStage.close(); // Закрываем окно логина
        } else {
            logger.warn("Authentication failed for user: {}", username);
            showError("Ошибка входа", "Неверное имя пользователя или пароль.");
        }
    }

    // Метод для проверки введённых данных
    private boolean validateInputs(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

    // Метод для аутентификации пользователя
    private boolean authenticate(String username, String password) {
        // Здесь должна быть ваша логика проверки, например, через сервер
        // Для тестирования допустим любой непустой ввод
        return !username.isEmpty() && !password.isEmpty();
    }

    // Метод для открытия окна чата
    private void openChatWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tetetete/chat.fxml"));
            Parent root = loader.load();
            ChatController chatController = loader.getController();

            // Создаем ClientSocketHandler и передаем его в ChatController
            ClientSocketHandler socketHandler = new ClientSocketHandler("localhost", 8080, chatController);
            chatController.setSocketHandler(socketHandler);

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat Client");
            chatStage.setScene(new Scene(root));
            chatStage.show();
        } catch (IOException e) {
            logger.error("Error while opening chat window", e);
            showError("Ошибка", "Не удалось открыть окно чата.");
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

            primaryStage.close();
        } catch (IOException e) {
            logger.error("Error while opening register window", e);
            showError("Ошибка", "Не удалось открыть окно регистрации.");
        }
    }

    // Показать сообщение об ошибке
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Установка основного окна
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Сброс полей ввода
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }
}

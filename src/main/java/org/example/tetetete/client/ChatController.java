package org.example.tetetete.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @FXML
    private TextArea chatArea; // Область для отображения сообщений чата

    @FXML
    private TextField messageField; // Поле для ввода сообщений

    @FXML
    private Button sendButton; // Кнопка для отправки сообщений

    @FXML
    private Label userCountLabel; // Метка для отображения количества пользователей

    @FXML
    private Button logoutButton; // Кнопка для выхода

    private ClientSocketHandler socketHandler; // Обработчик сокета для взаимодействия с сервером

    @FXML
    public void initialize() {
        // Устанавливаем обработчик событий для кнопки отправки сообщений
        sendButton.setOnAction(event -> sendMessage());

        // Устанавливаем обработчик событий для текстового поля ввода сообщений
        messageField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });

        // Устанавливаем обработчик событий для кнопки выхода
        logoutButton.setOnAction(event -> showLogoutConfirmation());
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (message != null && !message.trim().isEmpty()) {
            socketHandler.sendMessage(message); // Отправляем сообщение на сервер
            messageField.clear(); // Очищаем поле ввода
        }
    }

    private void showLogoutConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Вы точно хотите выйти?");
        alert.setContentText("Вы действительно хотите выйти из чата?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    logout();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void logout() throws IOException {
        // Логика для выхода из чата
        socketHandler.close();
        // Закрываем окно чата
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
        // Полное завершение программы
        System.exit(0);
    }

    // Метод для установки обработчика сокета
    public void setSocketHandler(ClientSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    // Метод для добавления сообщения в область чата
    public void appendMessage(String message) {
        chatArea.appendText(message + "\n");
    }

    // Метод для обновления количества подключенных пользователей
    public void updateUserCount(int userCount) {
        if (userCountLabel != null) {
            userCountLabel.setText("Users online: " + userCount);
        }
    }
}

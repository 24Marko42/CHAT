package org.example.tetetete.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientSocketHandler.class);

    private Socket socket; // Сокет для соединения с сервером
    private PrintWriter out; // PrintWriter для отправки сообщений
    private BufferedReader in; // BufferedReader для получения сообщений
    private ChatController chatController; // Контроллер чата для обновления интерфейса

    // Конструктор, который устанавливает соединение с сервером и инициализирует потоки ввода/вывода
    public ClientSocketHandler(String host, int port, ChatController chatController) throws IOException {
        this.socket = new Socket(host, port); // Создаем сокет и подключаемся к серверу
        this.out = new PrintWriter(socket.getOutputStream(), true); // Создаем PrintWriter для отправки сообщений
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Создаем BufferedReader для получения сообщений
        this.chatController = chatController; // Устанавливаем контроллер чата
        startListening(); // Запускаем поток для прослушивания входящих сообщений
    }

    // Метод для отправки сообщения на сервер
    public void sendMessage(String message) {
        out.println(message); // Отправляем сообщение на сервер
        logger.info("Sent message: {}", message); // Логируем отправленное сообщение
    }

    // Метод для запуска потока прослушивания входящих сообщений
    private void startListening() {
        new Thread(() -> {
            try {
                // Отправляем имя пользователя на сервер
                out.println(ChatClient.getUsername());

                String message;
                while ((message = in.readLine()) != null) { // Читаем входящие сообщения в цикле
                    if (message.startsWith("USER_COUNT:")) {
                        int userCount = Integer.parseInt(message.substring("USER_COUNT:".length()));
                        chatController.updateUserCount(userCount);
                    } else {
                        chatController.appendMessage(message); // Добавляем полученное сообщение в чат
                        logger.info("Received message: {}", message); // Логируем полученное сообщение
                    }
                }
            } catch (IOException e) {
                logger.error("Error while listening for messages", e); // Логируем ошибку при прослушивании сообщений
            }
        }).start(); // Запускаем поток
    }

    // Метод для закрытия соединения
    public void close() throws IOException {
        in.close(); // Закрываем BufferedReader
        out.close(); // Закрываем PrintWriter
        socket.close(); // Закрываем сокет
        logger.info("Connection closed"); // Логируем закрытие соединения
    }
}

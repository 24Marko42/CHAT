package org.example.tetetete.server;

import org.example.tetetete.common.exception.InvalidCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    private final int port;
    private final String host;
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public ChatServer(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Сервер запущен на порту {} и хосте {}", port, host);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket, this)).start();
            }
        } catch (IOException e) {
            logger.error("Ошибка запуска сервера: {}", e.getMessage(), e);
        }
    }

    public void broadcast(String message, ClientHandler sender) {
        logger.info("Широковещательная отправка сообщения: {}", message);
        for (ClientHandler client : clients.values()) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public synchronized void addClient(String username, ClientHandler clientHandler) {
        clients.put(username, clientHandler);
        broadcast("Пользователь " + username + " присоединился к чату.", null);
        logger.info("Пользователь {} подключен", username);
        updateUserCount();
    }

    public synchronized void removeClient(String username) {
        if (username != null) {
            clients.remove(username);
            broadcast("Пользователь " + username + " покинул чат.", null);
            logger.info("Пользователь {} отключен", username);
            updateUserCount();
        }
    }

    private void updateUserCount() {
        int userCount = clients.size();
        broadcast("USER_COUNT:" + userCount, null);
    }

    public static void main(String[] args) {
        AppConfig config = new AppConfig();
        int port = Integer.parseInt(config.getProperty("server.port"));
        String host = config.getProperty("server.host");

        ChatServer chatServer = new ChatServer(port, host);
        chatServer.start();
    }

    public static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final ChatServer chatServer;
        private BufferedReader reader;
        private PrintWriter writer;
        private String username;

        public ClientHandler(Socket clientSocket, ChatServer chatServer) {
            this.clientSocket = clientSocket;
            this.chatServer = chatServer;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream(), true);

                // Получаем имя пользователя
                writer.println("Введите имя пользователя:");
                username = reader.readLine();

                if (username == null || username.isBlank()) {
                    writer.println("Имя пользователя не может быть пустым.");
                    closeResources();
                    return;
                }

                synchronized (chatServer) {
                    if (chatServer.clients.containsKey(username)) {
                        writer.println("Имя пользователя занято. Попробуйте другое.");
                        closeResources();
                        return;
                    }
                    chatServer.addClient(username, this);
                }

                // Обработка сообщений
                String message;
                while ((message = reader.readLine()) != null) {
                    if (message.equalsIgnoreCase("/exit")) {
                        writer.println("Вы вышли из чата.");
                        break;
                    }
                    chatServer.broadcast(username + ": " + message, this);
                }
            } catch (IOException e) {
                logger.error("Ошибка связи с клиентом: {}", e.getMessage(), e);
            } finally {
                chatServer.removeClient(username);
                closeResources();
            }
        }

        public void sendMessage(String message) {
            if (writer != null) {
                writer.println(message);
            }
        }

        private void closeResources() {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                logger.error("Ошибка при закрытии ресурсов: {}", e.getMessage(), e);
            }
        }
    }
}

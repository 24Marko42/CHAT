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
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatServer {
    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);
    private final int port;
    private final String host;
    private final UserService userService = new UserService();
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
            logger.error("Ошибка запуска сервера: {}", e.getMessage());
        }
    }

    public void broadcast(String message, ClientHandler sender) {
        clients.values().forEach(client -> {
            if (client != sender) {
                client.sendMessage(message);
            }
        });
    }

    public synchronized void addClient(String username, ClientHandler clientHandler) {
        clients.put(username, clientHandler);
        broadcast("Пользователь " + username + " присоединился к чату.", null);
        logger.info("Пользователь {} подключен", username);
        updateUserCount();
    }

    public synchronized void removeClient(String username) {
        clients.remove(username);
        broadcast("Пользователь " + username + " покинул чат.", null);
        logger.info("Пользователь {} отключен", username);
        updateUserCount();
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

        // Запускаем периодическое обновление количества пользователей
        chatServer.scheduler.scheduleAtFixedRate(chatServer::updateUserCount, 0, 1, TimeUnit.SECONDS);
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

                // Получаем имя пользователя от клиента
                username = reader.readLine();

                if (username == null || username.isBlank()) {
                    writer.println("Недопустимое имя пользователя. Подключение завершено.");
                    closeResources();
                    return;
                }

                synchronized (chatServer) {
                    if (chatServer.clients.containsKey(username)) {
                        writer.println("Имя пользователя уже занято. Подключение завершено.");
                        closeResources();
                        return;
                    }
                    chatServer.addClient(username, this);
                }

                String message;
                while ((message = reader.readLine()) != null) {
                    if (message.equalsIgnoreCase("/exit")) {
                        break;
                    }
                    chatServer.broadcast(username + ": " + message, this);
                }
            } catch (IOException e) {
                logger.error("Ошибка связи с клиентом: {}", e.getMessage());
            } finally {
                chatServer.removeClient(username);
                closeResources();
            }
        }

        public void sendMessage(String message) {
            writer.println(message);
        }

        private void closeResources() {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                logger.error("Ошибка при закрытии ресурсов: {}", e.getMessage());
            }
        }
    }
}

package org.example.tetetete.common;

public class Message {
    private String type; // Тип сообщения (например, "chat", "login", "register")
    private String content; // Содержимое сообщения

    // Конструкторы, геттеры и сеттеры
    public Message() {
    }

    public Message(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

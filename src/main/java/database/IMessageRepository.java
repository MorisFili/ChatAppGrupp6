package database;

import core.TextMessage;

import java.util.List;

public interface IMessageRepository {
    void saveMessage(TextMessage message);
    void deleteMessage(TextMessage message);
    List<TextMessage> loadMessages();
}

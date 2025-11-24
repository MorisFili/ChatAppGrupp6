package database;

import core.TextMessage;

public interface IMessageRepository {
    void saveMessage(TextMessage message);
    void deleteMessage(TextMessage message);
}

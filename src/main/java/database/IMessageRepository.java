package database;

import model.TextNode;

public interface IMessageRepository {
    void saveMessage(TextNode message);
    void deleteMessage(String username, String content) throws Exception;
}

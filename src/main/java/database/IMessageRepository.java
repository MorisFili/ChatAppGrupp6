package database;

import model.TextNode;

public interface IMessageRepository {
    void saveMessage(TextNode message);
}

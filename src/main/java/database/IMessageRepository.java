package database;

import javafx.scene.Node;
import model.TextNode;

public interface IMessageRepository {
    void saveMessage(TextNode message);
}

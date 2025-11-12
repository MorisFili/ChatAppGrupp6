package database;

import model.TextNode;

// Har functional interface annotation för att det är bara en metod just nu
// Om det blir fler metoder senare kommer jag ta bort den
@FunctionalInterface
public interface IMessageRepository {
    void saveMessage(TextNode message);
}

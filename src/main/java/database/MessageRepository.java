package database;

import model.TextNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MessageRepository implements IMessageRepository {
    private final File file = new File("Messages.txt");

    @Override
    public void saveMessage(TextNode message) {
        String fileName = file.getName();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            String content = message.getContent();
            String username = message.getUsername();
            String timestamp = String.valueOf(message.getTimestamp());

            writer
                    .append(content)
                    .append("\n")
                    .append(username)
                    .append("\n")
                    .append(timestamp)
                    .append("\n");

        } catch (IOException e) {
            System.out.println("Error occurred while writing in file file.");
            e.printStackTrace();
        }
    }
}

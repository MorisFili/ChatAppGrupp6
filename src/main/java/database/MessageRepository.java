package database;

import model.TextNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

public class MessageRepository implements IMessageRepository {
    private final File file = new File("Messages.txt");

    @Override
    public void saveMessage(TextNode message) {
        String fileName = file.getName();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {

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

    @Override
    public void deleteMessage(String username, String content) throws Exception {
        Path filePath = Path.of(file.toURI());

        // Reads all lines into memory
        List<String> fileLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

        // Finds the index of line to be deleted based on Content/Message AND Username
        int startIndex = IntStream.range(0, fileLines.size())
                .filter(i -> fileLines.get(i).trim().equals(content.trim()) && fileLines.get(i + 1).trim().equals(username.trim()))
                .findFirst()
                .orElse(-1);

        if (startIndex == -1) {
            System.out.println("Message content not found: " + content);
            return;
        }

        // Filters and Rewrite
        List<String> updatedLines = IntStream.range(0, fileLines.size())
                .filter(i -> i != startIndex && i != startIndex + 1 && i != startIndex + 2)
                .mapToObj(fileLines::get)
                .toList();

        // Overwrites the original file
        Files.write( filePath, updatedLines );
    }
}

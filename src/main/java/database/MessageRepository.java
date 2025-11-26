package database;

import UI.ChatWindow;
import core.TextMessage;
import org.w3c.dom.Text;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class MessageRepository implements IMessageRepository {
    private static final String HARDCODED_PASSWORD = "ThisIsASecretPasswordForEncryption";
    private static final SecretKey KEY;

    static {
        try {
            byte[] keyBytes = HARDCODED_PASSWORD.getBytes(StandardCharsets.UTF_8);
            byte[] desKeyBytes = new byte[8];
            System.arraycopy(keyBytes, 0, desKeyBytes, 0, Math.min(keyBytes.length, desKeyBytes.length));

            KEY = new SecretKeySpec(desKeyBytes, "DES");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize encryption key", e);
        }
    }

    private final File file;

    public MessageRepository() {
        String groupName = ChatWindow.instance.getUserSession().getGroup();
        this.file = new File(groupName + ".txt");
    }

    @Override
    public void saveMessage(TextMessage textMessage) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {

            String messageToEncrypt = TextMessage.serializeForFile(textMessage);

            Cipher cipher = Cipher.getInstance("DES");

            cipher.init(Cipher.ENCRYPT_MODE, KEY);
            byte[] encryptedText = cipher.doFinal(messageToEncrypt.getBytes(StandardCharsets.UTF_8));

            String encryption = Base64.getEncoder().encodeToString(encryptedText);

            writer.append(encryption);
            writer.newLine();

        } catch (IOException e) {
            System.out.println("Error occurred while writing in file file.");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteMessage(TextMessage message) {

        ChatWindow.instance.getUserSession().getChatLog().remove(message);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {

            for (TextMessage msg : ChatWindow.instance.getUserSession().getChatLog()) {

                String msgs = msg.getContent() + msg.getUsername() + msg.getTimestamp();

                KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
                SecretKey key = keyGenerator.generateKey();

                Cipher deCipher = Cipher.getInstance("DES");

                byte[] text = msgs.getBytes(StandardCharsets.UTF_8);

                deCipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] encryptedText = deCipher.doFinal(text);

                String encryption = new String(encryptedText);

                writer.append(encryption + "\n");

            }

        } catch (IOException e) {
            System.out.println("Error occurred while writing in file file.");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TextMessage> loadMessages() {
        List<TextMessage> loadedMessages = new ArrayList<>();

        if (!file.exists()) {
            return loadedMessages;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String encryptedLine;
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, KEY);

            while ((encryptedLine = reader.readLine()) != null) {
                if (encryptedLine.trim().isEmpty()) continue;

                try { // <-- Start granular try-catch for corrupted lines
                    // 1. Convert the Base64 String back to a byte array
                    byte[] encryptedBytes = Base64.getDecoder().decode(encryptedLine); // Line 122

                    // 2. Decrypt the byte array
                    byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
                    String decryptedMessage = new String(decryptedBytes, StandardCharsets.UTF_8);

                    // 3. Deserialize and add the message
                    TextMessage message = TextMessage.deserializeFromFile(decryptedMessage);
                    if (message != null) {
                        loadedMessages.add(message);
                    }

                } catch (IllegalArgumentException e) {
                    // Catches the 'Illegal base64 character' error specifically
                    System.err.println("Skipping corrupted message line due to Base64 error. Line: " + encryptedLine);
                    // The loop continues to the next line
                } catch (Exception e) {
                    // Catches decryption errors or parsing errors on this line
                    System.err.println("Skipping message line due to decryption/parsing error: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist, return empty list (safe to ignore)
        } catch (Exception e) {
            System.err.println("Fatal I/O error during message loading: " + e.getMessage());
            e.printStackTrace();
        }

        return loadedMessages;
    }
}

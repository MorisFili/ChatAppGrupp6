package database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import UI.ChatWindow;
import core.TextMessage;


public class MessageRepository implements IMessageRepository {
    private final File file = new File(ChatWindow.instance.getUserSession().getGroup() + ".txt");

    @Override
    public void saveMessage(TextMessage textMessage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {

            String message = textMessage.getContent() + textMessage.getUsername() + textMessage.getTimestamp();

            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            SecretKey key = keyGenerator.generateKey();

            Cipher deCipher = Cipher.getInstance("DES");

            byte[] text = message.getBytes(StandardCharsets.UTF_8);

            deCipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedText = deCipher.doFinal(text);

            String encryption = new String(encryptedText);

            writer.append(encryption + "\n");

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
}

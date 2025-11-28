package core;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageMessage extends Message {

    private final File file;
    private final ImageView imageView;
    private final LocalDateTime timestamp;

    public ImageMessage(String username, File file) {
        super(username, null);
        this.file = file;
        this.timestamp = LocalDateTime.now();

        this.imageView = new ImageView(new Image(file.toURI().toString()));
        this.imageView.setFitWidth(200);
        this.imageView.setPreserveRatio(true);

        setText("[" + timestamp.format(DateTimeFormatter.ofPattern("dd MMM HH:mm")) + "] " + username + ": \n");
        setFont(javafx.scene.text.Font.font("Segoe UI Emoji", 12));
    }

    public File getFile() {
        return file;
    }

    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public String serialize() {
        return "IMG:" + username + ":" + file.getName();
    }
}

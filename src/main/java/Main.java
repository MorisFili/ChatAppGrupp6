import GUI.WindowManager;
import database.MessageRepository;
import javafx.application.Application;
import javafx.stage.Stage;
import model.TextNode;

import java.time.LocalDateTime;
import java.util.Scanner;

public class Main extends Application {
    public static void main(String[] args) { launch(); }

    @Override
    public void start(Stage stage) throws Exception {
        WindowManager windowManager = new WindowManager(stage);
    }
}

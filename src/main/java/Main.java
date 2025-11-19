import GUI.WindowManager;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
    public static void main(String[] args) { launch(); }

    private WindowManager windowManager;

    @Override
    public void start(Stage stage) throws Exception {
        WindowManager windowManager = new WindowManager(stage);

    }
}

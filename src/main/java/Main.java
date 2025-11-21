import GUI.WindowManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;


public class Main extends Application {
    public static void main(String[] args) { launch(); }

    private WindowManager windowManager;

    @Override
    public void start(Stage stage) throws Exception {
        windowManager = new WindowManager(stage);

    }

    @Override
    public void stop() throws Exception {
        windowManager.getChatWindow().getNetwork().terminateNetwork();
    }
}

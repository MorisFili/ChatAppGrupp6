import GUI.WindowManager;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }

    private WindowManager windowManager;

    @Override
    public void start(Stage stage) throws Exception {
        windowManager = new WindowManager(stage);
    }

    @Override
    public void stop() throws Exception {
        if (windowManager.getChatWindow().getNetwork() != null) {
            windowManager.getChatWindow().getNetwork().terminateNetwork();
            windowManager.terminatePool();
        }
    }
}

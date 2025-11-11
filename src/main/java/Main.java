import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        TextFlow mainBody = new TextFlow();

        ScrollPane scrollPane = new ScrollPane(mainBody);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        TextArea inputText = new TextArea();
        inputText.setWrapText(true);

        inputText.setWrapText(true);
        inputText.setPrefRowCount(2);

        Button send = new Button(">");
        send.setMinSize(40,60);

        HBox bottomAlignment = new HBox(inputText, send);
        HBox.setHgrow(inputText, Priority.ALWAYS);

        VBox root = new VBox(scrollPane, bottomAlignment);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        root.setPadding(new Insets(3));

        Scene scene = new Scene(root,600, 800);
        stage.setScene(scene);
        stage.setTitle("Chat");
        stage.show();


    }
}

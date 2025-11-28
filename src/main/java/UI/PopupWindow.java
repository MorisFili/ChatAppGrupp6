package UI;

import core.TextMessage;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PopupWindow {

    public void showPopup(TextMessage textMessage, Stage stage){

        Popup popup = new Popup();
        Label label = new Label(textMessage.getUsername() + ": " + textMessage.getContent());

        label.setStyle("""
        -fx-background-color: rgba(30,30,30,0.9);
        -fx-text-fill: white;
        -fx-padding: 12;
        -fx-background-radius: 6;
        -fx-font-size: 16;
        """);

        popup.getContent().add(label);
        popup.setAutoFix(true);
        popup.setAutoHide(true);

        Rectangle2D placement = Screen.getPrimary().getVisualBounds();
        double x = placement.getMaxX() - 15 - label.prefWidth(-1);
        double y = placement.getMaxY() - 15 - label.prefHeight(-1);

        popup.show(stage, x, y);

        FadeTransition in = new FadeTransition(Duration.millis(180), label);
        in.setFromValue(0.0);
        in.setToValue(1.0);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));

        FadeTransition out = new FadeTransition(Duration.millis(180), label);
        out.setFromValue(1.0);
        out.setToValue(0.0);
        out.setOnFinished(e -> popup.hide());

        SequentialTransition seq = new SequentialTransition(in, pause, out);
        seq.play();

    }
}

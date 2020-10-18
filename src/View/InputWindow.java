package View;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;


public class InputWindow {
    private static final int MIN_WIDTH = 250; //px

    public static void display(String title, String message) {
        Stage window = new Stage();

        // Block interaction with other windows while this window is active
        window.initModality(Modality.APPLICATION_MODAL);

        window.setTitle(title);
        window.setMinWidth(MIN_WIDTH);

        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("close");
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}

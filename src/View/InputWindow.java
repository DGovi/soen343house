package View;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;


public class InputWindow {
    private static final int MIN_WIDTH = 250; //px
    private static String returnValue = null;

    public static String display(String title, String message) {

        Stage window = new Stage();

        // Block interaction with other windows while this window is active
        window.initModality(Modality.APPLICATION_MODAL);

        window.setTitle(title);
        window.setMinWidth(MIN_WIDTH);

        Label label = new Label();
        label.setText(message);
        TextField inputField = new TextField();
        Button closeButton = new Button("Cancel");
        closeButton.setOnAction(e -> window.close());
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            returnValue = inputField.getText();
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton, submitButton, inputField);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return returnValue;
    }
}

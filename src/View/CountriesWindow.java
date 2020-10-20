package View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

import java.util.Locale;

/**
 * This is a pop up window when the user is prompt to enter
 * a new location for their house.
 */
public class CountriesWindow {
    private static final int MIN_WIDTH = 250; //px
    private static String returnValue = null;

    /**
     * creates a window on which the user needs to enter a country
     * @param title the title of the window
     * @param message the prompt to enter text
     * @return a country value
     */
    public static String display(String title, String message) {

        Stage window = new Stage();

        // Block interaction with other windows while this window is active
        window.initModality(Modality.APPLICATION_MODAL);

        window.setTitle(title);
        window.setMinWidth(MIN_WIDTH);

        Label label = new Label();
        label.setText(message);

        ObservableList<String> cities = FXCollections.observableArrayList();
        ComboBox<String> countrySelect = new ComboBox<String>(cities);
        String[] locales1 = Locale.getISOCountries();
        for (String countrylist : locales1) {
            Locale obj = new Locale("", countrylist);
            String[] city = { obj.getDisplayCountry() };
            for (int x = 0; x < city.length; x++) {
                cities.add(obj.getDisplayCountry());
            }
        }
        countrySelect.setItems(cities);

        Button closeButton = new Button("Cancel");
        closeButton.setOnAction(e -> window.close());
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            returnValue = countrySelect.getValue();
            window.close();
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(closeButton, submitButton);
        layout.getChildren().addAll(label,countrySelect, buttonsBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return returnValue;
    }
}

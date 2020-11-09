package View;
// for Dashboard

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This creates the Dashboard window
 * from the Dashboard.fxml file.
 */
public class DashboardDriver extends Application {
    Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    //launch(args) calls this method to launch the dashboard
    @Override
    public void start(Stage window) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        window.setTitle("Smart Home Simulator");
        window.setScene(new Scene(root, 870, 850));
        window.show();
    }

}

package View;
// for Dashboard

import Controller.DashboardController;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        Parent root = loader.load();
        window.setTitle("Smart Home Simulator");
        window.setScene(new Scene(root, 870, 850));
        window.show();
    }

    @Override
    public void stop(){
        System.exit(0);
    }

}

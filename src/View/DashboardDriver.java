package View;
// for Dashboard
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardDriver extends Application {

    Stage window;
    public static void main(String args[]) {launch(args);}

    //launch(args) calls this method to launch the dashboard
    @Override
    public void start(Stage window) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        window.setTitle("Smart Home Simulator");
        window.setScene(new Scene(root, 870, 720));
        window.show();
    }
    
}

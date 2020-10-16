package Dashboard;
// for Dashboard
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.*;
import java.util.*;

import House.Room;



public class DashboardDriver extends Application {

    Stage window;
    
    
    public static void main(String args[]) {launch(args);}

    //launch(args) calls this method to launch the dashboard
    @Override
    public void start(Stage window) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
    	window.setTitle("Smart Home Simulator");
        window.setScene(new Scene(root, 800, 600));
        window.show();
    }
    
}

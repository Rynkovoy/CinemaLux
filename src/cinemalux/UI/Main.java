package cinemalux.UI;


import cinemalux.Employee;
import cinemalux.Hall;
import cinemalux.MovingPictureShow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Date;

public class Main extends Application {

    public static boolean RUN = true;
    public static Employee user;
    public static Stage stage;
    public static Parent current;
    public static Parent authScene;
    public static Parent mainScene;

    public static void setScene(Parent root) {
        current = root;
        stage.setTitle("Cinema Lux");
        stage.setScene(new Scene(root));

        stage.show();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {



        launch(args);
        RUN = false;
        if (!MainController.helper.isInterrupted())
            MainController.helper.interrupt();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setResizable(false);
        authScene = FXMLLoader.load(getClass().getResource("auth.fxml"));
        mainScene = FXMLLoader.load(getClass().getResource("main.fxml"));
        stage.setTitle("Cinema Lux");
        setScene(authScene);
    }
}

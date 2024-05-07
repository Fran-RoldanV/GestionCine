package es.gestioncine.gestioncine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/es/gestioncine/gestioncine/views/MainView.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Gestión de Cine");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

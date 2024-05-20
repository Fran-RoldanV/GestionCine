package es.gestioncine.gestioncine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/es/gestioncine/gestioncine/views/MainView.fxml"));
        Scene scene = new Scene(root);
        Image icon = new Image(getClass().getResourceAsStream("/es/gestioncine/gestioncine/resources/img/icon.png"));

        // Configurar el icono de la aplicación
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gestión de Cine");
        primaryStage.setMaximized(true); // Abre la ventana maximizada
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

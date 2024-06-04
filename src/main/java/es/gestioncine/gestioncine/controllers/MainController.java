package es.gestioncine.gestioncine.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentPane;

    private static MainController instance;

    public MainController() {
        instance = this;
    }

    public static MainController getInstance() {
        return instance;
    }

    public void initialize() {
        showHome();
    }

    @FXML
    private void hover(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: #00ADB5;");
    }

    @FXML
    private void exit(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: transparent;");
    }

    @FXML
    public void showHome() {
        setPage("/es/gestioncine/gestioncine/views/HomeView.fxml");
    }

    @FXML
    private void showPeliculas() {
        setPage("/es/gestioncine/gestioncine/views/MoviesView.fxml");
    }

    @FXML
    private void showReservas() {
        setPage("/es/gestioncine/gestioncine/views/ReservationsView.fxml");
    }

    @FXML
    private void showCriticas() {
        setPage("/es/gestioncine/gestioncine/views/ReviewsView.fxml");
    }

    @FXML
    private void showDescuentos() {
        setPage("/es/gestioncine/gestioncine/views/DiscountView.fxml");
    }

    @FXML
    private void showAjustes() {
        setPage("/es/gestioncine/gestioncine/views/SettingsView.fxml");
    }

    @FXML
    private void showIniciarSesion() {
        setPage("/es/gestioncine/gestioncine/views/LoginView.fxml");
    }

    @FXML
    private void showRegistrarse() {
        setPage("/es/gestioncine/gestioncine/views/RegisterView.fxml");
    }

    private void setPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Pane newLoadedPane = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(newLoadedPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

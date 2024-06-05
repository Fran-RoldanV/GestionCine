package es.gestioncine.gestioncine.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;

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
    public void exit(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: transparent;");
    }

    @FXML
    public void showHome() {
        setPage("/es/gestioncine/gestioncine/views/HomeView.fxml");
    }

    @FXML
    public void showPeliculas() {
        setPage("/es/gestioncine/gestioncine/views/MoviesView.fxml");
    }

    @FXML
    public void showReservas() {
        setPage("/es/gestioncine/gestioncine/views/ReservationsView.fxml");
    }

    @FXML
    public void showCriticas() {
        setPage("/es/gestioncine/gestioncine/views/ReviewsView.fxml");
    }

    @FXML
    public void showDescuentos() {
        setPage("/es/gestioncine/gestioncine/views/DiscountView.fxml");
    }

    @FXML
    public void showAjustes() {
        setPage("/es/gestioncine/gestioncine/views/SettingsView.fxml");
    }

    @FXML
    public void showIniciarSesion() {
        setPage("/es/gestioncine/gestioncine/views/LoginView.fxml");
    }

    @FXML
    public void showRegistrarse() {
        setPage("/es/gestioncine/gestioncine/views/RegisterView.fxml");
    }

    public void showForgotPassword() {
        setPage("/es/gestioncine/gestioncine/views/ForgotPasswordView.fxml");
    }

    // Nuevo método para mostrar la página de datos de la película
    public void showMovieData(String imageUrl, String correo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/gestioncine/gestioncine/views/MovieDataView.fxml"));
            Parent newLoadedPane = loader.load();
            MovieDataController controller = loader.getController();
            controller.initialize(imageUrl, correo);
            contentPane.getChildren().clear();
            contentPane.getChildren().add(newLoadedPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent newLoadedPane = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(newLoadedPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

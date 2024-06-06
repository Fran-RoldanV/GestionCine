package es.gestioncine.gestioncine.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnIniciarSesion;

    @FXML
    private Button btnRegistrarse;

    @FXML
    private Label lblCorreoUsuario;

    public String getLblCorreoUsuario() {
        return lblCorreoUsuario.getText();
    }

    private static MainController instance;

    // Constructor
    public MainController() {
        instance = this;
    }

    // Singleton instance getter
    public static MainController getInstance() {
        return instance;
    }

    // Initialize method
    public void initialize() {
        // Show the home view by default
        showHome();

        // Configure buttons based on the login status
        actualizarEstadoSesion(false, null);
    }

    // Event handler for hover effect
    @FXML
    private void hover(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: #00ADB5;");
    }

    // Event handler for exit effect
    @FXML
    public void exit(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: transparent;");
    }

    // Methods to show different views
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

    public void showReserveMovie() {
        setPage("/es/gestioncine/gestioncine/views/ReserveView.fxml");
    }

    @FXML
    public void cerrarSesion(ActionEvent event) {
        actualizarEstadoSesion(false, null);
        showHome(); // Show home view after logging out
    }

    // Method to show the movie data page
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

    // Helper method to set the page
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

    // Method to update the session status
    public void actualizarEstadoSesion(boolean sesionIniciada, String correo) {
        btnCerrarSesion.setDisable(!sesionIniciada);
        btnCerrarSesion.setVisible(sesionIniciada);

        lblCorreoUsuario.setVisible(sesionIniciada);
        lblCorreoUsuario.setText(sesionIniciada ? correo : "");

        btnIniciarSesion.setDisable(sesionIniciada);
        btnIniciarSesion.setVisible(!sesionIniciada);

        btnRegistrarse.setDisable(sesionIniciada);
        btnRegistrarse.setVisible(!sesionIniciada);
    }
}

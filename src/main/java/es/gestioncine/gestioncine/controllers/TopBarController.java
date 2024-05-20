package es.gestioncine.gestioncine.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class TopBarController {


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
    private void showHome() {
        // Lógica para mostrar la página de Inicio
    }

    @FXML
    private void showMovies() {
        // Lógica para mostrar la página de Películas
    }

    @FXML
    private void showReservations() {
        // Lógica para mostrar la página de Reservas
    }

    @FXML
    private void showSettings() {
        // Lógica para mostrar la página de Ajustes
    }

    @FXML
    private void showRegister() {
        // Lógica para mostrar la página de Registro
    }

    @FXML
    private void showLogin() {
        // Lógica para mostrar la página de Inicio de Sesión
    }}

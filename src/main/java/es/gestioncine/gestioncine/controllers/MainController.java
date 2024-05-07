package es.gestioncine.gestioncine.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private VBox sideMenu;

    public void initialize() {
        // Ocultar el SideMenu al iniciar la aplicación
        sideMenu.setVisible(false);
    }

    // Método para mostrar u ocultar el SideMenu
    public void toggleSideMenu() {
        sideMenu.setVisible(!sideMenu.isVisible());
    }

    // Método para cerrar el SideMenu al hacer clic fuera de él
    public void closeSideMenu(javafx.scene.input.MouseEvent event) {
        if (!sideMenu.getBoundsInParent().contains(event.getX(), event.getY())) {
            sideMenu.setVisible(false);
        }
    }
}

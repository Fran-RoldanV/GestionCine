package es.gestioncine.gestioncine.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class TopBarController {

    @FXML
    private VBox sideMenu;

    private boolean sideMenuVisible = false;

    @FXML
    private void toggleSideMenu() {
        sideMenu.setVisible(!sideMenuVisible);
        sideMenuVisible = !sideMenuVisible;
    }
}

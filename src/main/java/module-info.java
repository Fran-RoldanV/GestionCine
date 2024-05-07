module es.gestioncine.gestioncine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens es.gestioncine.gestioncine.views to javafx.fxml;
    exports es.gestioncine.gestioncine.controllers;
    opens es.gestioncine.gestioncine.controllers to javafx.fxml;
    exports es.gestioncine.gestioncine.models;
    opens es.gestioncine.gestioncine.models to javafx.fxml;

    // Clase principal de inicio
    exports es.gestioncine.gestioncine;
    opens es.gestioncine.gestioncine;
    requires javafx.graphics;
}

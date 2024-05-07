module es.gestioncine.gestioncine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens es.gestioncine.gestioncine to javafx.fxml;
    exports es.gestioncine.gestioncine;
}
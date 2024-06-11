package es.gestioncine.gestioncine.controllers;

import es.gestioncine.gestioncine.Configuration;
import es.gestioncine.gestioncine.controllers.MainController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReserveListController {

    private static final String IP = Configuration.IP;
    private static final int PORT = Configuration.PORT;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @FXML
    private VBox tableLayout;

    @FXML
    private ScrollPane scrollPane;

    public void initialize() {
        readReservesFromDB();
    }

    private void readReservesFromDB() {
        executorService.execute(() -> {
            try {
                Socket socket = new Socket(IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Enviar orden al servidor
                out.println("GET_RESERVE");

                // Enviar el correo
                out.println(MainController.getInstance().getLblCorreoUsuario());  // Cambia esto por el correo real si es necesario

                // Leer todas las líneas de respuesta
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    responseBuilder.append(line).append("\n");
                }
                String response = responseBuilder.toString().trim();

                // Cerrar recursos
                out.close();
                in.close();
                socket.close();

                // Manejar la respuesta
                Platform.runLater(() -> handleServerResponse(response));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleServerResponse(String response) {
        tableLayout.getChildren().clear();

        if (response != null && !response.isEmpty()) {
            String[] lines = response.split("\n");
            for (String line : lines) {
                String[] fields = line.split("\\|");

                HBox row = new HBox();
                for (String field : fields) {
                    Label label = new Label(field);
                    label.setStyle("-fx-padding: 8px;");
                    row.getChildren().add(label);
                }
                tableLayout.getChildren().add(row);
            }
        } else {
            Label label = new Label("No hay reservas disponibles");
            label.setStyle("-fx-padding: 8px;");
            tableLayout.getChildren().add(label);
        }
    }
}

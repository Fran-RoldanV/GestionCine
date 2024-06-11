package es.gestioncine.gestioncine.controllers;

import es.gestioncine.gestioncine.Configuration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReserveMovieController {

    @FXML
    private Label labelTitulo;

    @FXML
    private ComboBox<String> comboBoxSala;

    @FXML
    private ComboBox<String> comboBoxHorario;

    @FXML
    private GridPane gridButacas;

    @FXML
    private Button buttonConfirmar;

    private static final String IP = Configuration.IP;
    private static final int PORT = Configuration.PORT;

    private String tituloPelicula;
    private String correoUsuario;

    private List<Button> selectedButacas = new ArrayList<>();

    public void initialize() {
        // You can set these values from another method or pass them when creating the scene
        tituloPelicula = "Immaculate";
        correoUsuario = "user@example.com";

        labelTitulo.setText(tituloPelicula);
        loadSalaData(tituloPelicula);

        comboBoxSala.setOnAction(event -> {
            String selectedSala = comboBoxSala.getValue();
            if (selectedSala != null) {
                loadHorarioData(selectedSala, tituloPelicula);
            }
        });

        comboBoxHorario.setOnAction(event -> {
            String selectedSala = comboBoxSala.getValue();
            String selectedHorario = comboBoxHorario.getValue();
            if (selectedSala != null && selectedHorario != null) {
                loadButacasData(selectedSala, selectedHorario, tituloPelicula);
            }
        });

        buttonConfirmar.setOnAction(event -> confirmarReserva());
    }

    private void loadSalaData(String tituloPelicula) {
        CompletableFuture.supplyAsync(() -> {
            List<String> salas = new ArrayList<>();
            try (Socket socket = new Socket(IP, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("GET_SALAS_FOR_MOVIE");
                out.println(tituloPelicula);
                String response;
                while ((response = in.readLine()) != null) {
                    salas.add(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return salas;
        }).thenAccept(salas -> Platform.runLater(() -> {
            comboBoxSala.getItems().setAll(salas);
        }));
    }

    private void loadHorarioData(String numeroSala, String tituloPelicula) {
        CompletableFuture.supplyAsync(() -> {
            List<String> horarios = new ArrayList<>();
            try (Socket socket = new Socket(IP, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("GET_HORARIOS_FOR_MOVIE");
                out.println(numeroSala);
                out.println(tituloPelicula);
                String response;
                while ((response = in.readLine()) != null) {
                    horarios.add(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return horarios;
        }).thenAccept(horarios -> Platform.runLater(() -> {
            comboBoxHorario.getItems().setAll(horarios);
        }));
    }

    private void loadButacasData(String numeroSala, String horario, String tituloPelicula) {
        // Sample data for seat availability
        Platform.runLater(() -> {
            gridButacas.getChildren().clear();
            selectedButacas.clear();
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    Button butaca = new Button();
                    butaca.setStyle("-fx-background-color: green;");
                    butaca.setOnAction(event -> {
                        if (butaca.getStyle().contains("green")) {
                            butaca.setStyle("-fx-background-color: red;");
                            selectedButacas.add(butaca);
                        } else {
                            butaca.setStyle("-fx-background-color: green;");
                            selectedButacas.remove(butaca);
                        }
                    });
                    gridButacas.add(butaca, i, j);
                }
            }
        });
    }

    @FXML
    private void confirmarReserva() {
        if (comboBoxSala.getValue() == null || comboBoxHorario.getValue() == null || selectedButacas.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error en la reserva");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione la sala, el horario y al menos una butaca.");
            alert.showAndWait();
            return;
        }

        CompletableFuture.runAsync(() -> {
            try (Socket socket = new Socket(IP, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("CONFIRM_RESERVATION");
                out.println(correoUsuario);
                out.println(comboBoxSala.getValue());
                out.println(comboBoxHorario.getValue());
                out.println(tituloPelicula);
                out.println(selectedButacas.size());

                for (Button butaca : selectedButacas) {
                    // Assuming you have a way to identify each seat, e.g., using GridPane's row/column indices
                    Integer row = GridPane.getRowIndex(butaca);
                    Integer col = GridPane.getColumnIndex(butaca);
                    out.println(row + "," + col);
                }

                String response = in.readLine();
                Platform.runLater(() -> {
                    if ("SUCCESS".equals(response)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Reserva Confirmada");
                        alert.setHeaderText(null);
                        alert.setContentText("Reserva confirmada con éxito.");
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error en la reserva");
                        alert.setHeaderText(null);
                        alert.setContentText("Hubo un error al confirmar la reserva. Inténtelo nuevamente.");
                        alert.showAndWait();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error en la conexión");
                    alert.setHeaderText(null);
                    alert.setContentText("No se pudo conectar con el servidor. Inténtelo nuevamente.");
                    alert.showAndWait();
                });
            }
        });
    }
}

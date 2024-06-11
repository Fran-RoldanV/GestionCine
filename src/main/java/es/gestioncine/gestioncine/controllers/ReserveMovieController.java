package es.gestioncine.gestioncine.controllers;

import es.gestioncine.gestioncine.Configuration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReserveMovieController {

    private static final String IP = Configuration.IP;
    private static final int PORT = Configuration.PORT;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private String correo = MainController.getInstance().getLblCorreoUsuario();

    @FXML
    private Label tituloLabel;

    @FXML
    private ComboBox<String> salaComboBox;

    @FXML
    private ComboBox<String> horarioComboBox;

    @FXML
    private GridPane gridLayout;

    @FXML
    private Button confirmButton;

    public void initialize(String titulo) {
        tituloLabel.setText(titulo);
        loadSalaData(titulo);

        for (int i = 0; i < 12; i++) {
            Button butaca = new Button();
            butaca.setPrefSize(40, 40);
            butaca.setStyle("-fx-background-color: transparent;"); // Fondo transparente
            Image butacaImage = new Image(getClass().getResource("/es/gestioncine/gestioncine/resources/img/butaca_libre.png").toExternalForm());
            butaca.setGraphic(new ImageView(butacaImage));
            butaca.setOnAction(event -> reservarButaca(butaca));

            gridLayout.add(butaca, i % 4, i / 4); // Añadir butaca al GridPane (columna, fila)
        }

        salaComboBox.setOnAction(event -> {
            String selectedSala = salaComboBox.getSelectionModel().getSelectedItem();
            System.out.println("Sala seleccionada: " + selectedSala); // Verificar la sala seleccionada
            if (selectedSala != null) {
                loadHorarioData(selectedSala, titulo);
            }
        });

        confirmButton.setOnAction(event -> {
            String estadoReserva = "Confirmada";
            CompletableFuture<Integer>[] futures = new CompletableFuture[2];
            futures[0] = obtenerIdUsuario(correo);
            futures[1] = obtenerIdPelicula(titulo);

            CompletableFuture.allOf(futures).thenAcceptAsync(result -> {
                try {
                    String numeroSala = salaComboBox.getSelectionModel().getSelectedItem();
                    String horario = horarioComboBox.getSelectionModel().getSelectedItem();
                    System.out.println("Horario seleccionado: " + horario); // Verificar el horario seleccionado
                    reservar(futures[0].get(), futures[1].get(), numeroSala, horario, estadoReserva, contarButacasReservadas());
                } catch (Exception e) {
                    mostrarMensaje("Error al realizar la reserva: " + e.getMessage());
                }
            });
        });
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
        }, executorService).thenAcceptAsync(salas -> {
            Platform.runLater(() -> {
                salaComboBox.getItems().clear();
                salaComboBox.getItems().addAll(salas);
                System.out.println("Salas cargadas en salaComboBox: " + salas); // Verificar las salas cargadas
            });
        });
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
        }, executorService).thenAcceptAsync(horarios -> {
            Platform.runLater(() -> {
                horarioComboBox.getItems().clear();
                horarioComboBox.getItems().addAll(horarios);
                System.out.println("Horarios cargados en horarioComboBox: " + horarios); // Verificar los horarios cargados
            });
        });
    }

    @FXML
    private void reservarButaca(Button butaca) {
        String currentStyle = butaca.getStyle();
        if (currentStyle.contains("-fx-background-color: white;")) {
            butaca.setStyle("-fx-background-color: transparent;");
        } else {
            butaca.setStyle("-fx-background-color: white;");
        }
    }

    private int contarButacasReservadas() {
        int count = 0;
        for (var node : gridLayout.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                if (button.getStyle().contains("-fx-background-color: red;")) {
                    count++;
                }
            }
        }
        return count;
    }

    private CompletableFuture<Integer> obtenerIdUsuario(String correo) {
        return CompletableFuture.supplyAsync(() -> {
            try (Socket socket = new Socket(IP, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("GET_USER_ID");
                out.println(correo);
                String response = in.readLine();
                System.out.println("ID de usuario obtenido: " + response); // Verificar el ID de usuario
                return Integer.parseInt(response);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error al obtener el ID de usuario.");
            }
        }, executorService);
    }

    private CompletableFuture<Integer> obtenerIdPelicula(String titulo) {
        return CompletableFuture.supplyAsync(() -> {
            try (Socket socket = new Socket(IP, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("GET_MOVIE_ID");
                out.println(titulo);
                String response = in.readLine();
                System.out.println("ID de película obtenido: " + response); // Verificar el ID de película
                return Integer.parseInt(response);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error al obtener el ID de película.");
            }
        }, executorService);
    }

    private void reservar(int idUsuario, int idPelicula, String sala, String hora, String estadoReserva, int butacasReservadas) {
        // Aquí, puedes implementar la lógica para proceder con el pago y la reserva
        System.out.println("Reserva realizada con éxito. Usuario: " + idUsuario + ", Película: " + idPelicula + ", Sala: " + sala + ", Hora: " + hora + ", Butacas: " + butacasReservadas);
    }

    private void mostrarMensaje(String mensaje) {
        Platform.runLater(() -> {
            // Aquí, puedes implementar la lógica para mostrar un mensaje al usuario, por ejemplo, con una alerta
            System.out.println(mensaje);
        });
    }
}

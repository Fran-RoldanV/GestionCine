package es.gestioncine.gestioncine.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MovieDataController {
    private static final String IP = "192.168.1.111";
    private static final int PORT = 12345;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @FXML
    private ImageView peliculaImageView;

    @FXML
    private Label tituloLabel;

    @FXML
    private Label descripcionLabel;

    @FXML
    private Label generoLabel;

    @FXML
    private Label directorLabel;

    @FXML
    private Label duracionLabel;

    @FXML
    private Label clasificacionLabel;

    @FXML
    private Button botonReservar;

    public void initialize(String imageUrl, String correo) {
        loadImage(imageUrl);
        fetchMovieData(imageUrl);

        botonReservar.setOnAction(event -> {
            // Lógica para redirigir a la página de reserva
            System.out.println("Redirigir a la página de reserva para la película: " + tituloLabel.getText());
            // Aquí puedes implementar la lógica para redirigir a la página de reserva
        });
    }

    private void loadImage(String imageUrl) {
        Image image = new Image(imageUrl);
        peliculaImageView.setImage(image);
    }

    private void fetchMovieData(String imageUrl) {
        executorService.execute(() -> {
            String response;

            try (Socket socket = new Socket(IP, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Enviamos orden al servidor.
                out.println("MOVIE_DATA");

                // Enviamos credenciales al servidor.
                out.println(imageUrl);

                // Leemos respuesta.
                response = in.readLine();

            } catch (IOException e) {
                response = "ERROR";
            }

            final String result = response;
            Platform.runLater(() -> handleServerResponse(result));
        });
    }

    private void handleServerResponse(String result) {
        if (result.equals("ERROR")) {
            System.out.println("Error al obtener los datos de la película");
        } else if (result.equals("MOVIE_NOT_FOUND")) {
            System.out.println("La película no se encuentra");
        } else {
            String[] movieData = result.split(";");

            tituloLabel.setText(movieData[0]);
            descripcionLabel.setText(movieData[1]);
            generoLabel.setText(movieData[2]);
            directorLabel.setText(movieData[3]);
            duracionLabel.setText(movieData[4]);
            clasificacionLabel.setText(movieData[5]);
        }
    }
}

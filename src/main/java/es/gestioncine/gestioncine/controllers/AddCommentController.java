package es.gestioncine.gestioncine.controllers;

import es.gestioncine.gestioncine.Configuration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AddCommentController {

    private final String IP = Configuration.IP;
    private final int PORT = Configuration.PORT;

    @FXML
    private TextField etComment;

    @FXML
    private HBox ratingBox;

    @FXML
    private Button btnSubmit;

    @FXML
    private ComboBox<String> comboBox;

    private String correo;
    private int selectedMovieId;
    private int currentRating;

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @FXML
    private void initialize() {
        initializeRatingBox();
        btnSubmit.setOnAction(event -> submitComment());

        // Load movie names into ComboBox
        Executors.newSingleThreadExecutor().execute(() -> {
            List<String> movieNames = fetchMovieNamesFromServer();
            Platform.runLater(() -> {
                if (movieNames != null) {
                    comboBox.getItems().addAll(movieNames);
                } else {
                    showAlert("Error", "No se pudieron cargar los nombres de las películas. Por favor, inténtalo de nuevo más tarde.");
                }
            });
        });

        comboBox.setOnAction(event -> {
            // Handle movie selection change if needed
        });
    }

    private void initializeRatingBox() {
        ratingBox.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(new Image(getClass().getResourceAsStream("/es/gestioncine/gestioncine/resources/img/star.png")));
            star.setFitWidth(40);
            star.setFitHeight(40);
            final int rating = i + 1;
            star.setOnMouseClicked(event -> setRating(rating));
            ratingBox.getChildren().add(star);
        }
    }

    private void setRating(int rating) {
        currentRating = rating;
        for (int i = 0; i < 5; i++) {
            ImageView star = (ImageView) ratingBox.getChildren().get(i);
            if (i < rating) {
                star.setImage(new Image(getClass().getResourceAsStream("/es/gestioncine/gestioncine/resources/img/star_filled.png")));
            } else {
                star.setImage(new Image(getClass().getResourceAsStream("/es/gestioncine/gestioncine/resources/img/star.png")));
            }
        }
    }

    private void submitComment() {
        String comment = etComment.getText();
        String selectedMovie = comboBox.getValue();
        if (selectedMovie == null) {
            showAlert("Error", "Por favor, selecciona una película.");
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            String result = submitCommentToServer(comment, currentRating);
            Platform.runLater(() -> {
                if ("INSERT_COMMENT_SUCCESS".equals(result)) {
                    ((Stage) btnSubmit.getScene().getWindow()).close(); // Cierra la ventana al enviar el comentario exitosamente.
                } else {
                    showAlert("Error", "No se pudo añadir el comentario. Por favor, inténtalo de nuevo más tarde.");
                }
            });
        });
    }

    private String submitCommentToServer(String comment, int rating) {
        String response;

        try (Socket socket = new Socket(IP, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("INSERT_COMMENT");
            out.println(comment);
            out.println(rating);
            out.println(selectedMovieId);
            out.println(correo);

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseBuilder.append(line).append("\n");
            }
            response = responseBuilder.toString().trim();

        } catch (IOException e) {
            response = "ERROR";
        }

        return response;
    }

    private List<String> fetchMovieNamesFromServer() {
        List<String> movieNames = new ArrayList<>();

        try (Socket socket = new Socket(IP, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_MOVIE_NAME");

            String line;
            while ((line = in.readLine()) != null && !line.equals("END")) {
                movieNames.add(line);
            }

        } catch (IOException e) {
            movieNames = null;
        }

        return movieNames;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

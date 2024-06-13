package es.gestioncine.gestioncine.controllers;

import es.gestioncine.gestioncine.Configuration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private int MovieId;
    private int UserId; // Nueva variable para el ID del usuario
    private int currentRating;

    public void setCorreo(String correo) {
        this.correo = correo;
        fetchUserIdFromServer(); // Obtener el UserId basado en el correo
    }

    @FXML
    private void initialize() {
        initializeRatingBox();
        btnSubmit.setOnAction(event -> submitComment());

        // Set custom cell factory for ComboBox
        comboBox.setButtonCell(createStyledListCell());
        comboBox.setCellFactory(listView -> createStyledListCell());

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
            String selectedMovie = comboBox.getValue();
            if (selectedMovie != null) {
                retrieveMovieId(selectedMovie);
            }
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
        System.out.println(currentRating);
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

        Executors.newSingleThreadExecutor().execute(() -> {
            String result = submitCommentToServer(comment, currentRating);
            Platform.runLater(() -> {
                if ("INSERT_COMMENT_SUCCESS".equals(result)) {
                    ((Stage) btnSubmit.getScene().getWindow()).close();
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
            out.println(UserId); // Enviar el UserId en lugar del correo
            out.println(MovieId);
            out.println(rating);
            out.println(comment);

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

    private ListCell<String> createStyledListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #222831; -fx-text-fill: #EEEEEE; -fx-font-size: 12px;");
                    setMinHeight(comboBox.getMinHeight());
                    setPrefHeight(comboBox.getPrefHeight());
                    setMaxHeight(comboBox.getMaxHeight());
                }
            }
        };
    }

    private void retrieveMovieId(String movieName) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try (Socket socket = new Socket(IP, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("GET_MOVIE_ID");
                out.println(movieName);

                String response = in.readLine();
                MovieId = Integer.parseInt(response);
                System.out.println("Movie ID for " + movieName + " is " + MovieId);

            } catch (IOException | NumberFormatException e) {
                showAlert("Error", "No se pudo obtener el ID de la película.");
                e.printStackTrace();
            }
        });
    }

    private void fetchUserIdFromServer() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try (Socket socket = new Socket(IP, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("GET_USER_ID");
                out.println(correo);

                String response = in.readLine();
                UserId = Integer.parseInt(response.trim());
                System.out.println("User ID for " + correo + " is " + UserId);

            } catch (IOException | NumberFormatException e) {
                showAlert("Error", "No se pudo obtener el ID del usuario.");
                e.printStackTrace();
            }
        });
    }
}

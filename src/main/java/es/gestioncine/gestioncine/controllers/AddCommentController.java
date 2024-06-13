package es.gestioncine.gestioncine.controllers;

import es.gestioncine.gestioncine.Configuration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

        Executors.newSingleThreadExecutor().execute(() -> {
            String result = submitCommentToServer(comment, currentRating);
            Platform.runLater(() -> {
                if ("INSERT_COMMENT_SUCCESS".equals(result)) {
                    ((Stage) btnSubmit.getScene().getWindow()).close();
                } else {
                    // Show error message
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
}

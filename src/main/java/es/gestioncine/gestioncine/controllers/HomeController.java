package es.gestioncine.gestioncine.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeController {

    private static final String IP = "192.168.1.111";
    private static final int PORT = 12345;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @FXML
    private HBox imageBox;

    @FXML
    private Button leftButton;

    @FXML
    private Button rightButton;

    private List<ImageView> imageViews = new ArrayList<>();
    private int currentIndex = 0;

    @FXML
    public void initialize() {
        fetchImagePathsFromServer();
        leftButton.setDisable(true);
        rightButton.setDisable(true);
    }

    private void fetchImagePathsFromServer() {
        executorService.execute(() -> {
            try {
                Socket socket = new Socket(IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Enviamos orden al servidor.
                out.println("GET_ALL_IMAGE_PATHS");

                // Leemos respuesta.
                String imagePath;
                List<String> imagePathsList = new ArrayList<>();
                while ((imagePath = in.readLine()) != null) {
                    imagePathsList.add(imagePath);
                }

                // Cerramos el socket.
                out.close();
                in.close();
                socket.close();

                // Manejamos la respuesta del servidor en el hilo principal
                Platform.runLater(() -> handleServerResponse(imagePathsList));

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleServerResponse(List<String> imagePaths) {
        for (String imagePath : imagePaths) {
            ImageView imageView = new ImageView(new Image(imagePath));
            imageView.setPreserveRatio(true);
            imageView.setOnMouseClicked(event -> handleImageClick(imageView));
            imageViews.add(imageView);
        }
        updateVisibleImages();
        leftButton.setDisable(false);
        rightButton.setDisable(false);
    }

    private void updateVisibleImages() {
        imageBox.getChildren().clear();

        if (imageViews.size() >= 3) {
            int leftIndex = (currentIndex - 1 + imageViews.size()) % imageViews.size();
            int rightIndex = (currentIndex + 1) % imageViews.size();

            ImageView leftImageView = imageViews.get(leftIndex);
            leftImageView.setFitWidth(450);
            leftImageView.setFitHeight(650);

            ImageView centerImageView = imageViews.get(currentIndex);
            centerImageView.setFitWidth(500);
            centerImageView.setFitHeight(700);

            ImageView rightImageView = imageViews.get(rightIndex);
            rightImageView.setFitWidth(450);
            rightImageView.setFitHeight(650);

            imageBox.getChildren().add(leftImageView);
            imageBox.getChildren().add(centerImageView);
            imageBox.getChildren().add(rightImageView);
        }
    }

    @FXML
    private void moveLeft() {
        currentIndex = (currentIndex - 1 + imageViews.size()) % imageViews.size();
        updateVisibleImages();
    }

    @FXML
    private void moveRight() {
        currentIndex = (currentIndex + 1) % imageViews.size();
        updateVisibleImages();
    }

    private void handleImageClick(ImageView clickedImageView) {
        int clickedIndex = imageViews.indexOf(clickedImageView);

        int leftIndex = (currentIndex - 1 + imageViews.size()) % imageViews.size();
        int rightIndex = (currentIndex + 1) % imageViews.size();

        if (clickedIndex == leftIndex) {
            moveLeft();
        } else if (clickedIndex == rightIndex) {
            moveRight();
        }
    }
}

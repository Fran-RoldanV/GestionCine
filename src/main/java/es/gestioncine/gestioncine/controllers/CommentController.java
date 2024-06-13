package es.gestioncine.gestioncine.controllers;

import es.gestioncine.gestioncine.Configuration;
import es.gestioncine.gestioncine.models.Comment;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class CommentController {

    private final String IP = Configuration.IP;

    private final int PORT = Configuration.PORT;
    private String email = MainController.getInstance().getLblCorreoUsuario();
    @FXML
    private ListView<AnchorPane> commentListView;

    @FXML
    private Button addCommentButton;

    private final List<Comment> commentList = new ArrayList<>();

    @FXML
    private void initialize() {
        addCommentButton.setOnAction(event -> openAddCommentView());

        loadComments();
    }

    private void loadComments() {
        Executors.newSingleThreadExecutor().execute(() -> {
            String response;

            try (Socket socket = new Socket(IP, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("GET_COMMENTS");

                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    responseBuilder.append(line).append("\n");
                }
                response = responseBuilder.toString().trim();

            } catch (IOException e) {
                response = "ERROR";
            }

            final String result = response;
            Platform.runLater(() -> handleCommentsResponse(result));
        });
    }

    private void handleCommentsResponse(String result) {
        if (!"ERROR".equals(result)) {
            commentList.clear();
            String[] comments = result.split("\n");
            for (String commentData : comments) {
                String[] fields = commentData.split(";");
                if (fields.length == 5) {
                    String name = fields[0];
                    int rating = Integer.parseInt(fields[1]);
                    String commentText = fields[2];
                    String dateTime = fields[3];
                    String imageUrl = fields[4];
                    commentList.add(new Comment(name, rating, commentText, dateTime, imageUrl));
                }
            }
            updateCommentListView();
        } else {
            // Show error message
        }
    }

    private void updateCommentListView() {
        commentListView.getItems().clear();
        for (Comment comment : commentList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/gestioncine/gestioncine/views/CommentItem.fxml"));
                AnchorPane commentItem = loader.load();
                CommentItemController controller = loader.getController();
                controller.setData(comment);
                commentListView.getItems().add(commentItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openAddCommentView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/gestioncine/gestioncine/views/AddCommentView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            AddCommentController controller = loader.getController();
            controller.setCorreo(email); // pass the email if needed
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

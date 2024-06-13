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

public class CommentViewController {

    private final String IP = Configuration.IP;

    private final int PORT = Configuration.PORT;

    private String email = MainController.getInstance().getLblCorreoUsuario();

    @FXML
    private ListView<AnchorPane> listViewComments;

    @FXML
    private Button addCommentButton;

    @FXML
    private void initialize() {
        loadComments();
        addCommentButton.setOnAction(event -> openAddCommentView());
    }

    private void loadComments() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Comment> comments = fetchCommentsFromServer();
            Platform.runLater(() -> {
                for (Comment comment : comments) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/gestioncine/gestioncine/views/CommentItem.fxml"));
                        AnchorPane pane = loader.load();
                        CommentItemController controller = loader.getController();
                        controller.setData(comment);
                        listViewComments.getItems().add(pane);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    private List<Comment> fetchCommentsFromServer() {
        List<Comment> commentList = new ArrayList<>();
        try (Socket socket = new Socket(Configuration.IP, Configuration.PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_COMMENTS");
            String response;
            while ((response = in.readLine()) != null) {
                String[] fields = response.split(";");
                if (fields.length == 5) {
                    String name = fields[0];
                    int rating = Integer.parseInt(fields[1]);
                    String commentText = fields[2];
                    String dateTime = fields[3];
                    String imageUrl = fields[4];
                    commentList.add(new Comment(name, rating, commentText, dateTime, imageUrl));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return commentList;
    }

    private void openAddCommentView() {
        try {
            if (!email.isEmpty()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/gestioncine/gestioncine/views/AddCommentView.fxml"));
                AnchorPane pane = loader.load();
                AddCommentController controller = loader.getController();
                controller.setCorreo(email); // replace with the actual email
                Stage stage = new Stage();
                stage.setScene(new Scene(pane));
                stage.setTitle("Add Comment");
                stage.show();
            } else {
                MainController.getInstance().showIniciarSesion();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

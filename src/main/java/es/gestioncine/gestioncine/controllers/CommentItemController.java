package es.gestioncine.gestioncine.controllers;

import es.gestioncine.gestioncine.models.Comment;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class CommentItemController {

    @FXML
    private Label userName;

    @FXML
    private HBox ratingBox;

    @FXML
    private Label commentText;

    @FXML
    private Label dateTime;

    @FXML
    private ImageView movieImage;

    public void setData(Comment comment) {
        userName.setText(comment.getName());
        setRating(comment.getRating());
        commentText.setText(comment.getCommentText());
        dateTime.setText(comment.getDateTime());
        movieImage.setImage(new Image(comment.getImageUrl()));
    }

    private void setRating(int rating) {
        ratingBox.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(new Image(getClass().getResourceAsStream("/es/gestioncine/gestioncine/resources/img/star.png")));
            if (i < rating) {
                star.setImage(new Image(getClass().getResourceAsStream("/es/gestioncine/gestioncine/resources/img/star_filled.png")));
            }
            star.setFitWidth(40);
            star.setFitHeight(40);
            ratingBox.getChildren().add(star);
        }
    }
}

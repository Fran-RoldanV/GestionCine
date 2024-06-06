package es.gestioncine.gestioncine.adapters;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import es.gestioncine.gestioncine.interfaces.OnItemClickListener;

import java.util.List;

public class MovieAdapter {

    private List<String> movieUrls;
    private OnItemClickListener clickListener;
    private String category;

    public MovieAdapter(List<String> movieUrls, OnItemClickListener clickListener, String category) {
        this.movieUrls = movieUrls;
        this.clickListener = clickListener;
        this.category = category;
    }

    public VBox createItem(String movieUrl, int position) {
        VBox itemBox = new VBox();
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(movieUrl));
        imageView.setFitWidth(160);
        imageView.setFitHeight(240);
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            clickListener.onItemClick(position, category);
            event.consume();
        });
        itemBox.getChildren().add(imageView);
        return itemBox;
    }
}

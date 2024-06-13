package es.gestioncine.gestioncine.models;

public class Comment {
    private String name;
    private int rating;
    private String commentText;
    private String dateTime;
    private String imageUrl;

    public Comment(String name, int rating, String commentText, String dateTime, String imageUrl) {
        this.name = name;
        this.rating = rating;
        this.commentText = commentText;
        this.dateTime = dateTime;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
package es.gestioncine.gestioncine.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import es.gestioncine.gestioncine.adapters.MovieAdapter;
import es.gestioncine.gestioncine.interfaces.OnItemClickListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MovieController implements OnItemClickListener {

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private static final String IP = "192.168.1.111";
    private static final int PORT = 12345;

    @FXML
    private HBox mayor18;

    @FXML
    private HBox menor18;

    @FXML
    private HBox kids;

    private List<String> movieUrlsMayor18;
    private List<String> movieUrlsMenor18;
    private List<String> movieUrlsKids;

    @FXML
    public void initialize() {
        movieUrlsMayor18 = new ArrayList<>();
        movieUrlsMenor18 = new ArrayList<>();
        movieUrlsKids = new ArrayList<>();

        fetchMovieUrls("GET_MORE_18_IMAGES_PATH", movieUrlsMayor18, mayor18);
        fetchMovieUrls("GET_MINUS_18_IMAGES_PATH", movieUrlsMenor18, menor18);
        fetchMovieUrls("GET_KIDS_IMAGES_PATH", movieUrlsKids, kids);
    }

    private void fetchMovieUrls(String command, List<String> movieUrls, HBox hbox) {
        executorService.execute(() -> {
            try (Socket socket = new Socket(IP, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println(command);

                String imagePath;
                while ((imagePath = in.readLine()) != null) {
                    movieUrls.add(imagePath);
                }

                Platform.runLater(() -> {
                    MovieAdapter movieAdapter = new MovieAdapter(movieUrls, this);
                    hbox.getChildren().clear(); // Asegúrate de limpiar cualquier elemento existente
                    for (int i = 0; i < movieUrls.size(); i++) {
                        hbox.getChildren().add(movieAdapter.createItem(movieUrls.get(i), i));
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        String movieUrl = "";
        if (position < movieUrlsMayor18.size()) {
            movieUrl = movieUrlsMayor18.get(position);
        } else if (position < movieUrlsMayor18.size() + movieUrlsMenor18.size()) {
            movieUrl = movieUrlsMenor18.get(position - movieUrlsMayor18.size());
        } else if (position < movieUrlsMayor18.size() + movieUrlsMenor18.size() + movieUrlsKids.size()) {
            movieUrl = movieUrlsKids.get(position - movieUrlsMayor18.size() - movieUrlsMenor18.size());
        }

        // Redirigir a la página de datos de la película
        String correo = "CORREO_DEL_USUARIO"; // Asegúrate de obtener el correo del usuario de la forma correcta
        MainController.getInstance().showMovieData(movieUrl, correo);
    }

}

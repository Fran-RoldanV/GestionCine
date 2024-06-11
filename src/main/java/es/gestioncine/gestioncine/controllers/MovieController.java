package es.gestioncine.gestioncine.controllers;

import es.gestioncine.gestioncine.Configuration;
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
    private static final String IP = Configuration.IP;
    private static final int PORT = Configuration.PORT;

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

        fetchMovieUrls("GET_MORE_18_IMAGES_PATH", movieUrlsMayor18, mayor18, "MAYOR_18");
        fetchMovieUrls("GET_MINUS_18_IMAGES_PATH", movieUrlsMenor18, menor18, "MENOR_18");
        fetchMovieUrls("GET_KIDS_IMAGES_PATH", movieUrlsKids, kids, "KIDS");
    }

    private void fetchMovieUrls(String command, List<String> movieUrls, HBox hbox, String category) {
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
                    MovieAdapter movieAdapter = new MovieAdapter(movieUrls, this, category);
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
    public void onItemClick(int position, String category) {
        String movieUrl = "";

        switch (category) {
            case "MAYOR_18":
                movieUrl = movieUrlsMayor18.get(position);
                break;
            case "MENOR_18":
                movieUrl = movieUrlsMenor18.get(position);
                break;
            case "KIDS":
                movieUrl = movieUrlsKids.get(position);
                break;
        }

        // Debug: imprimir la categoría y la URL de la película seleccionada
        System.out.println("Categoría: " + category);
        System.out.println("URL de la película seleccionada: " + movieUrl);

        // Redirigir a la página de datos de la película
        if(MainController.getInstance().getLblCorreoUsuario().isEmpty()){
            MainController.getInstance().showIniciarSesion();
        }
        else {
            System.out.println(MainController.getInstance().getLblCorreoUsuario());
            MainController.getInstance().showMovieData(movieUrl, MainController.getInstance().getLblCorreoUsuario());
        }
    }
}

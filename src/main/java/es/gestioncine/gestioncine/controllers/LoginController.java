package es.gestioncine.gestioncine.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginController {

    private static final String IP = "192.168.0.108";
    private static final int PORT = 12345;

    @FXML
    private TextField campoUser;

    @FXML
    private PasswordField campoPassword;

    @FXML
    private Button botonSiguiente;

    @FXML
    private Hyperlink textoOlvidastePassword;

    @FXML
    private Label responseText;

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @FXML
    public void initialize() {
        botonSiguiente.setOnAction(e -> processLogin());
        textoOlvidastePassword.setOnAction(e -> handleForgotPassword());
    }

    private void processLogin() {
        String user = campoUser.getText().trim();
        String password = campoPassword.getText().trim();
        String passwordHashed = cifrarPassword(password);

        if (user.isEmpty() || password.isEmpty()) {
            responseText.setText("Por favor, rellene todos los campos.");
            return;
        }

        ordenServer(user, passwordHashed);
    }

    private void handleForgotPassword() {
        // Aquí puedes abrir una ventana de olvido de contraseña.
        responseText.setText("Función de recuperación de contraseña no implementada.");
    }

    private String cifrarPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void ordenServer(String user, String passwordHashed) {
        executorService.execute(() -> {
            try (Socket socket = new Socket(IP, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Enviamos orden al servidor.
                out.println("LOGIN");
                out.println(user);
                out.println(passwordHashed);

                // Leemos respuesta.
                final String response = in.readLine();
                handleServerResponse(response);

            } catch (Exception e) {
                handleServerResponse("ERROR");
            }
        });
    }

    private void handleServerResponse(String result) {
        if ("LOGIN_SUCCESS".equals(result)) {
            responseText.setText("Inicio de sesión exitoso.");
            // Aquí puedes cambiar a la vista principal de tu aplicación.
        } else if ("LOGIN_FAILED".equals(result)) {
            responseText.setText("Usuario o contraseña incorrectos.");
        } else {
            responseText.setText("Error de conexión.");
        }
    }
}

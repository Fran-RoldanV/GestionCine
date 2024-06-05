package es.gestioncine.gestioncine.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginController {
    @FXML
    private TextField campoUser;
    @FXML
    private PasswordField campoPassword;
    @FXML
    private Label responseText;

    @FXML
    private void handleLogin() {
        String email = campoUser.getText();
        String password = campoPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            responseText.setText("Todos los campos son obligatorios.");
            return;
        }

        String hashedPassword = hashPassword(password);

        if (hashedPassword == null) {
            responseText.setText("Error al procesar la contraseña.");
            return;
        }

        try (Socket socket = new Socket("localhost", 12345);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("LOGIN");
            out.println(email);
            out.println(hashedPassword);

            String response = in.readLine();
            switch (response) {
                case "LOGIN_SUCCESS" -> {
                    responseText.setText("Inicio de sesión exitoso.");
                    // Redirigir a la vista de inicio usando el MainController
                    MainController.getInstance().showHome();
                }
                case "LOGIN_FAILED" -> responseText.setText("Credenciales incorrectas. Inténtelo nuevamente.");
                default -> responseText.setText("Error desconocido.");
            }
        } catch (IOException e) {
            responseText.setText("Error de conexión: " + e.getMessage());
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        MainController.getInstance().showForgotPassword();
    }
}

package es.gestioncine.gestioncine.controllers;

import es.gestioncine.gestioncine.Configuration;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForgotPasswordController {

    private static final String IP = Configuration.IP;
    private static final int PORT = Configuration.PORT;

    @FXML
    private TextField campoUser;
    @FXML
    private VBox layoutPasswords;
    @FXML
    private PasswordField campoPassword;
    @FXML
    private PasswordField campoRepitePassword;
    @FXML
    private Button botonSiguiente;
    @FXML
    private Button botonContinuar;
    @FXML
    private Label mensajeError;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @FXML
    public void initialize() {
        layoutPasswords.setVisible(false);
        botonContinuar.setVisible(false);
        mensajeError.setText("");
    }

    @FXML
    protected void handleSiguienteAction(ActionEvent event) {
        String correo = campoUser.getText().trim();

        if (correo.isEmpty()) {
            mostrarMensajeError("Por favor, introduzca su correo");
            return;
        }

        // Verificar si el correo existe en la bd.
        ordenServer("FORGOT", correo, null);
    }

    @FXML
    protected void handleContinuarAction(ActionEvent event) {
        String correo = campoUser.getText().trim();
        String password = campoPassword.getText().trim();
        String repitePassword = campoRepitePassword.getText().trim();

        if (password.isEmpty() || repitePassword.isEmpty()) {
            mostrarMensajeError("Por favor, rellene todos los campos");
        } else if (!password.equals(repitePassword)) {
            mostrarMensajeError("Las contraseñas no coinciden");
            return;
        }

        // Encriptar la contraseña.
        String passwordHashed = cifrarPassword(password);

        // Actualiza la contraseña en la bd.
        ordenServer("UPDATE", correo, passwordHashed);
    }

    // Método para cifrar la contraseña usando SHA-256.
    public String cifrarPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            // Convertir el hash en formato hexadecimal
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

    // Método para enviar orden al servidor.
    private void ordenServer(String orden, String correo, String passwordHashed) {
        executorService.execute(() -> {
            String response = "";

            try {
                Socket socket = new Socket(IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                if (orden.equals("FORGOT")) {
                    // Enviamos orden al servidor.
                    out.println(orden);

                    // Enviamos correo al servidor.
                    out.println(correo);

                    // Leemos respuesta.
                    response = in.readLine();

                } else if (orden.equals("UPDATE")) {
                    // Enviamos orden al servidor.
                    out.println(orden);

                    // Enviamos correo y contraseña al servidor.
                    out.println(correo);
                    out.println(passwordHashed);

                    // Leemos respuesta.
                    response = in.readLine();
                }

                // Cerramos el socket.
                out.close();
                in.close();
                socket.close();

            } catch (IOException e) {
                response = "ERROR";
            }

            final String result = response;
            Platform.runLater(() -> handleServerResponse(result));
        });
    }

    // Método para manejar la respuesta del servidor.
    private void handleServerResponse(String result) {
        switch (result) {
            case "FORGOT_SUCCESS":
                layoutPasswords.setVisible(true);
                botonSiguiente.setVisible(false);
                botonContinuar.setVisible(true);
                mostrarMensajeError(""); // Clear any previous error messages
                break;

            case "FORGOT_FAILED":
                mostrarMensajeError("El correo no existe en la base de datos");
                break;

            case "UPDATE_SUCCESS":
                mostrarMensajeError("Contraseña actualizada");
                MainController.getInstance().showIniciarSesion();
                break;

            case "UPDATE_FAILED":
                mostrarMensajeError("Error al actualizar");
                break;

            default:
                mostrarMensajeError("Error de conexión");
                break;
        }
    }

    // Método para mostrar mensajes de error
    private void mostrarMensajeError(String message) {
        mensajeError.setText(message);
    }
}

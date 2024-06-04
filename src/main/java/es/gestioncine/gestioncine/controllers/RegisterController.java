package es.gestioncine.gestioncine.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterController {
    @FXML
    private TextField campoName;
    @FXML
    private TextField campoUser;
    @FXML
    private PasswordField campoPassword;
    @FXML
    private PasswordField campoRepitePassword;
    @FXML
    private DatePicker campoFecha;
    @FXML
    private CheckBox campoConsentimiento;
    @FXML
    private Label errorLabel;

    @FXML
    private void handleRegister() {
        String name = campoName.getText();
        String email = campoUser.getText();
        String password = campoPassword.getText();
        String repeatPassword = campoRepitePassword.getText();
        String birthdate = campoFecha.getValue().toString();
        boolean consent = campoConsentimiento.isSelected();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || birthdate.isEmpty()) {
            errorLabel.setText("Todos los campos son obligatorios.");
            return;
        }

        if (!consent) {
            errorLabel.setText("Debe aceptar los términos y la política de privacidad.");
            return;
        }

        if (!password.equals(repeatPassword)) {
            errorLabel.setText("Las contraseñas no coinciden.");
            return;
        }

        String hashedPassword = hashPassword(password);

        try (Socket socket = new Socket("localhost", 12345);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("REGISTER");
            out.println(name);
            out.println(email);
            out.println(hashedPassword);
            out.println(birthdate);

            String response = in.readLine();
            switch (response) {
                case "REGISTER_SUCCESS" -> {
                    errorLabel.setText("Registro exitoso.");
                    // Redirigir a la vista de inicio usando el MainController
                    MainController.getInstance().showHome();
                }
                case "REGISTER_FAILED" -> errorLabel.setText("Error en el registro. Inténtelo nuevamente.");
                case "NOT_VALID_EMAIL" -> errorLabel.setText("Correo electrónico no válido.");
                default -> errorLabel.setText("Error desconocido.");
            }
        } catch (IOException e) {
            errorLabel.setText("Error de conexión: " + e.getMessage());
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

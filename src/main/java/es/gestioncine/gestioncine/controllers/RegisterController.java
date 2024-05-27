package es.gestioncine.gestioncine.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterController {

    private static final String IP = "192.168.0.108";
    private static final int PORT = 12345;

    @FXML
    private TextField campoName;
    @FXML
    private TextField campoSurname;
    @FXML
    private TextField campoUser;
    @FXML
    private PasswordField campoPassword;
    @FXML
    private PasswordField campoRepitePassword;
    @FXML
    private TextField campoFecha;
    @FXML
    private CheckBox campoConsentimiento;
    @FXML
    private Button botonSiguiente;
    @FXML
    private Label responseText;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @FXML
    private void initialize() {
        botonSiguiente.setOnAction(event -> {
            String name = campoName.getText();
            String surname = campoSurname.getText();
            String email = campoUser.getText();
            String password = campoPassword.getText();
            String repitePassword = campoRepitePassword.getText();
            String passwordHashed = cifrarPassword(password);
            String birthdate = campoFecha.getText();
            boolean consentimiento = campoConsentimiento.isSelected();

            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || repitePassword.isEmpty()
                    || birthdate.isEmpty()) {
                responseText.setText("Por favor, rellene todos los campos.");
                return;
            }

            if (!password.equals(repitePassword)) {
                responseText.setText("Las contraseñas no coinciden.");
                return;
            }

            if (!consentimiento) {
                responseText.setText("Por favor, acepte los términos y la política de privacidad.");
                return;
            }

            ordenServer(name, surname, email, passwordHashed, birthdate);
        });
    }

    private void ordenServer(String name, String surname, String email, String passwordHashed, String birthdate) {
        executorService.submit(() -> {
            String response = authenticationTask("REGISTER", name, surname, email, passwordHashed, birthdate);

            Platform.runLater(() -> {
                if (response.equals("REGISTER_SUCCESS")) {
                    responseText.setText("Registro exitoso.");
                    // Aquí podría colocar código para cambiar de ventana.
                } else if (response.equals("REGISTER_FAILED")) {
                    responseText.setText("Algo ha ido mal...");
                } else {
                    responseText.setText("Error de conexión.");
                }
            });
        });
    }

    private String authenticationTask(String... params) {
        try (Socket socket = new Socket(IP, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            for (String param : params) {
                out.println(param);
            }

            return in.readLine();
        } catch (IOException e) {
            return "Error de conexión";
        }
    }

    public String cifrarPassword(String password) {
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

    // Metodo para cerrar el executor al cerrar la aplicación.
    public void shutdown() {
        executorService.shutdown();
    }
}

package es.gestioncine.gestioncine.controllers;

import es.gestioncine.gestioncine.Configuration;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentController {

    private String IP = Configuration.IP;
    private int PORT = Configuration.PORT;

    @FXML
    private String emailFactura = MainController.getInstance().getLblCorreoUsuario();

    @FXML
    private ComboBox<String> spinnerPaymentMethod;

    @FXML
    private VBox layoutCreditCard, layoutPaypal, layoutBizum, layoutCashDesk;

    @FXML
    private TextField creditCardNumber, creditCardName, creditCardExpiration, creditCardCVV;

    @FXML
    private TextField paypalEmail, paypalPassword;

    @FXML
    private TextField bizumPhone;

    @FXML
    private Button buttonConfirmPayment;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private int idUsuario, idPelicula, butacasReservadas;
    private String estadoReserva, metodoPago, sala, hora;
    private double totalPagar;

    public void initialize(int idUsuario, int idPelicula, String sala, String hora, String estadoReserva, int butacasReservadas) {
        this.idUsuario = idUsuario;
        this.idPelicula = idPelicula;
        this.sala = sala;
        this.hora = hora;
        this.estadoReserva = estadoReserva;
        this.butacasReservadas = butacasReservadas;

        // Configure spinnerPaymentMethod ComboBox
        spinnerPaymentMethod.setOnAction(event -> {
            String selectedMethod = spinnerPaymentMethod.getValue();
            mostrarLayout(selectedMethod);
        });

        spinnerPaymentMethod.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item);
                            setStyle("-fx-background-color: #222831; -fx-text-fill: #EEEEEE; -fx-pref-height: 50.0;");
                        }
                    }
                };
            }
        });

        spinnerPaymentMethod.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #222831; -fx-text-fill: #EEEEEE; -fx-pref-height: 50.0;");
                }
            }
        });

        buttonConfirmPayment.setOnAction(event -> {
            metodoPago = spinnerPaymentMethod.getValue().trim();
            if (!validarCampos()) {
                mostrarMensaje("Por favor, complete todos los campos obligatorios.");
                return;
            }
            ordenPayment(metodoPago, totalPagar, idUsuario);
        });
    }

    private void mostrarLayout(String metodo) {
        layoutCreditCard.setVisible(false);
        layoutCreditCard.setManaged(false);
        layoutPaypal.setVisible(false);
        layoutPaypal.setManaged(false);
        layoutBizum.setVisible(false);
        layoutBizum.setManaged(false);
        layoutCashDesk.setVisible(false);
        layoutCashDesk.setManaged(false);

        switch (metodo) {
            case "Tarjeta de crédito":
                layoutCreditCard.setVisible(true);
                layoutCreditCard.setManaged(true);
                break;
            case "PayPal":
                layoutPaypal.setVisible(true);
                layoutPaypal.setManaged(true);
                break;
            case "Bizum":
                layoutBizum.setVisible(true);
                layoutBizum.setManaged(true);
                break;
            case "Cobro en taquilla":
                layoutCashDesk.setVisible(true);
                layoutCashDesk.setManaged(true);
                break;
        }
    }

    private boolean validarCampos() {
        if (emailFactura.isEmpty()) {
            return false;
        }

        switch (metodoPago) {
            case "Tarjeta de crédito":
                return !creditCardNumber.getText().toString().trim().isEmpty()
                        && !creditCardName.getText().toString().trim().isEmpty()
                        && !creditCardExpiration.getText().toString().trim().isEmpty()
                        && !creditCardCVV.getText().toString().trim().isEmpty();
            case "PayPal":
                return !paypalEmail.getText().toString().trim().isEmpty()
                        && !paypalPassword.getText().toString().trim().isEmpty();
            case "Bizum":
                return !bizumPhone.getText().toString().trim().isEmpty();
            case "Cobro en taquilla":
                return true;
            default:
                return false;
        }
    }

    private void ordenPayment(String metodoPago, Double totalPagar, int idUsuario) {
        executorService.execute(() -> {
            String response = "";

            try {
                Socket socket = new Socket(IP, PORT);  // Reemplaza con tu IP y puerto
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("INSERT_TRANSACTION");
                out.println(metodoPago);
                out.println(totalPagar);
                out.println(idUsuario);

                response = in.readLine();

                out.close();
                in.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            final String result = response;
            javafx.application.Platform.runLater(() -> procesarPago(result));
        });
    }

    private void procesarPago(String result) {
        if ("INSERT_TRANSACTION_SUCCESS".equals(result)) {
            ordenReserve(idUsuario, idPelicula, sala, hora, butacasReservadas);
        } else {
            mostrarMensaje("Error al realizar la reserva.");
        }
    }

    private void ordenReserve(int idUsuario, int idPelicula, String sala, String hora, int butacasReservadas) {
        executorService.execute(() -> {
            String response = "";
            try {
                Socket socket = new Socket(IP, PORT);  // Reemplaza con tu IP y puerto
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("INSERT_RESERVE");
                out.println(idUsuario);
                out.println(idPelicula);
                out.println(sala);
                out.println(hora);
                out.println(estadoReserva);
                out.println(butacasReservadas);

                response = in.readLine();

                out.close();
                in.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            final String result = response;
            javafx.application.Platform.runLater(() -> procesarReserva(result));
        });
    }

    private void procesarReserva(String result) {
        if ("INSERT_MOVIE_SUCCESS".equals(result)) {
            mostrarMensaje("Reserva realizada con éxito.");
            // Navegar a la pantalla principal
        } else {
            mostrarMensaje("Error al realizar la reserva.");
            // Navegar a la pantalla principal
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

package api.supermercado.apisupermercadoempleados.controller;

import api.supermercado.apisupermercadoempleados.HelloApplication;
import api.supermercado.apisupermercadoempleados.model.Sesion;
import api.supermercado.apisupermercadoempleados.service.AsignacionService;
import api.supermercado.apisupermercadoempleados.service.AuthService;
import api.supermercado.apisupermercadoempleados.model.LoginResponse;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;

    private final AuthService authService = new AuthService();
    private final AsignacionService asignacionService = new AsignacionService();

    @FXML
    private void onLogin() {
        String correo = txtCorreo.getText() == null ? "" : txtCorreo.getText().trim();
        String pass = txtContrasena.getText() == null ? "" : txtContrasena.getText().trim();

        if (correo.isBlank() || pass.isBlank()) {
            alert("Faltan datos", "Poné correo y contraseña.");
            return;
        }

        Task<LoginResponse> task = new Task<>() {
            @Override
            protected LoginResponse call() throws Exception {
                return authService.login(correo, pass);
            }
        };

        task.setOnSucceeded(e -> {
            LoginResponse res = task.getValue();

            if (res == null || res.getToken() == null || res.getToken().isBlank()) {
                alert("Login inválido", "El servidor no devolvió token.");
                return;
            }
            Sesion.set(res.getToken(), res.getRol(), res.getUsuarioId());

            verificarAsignacionYEntrar();
        });

        task.setOnFailed(e -> alert("Error", task.getException().getMessage()));

        new Thread(task).start();
    }

    private void verificarAsignacionYEntrar() {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return asignacionService.tieneAsignacionBodegaHoy();
            }
        };

        task.setOnSucceeded(e -> {
            boolean tiene = task.getValue();
            if (tiene) {
                abrirBodega();
                cerrarVentanaActual();
            } else {
                alert("Sin asignación", "Hoy no tenés asignación en Bodega.");
            }
        });

        task.setOnFailed(e -> alert("Error", task.getException().getMessage()));

        new Thread(task).start();
    }

    private void abrirBodega() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("/api/supermercado/apisupermercadoempleados/view/Bodega.fxml")
            );
            Scene scene = new Scene(loader.load(), 1100, 700);

            Stage stageNuevo = new Stage();
            stageNuevo.setTitle("Bodega");
            stageNuevo.setScene(scene);
            stageNuevo.show();

            Stage stageActual = (Stage) txtCorreo.getScene().getWindow();
            stageActual.close();
        } catch (Exception ex) {
            alert("Error", ex.getClass().getName() + "\n" + ex.getMessage());
        }
    }

    private void cerrarVentanaActual() {
        Stage stage = (Stage) txtCorreo.getScene().getWindow();
        stage.close();
    }

    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
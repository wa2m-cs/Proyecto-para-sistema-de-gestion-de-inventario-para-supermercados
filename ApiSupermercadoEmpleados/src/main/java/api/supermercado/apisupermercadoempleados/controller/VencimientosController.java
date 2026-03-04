package api.supermercado.apisupermercadoempleados.controller;

import api.supermercado.apisupermercadoempleados.model.VencimientoDTO;
import api.supermercado.apisupermercadoempleados.service.TiendaService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

public class VencimientosController {

    @FXML private Button btnVolver;
    @FXML private DatePicker dpAntesDe;
    @FXML private TableView<VencimientoDTO> tblVencimientos;
    @FXML private TableColumn<VencimientoDTO, String> colProducto;
    @FXML private TableColumn<VencimientoDTO, Number> colCantidad;
    @FXML private TableColumn<VencimientoDTO, String> colUbicacion;
    @FXML private TableColumn<VencimientoDTO, String> colFecha;

    private final TiendaService api = new TiendaService();
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        dpAntesDe.setValue(LocalDate.now().plusDays(30));

        colProducto.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().producto != null ? d.getValue().producto.nombre : ""
                )
        );
        colCantidad.setCellValueFactory(d ->
                new javafx.beans.property.SimpleIntegerProperty(d.getValue().cantidad)
        );
        colUbicacion.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().ubicacion != null ? d.getValue().ubicacion.nombre : ""
                )
        );
        colFecha.setCellValueFactory(d -> {
            String fv = d.getValue().fechaVencimiento;
            String onlyDate = (fv == null || fv.length() < 10) ? "" : fv.substring(0,10);
            return new javafx.beans.property.SimpleStringProperty(onlyDate);
        });

        loadVencimientos();
    }

    @FXML
    public void loadVencimientos() {
        LocalDate d = dpAntesDe.getValue();
        if (d == null) d = LocalDate.now().plusDays(30);

        String date = d.toString(); // yyyy-MM-dd

        Task<List<VencimientoDTO>> task = new Task<>() {
            @Override
            protected List<VencimientoDTO> call() throws Exception {
                String json = api.get("/api/inventario/vencimientos?antesDe=" + date);
                Type listType = new TypeToken<List<VencimientoDTO>>(){}.getType();
                return gson.fromJson(json, listType);
            }
        };

        task.setOnSucceeded(e -> tblVencimientos.getItems().setAll(task.getValue()));
        task.setOnFailed(e -> showError(task.getException()));
        new Thread(task).start();
    }

    @FXML
    public void volver() {
        cambiarPantalla("/api/supermercado/apisupermercadoempleados/view/Bodega.fxml", "Bodega");
    }

    private void cambiarPantalla(String fxml, String title) {
        try {
            var url = getClass().getResource(fxml);
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load(), 1100, 700);
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Throwable ex) {
        ex.printStackTrace();
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(ex.getClass().getSimpleName() + ": " + ex.getMessage());
        a.showAndWait();
    }
}
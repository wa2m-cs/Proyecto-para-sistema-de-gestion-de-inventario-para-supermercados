package api.supermercado.apisupermercadoempleados.controller;

import api.supermercado.apisupermercadoempleados.model.EntradaDTO;
import api.supermercado.apisupermercadoempleados.model.StockDTO;
import api.supermercado.apisupermercadoempleados.model.UbicacionDTO;
import api.supermercado.apisupermercadoempleados.service.TiendaService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

public class StockBajoController {

    @FXML private Button btnVolver;
    @FXML private TableView<StockDTO> tblStockBajo;
    @FXML private TableColumn<StockDTO, Number> colId;
    @FXML private TableColumn<StockDTO, String> colNombre;
    @FXML private TableColumn<StockDTO, Number> colStock;

    private final TiendaService api = new TiendaService();
    private final Gson gson = new Gson();

    private final int min = 100;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().productoId));
        colNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().nombreProducto));
        colStock.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().stockTotal));

        loadStockBajo();
    }

    @FXML
    public void loadStockBajo() {
        Task<List<StockDTO>> task = new Task<>() {
            @Override
            protected List<StockDTO> call() throws Exception {
                String json = api.get("/api/inventario/stock?bajoMinimo=true&min=" + min);
                Type listType = new TypeToken<List<StockDTO>>(){}.getType();
                return gson.fromJson(json, listType);
            }
        };

        task.setOnSucceeded(e -> tblStockBajo.getItems().setAll(task.getValue()));
        task.setOnFailed(e -> showError(task.getException()));
        new Thread(task).start();
    }

    @FXML
    public void subirStock() {
        StockDTO sel = tblStockBajo.getSelectionModel().getSelectedItem();
        if (sel == null) {
            info("Seleccioná", "Elegí un producto de la tabla.");
            return;
        }

        List<UbicacionDTO> ubicaciones;
        try {
            String json = api.get("/api/inventario/ubicaciones");
            Type listType = new TypeToken<List<UbicacionDTO>>(){}.getType();
            ubicaciones = gson.fromJson(json, listType);
        } catch (Exception ex) {
            showError(ex);
            return;
        }

        Dialog<EntradaDTO> dialog = new Dialog<>();
        dialog.setTitle("Subir stock");
        dialog.setHeaderText("Producto: " + sel.nombreProducto + " (ID " + sel.productoId + ")");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        TextField txtCantidad = new TextField();
        txtCantidad.setPromptText("Cantidad");

        ComboBox<UbicacionDTO> cmbUbicacion = new ComboBox<>();
        cmbUbicacion.getItems().setAll(ubicaciones);

        DatePicker dpVence = new DatePicker(LocalDate.now().plusMonths(1));

        VBox box = new VBox(10,
                new Label("Cantidad:"), txtCantidad,
                new Label("Ubicación:"), cmbUbicacion,
                new Label("Fecha vencimiento (lote nuevo):"), dpVence
        );
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(bt -> {
            if (bt != btnGuardar) return null;

            int cantidad;
            try {
                cantidad = Integer.parseInt(txtCantidad.getText().trim());
            } catch (Exception ex) {
                return null;
            }
            if (cantidad <= 0) return null;

            UbicacionDTO u = cmbUbicacion.getValue();
            if (u == null) return null;

            LocalDate fv = dpVence.getValue();
            if (fv == null) return null;

            return new EntradaDTO(sel.productoId, u.ubicacionId, cantidad, fv.toString());
        });

        var result = dialog.showAndWait();
        if (result.isEmpty()) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String body = gson.toJson(result.get());
                api.post("/api/inventario/entradas", body);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            info("Listo", "Entrada registrada correctamente.");
            loadStockBajo();
        });
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

    private void info(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
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
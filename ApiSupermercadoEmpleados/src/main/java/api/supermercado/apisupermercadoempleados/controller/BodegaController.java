package api.supermercado.apisupermercadoempleados.controller;

import api.supermercado.apisupermercadoempleados.model.*;
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
import java.util.*;
import java.util.stream.Collectors;

public class BodegaController {

    @FXML private TableView<ProductoStockView> tbl;
    @FXML private TableColumn<ProductoStockView, Number> colId;
    @FXML private TableColumn<ProductoStockView, String> colNombre;
    @FXML private TableColumn<ProductoStockView, Number> colPrecio;
    @FXML private TableColumn<ProductoStockView, Number> colStock;

    private final TiendaService api = new TiendaService();
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().productoId));
        colNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().nombre));
        colPrecio.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().precio));
        colStock.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().stockTotal));

        loadProductosStock();
    }

    @FXML
    public void loadProductosStock() {
        Task<List<ProductoStockView>> task = new Task<>() {
            @Override
            protected List<ProductoStockView> call() throws Exception {

                String prodJson = api.get("/api/productos");
                Type prodType = new TypeToken<List<ProductoDTO>>(){}.getType();
                List<ProductoDTO> productos = gson.fromJson(prodJson, prodType);

                String stockJson = api.get("/api/inventario/stock");
                Type stockType = new TypeToken<List<StockDTO>>(){}.getType();
                List<StockDTO> stockList = gson.fromJson(stockJson, stockType);

                Map<Integer, Integer> stockMap = stockList.stream()
                        .collect(Collectors.toMap(s -> s.productoId, s -> s.stockTotal, (a,b)->a));

                List<ProductoStockView> vm = new ArrayList<>();
                for (ProductoDTO p : productos) {
                    int stock = stockMap.getOrDefault(p.productoId, 0);
                    vm.add(new ProductoStockView(p.productoId, p.nombre, p.precio, stock));
                }

                vm.sort(Comparator.comparingInt(a -> a.stockTotal));
                return vm;
            }
        };

        task.setOnSucceeded(e -> tbl.getItems().setAll(task.getValue()));
        task.setOnFailed(e -> showError(task.getException()));
        new Thread(task).start();
    }

    @FXML
    public void nuevaEntrada() {
        ProductoStockView sel = tbl.getSelectionModel().getSelectedItem();
        if (sel == null) {
            info("Seleccioná", "Elegí un producto de la tabla.");
            return;
        }
        List<UbicacionDTO> ubicaciones;
        try {
            String json = api.get("/api/inventario/ubicaciones");
            Type type = new TypeToken<List<UbicacionDTO>>(){}.getType();
            ubicaciones = gson.fromJson(json, type);
        } catch (Exception ex) {
            showError(ex);
            return;
        }

        Dialog<EntradaDTO> dialog = new Dialog<>();
        dialog.setTitle("Nueva entrada");
        dialog.setHeaderText("Producto: " + sel.nombre + " (ID " + sel.productoId + ")");

        ButtonType guardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardar, ButtonType.CANCEL);

        TextField txtCantidad = new TextField();
        txtCantidad.setPromptText("Cantidad (ej: 20)");

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
            if (bt != guardar) return null;

            int cantidad;
            try { cantidad = Integer.parseInt(txtCantidad.getText().trim()); }
            catch (Exception ex) { return null; }

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
            info("Listo", "Entrada guardada correctamente.");
            loadProductosStock();
        });
        task.setOnFailed(e -> showError(task.getException()));
        new Thread(task).start();
    }

    @FXML
    private void goStockBajo() {
        cambiarPantalla("/api/supermercado/apisupermercadoempleados/view/stock_bajo.fxml", "Stock Bajo");
    }

    @FXML
    private void goVencimientos() {
        cambiarPantalla("/api/supermercado/apisupermercadoempleados/view/vencimientos.fxml", "Vencimientos");
    }

    private void cambiarPantalla(String fxml, String title) {
        try {
            var url = getClass().getResource(fxml);
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load(), 1100, 700);
            Stage stage = (Stage) tbl.getScene().getWindow();
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
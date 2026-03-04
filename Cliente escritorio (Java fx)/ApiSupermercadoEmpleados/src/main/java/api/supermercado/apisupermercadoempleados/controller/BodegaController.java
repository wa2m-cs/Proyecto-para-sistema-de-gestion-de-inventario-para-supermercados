package api.supermercado.apisupermercadoempleados.controller;

import api.supermercado.apisupermercadoempleados.model.StockDTO;
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
import java.util.List;

public class BodegaController {

    @FXML private TableView<StockDTO> tblStock;
    @FXML private TableColumn<StockDTO, Number> colId;
    @FXML private TableColumn<StockDTO, String> colNombre;
    @FXML private TableColumn<StockDTO, Number> colStock;

    private final TiendaService api = new TiendaService();
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().productoId));
        colNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().nombreProducto));
        colStock.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().stockTotal));

        loadStock();
    }

    @FXML
    private void loadStock() {
        Task<List<StockDTO>> task = new Task<>() {
            @Override
            protected List<StockDTO> call() throws Exception {
                String json = api.get("/api/inventario/stock");
                Type listType = new TypeToken<List<StockDTO>>() {}.getType();
                return gson.fromJson(json, listType);
            }
        };

        task.setOnSucceeded(e -> tblStock.getItems().setAll(task.getValue()));
        task.setOnFailed(e -> alert("Error", task.getException().getMessage()));
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
            Stage stage = (Stage) tblStock.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            alert("Error", ex.getMessage());
        }
    }

    private void alert(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}
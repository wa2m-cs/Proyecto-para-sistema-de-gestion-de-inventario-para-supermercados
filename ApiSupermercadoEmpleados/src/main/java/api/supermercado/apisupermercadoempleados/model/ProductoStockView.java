package api.supermercado.apisupermercadoempleados.model;

public class ProductoStockView {
    public int productoId;
    public String nombre;
    public double precio;
    public int stockTotal;

    public ProductoStockView(int id, String nombre, double precio, int stock) {
        this.productoId = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stockTotal = stock;
    }
}

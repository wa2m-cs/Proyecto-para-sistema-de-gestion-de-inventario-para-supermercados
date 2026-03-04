package api.supermercado.apisupermercadoempleados.model;

public class EntradaDTO {
    public Integer productoId;
    public Integer ubicacionId;
    public int cantidad;
    public String fechaVencimiento;

    public EntradaDTO(Integer productoId, Integer ubicacionId, int cantidad, String fechaVencimiento) {
        this.productoId = productoId;
        this.ubicacionId = ubicacionId;
        this.cantidad = cantidad;
        this.fechaVencimiento = fechaVencimiento;
    }
}

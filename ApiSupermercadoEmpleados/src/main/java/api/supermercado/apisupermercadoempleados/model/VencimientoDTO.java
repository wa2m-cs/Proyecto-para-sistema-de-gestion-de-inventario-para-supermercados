package api.supermercado.apisupermercadoempleados.model;

import java.time.LocalDateTime;

public class VencimientoDTO {
    public int inventarioId;
    public Integer productoId;
    public Integer ubicacionId;
    public int cantidad;
    public String fechaVencimiento;
    public ProductoMini producto;
    public UbicacionMini ubicacion;

    public static class ProductoMini {
        public String nombre;
    }

    public static class UbicacionMini {
        public String nombre;
    }
}

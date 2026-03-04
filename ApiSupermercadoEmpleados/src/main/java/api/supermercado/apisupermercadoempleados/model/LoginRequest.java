package api.supermercado.apisupermercadoempleados.model;

public class LoginRequest {
    private String correo;
    private String Contrasena;

    public LoginRequest(String correo, String Contrasena) {
        this.correo = correo;
        this.Contrasena = Contrasena;
    }

    public String getCorreo() { return correo; }
    public String getContrasena() { return Contrasena; }
}

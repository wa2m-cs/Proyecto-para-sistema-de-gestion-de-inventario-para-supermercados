package api.supermercado.apisupermercadoempleados.model;

public class LoginResponse {
    private String token;
    private String rol;
    private int usuarioId;

    public String getToken() { return token; }
    public String getRol() { return rol; }
    public int getUsuarioId() {return usuarioId;}
}
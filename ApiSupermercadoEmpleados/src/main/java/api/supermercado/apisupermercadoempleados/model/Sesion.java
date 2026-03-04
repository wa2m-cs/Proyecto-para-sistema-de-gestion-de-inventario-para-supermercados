package api.supermercado.apisupermercadoempleados.model;

public class Sesion {
    private static String token;
    private static String rol;
    private static int usuarioId;

    public static void set(String jwt, String role, int id) {
        token = jwt;
        rol = role;
        usuarioId = id;
    }

    public static String token() { return token; }
    public static String rol() { return rol; }
    public static int usuarioId() { return usuarioId; }

    public static boolean isLogged() {
        return token != null && !token.isBlank();
    }

    public static void clear() {
        token = null;
        rol = null;
        usuarioId = 0;
    }
}
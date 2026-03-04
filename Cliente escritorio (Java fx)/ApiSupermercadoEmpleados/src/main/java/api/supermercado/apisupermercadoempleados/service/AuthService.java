package api.supermercado.apisupermercadoempleados.service;
import com.google.gson.Gson;
import api.supermercado.apisupermercadoempleados.model.LoginRequest;
import api.supermercado.apisupermercadoempleados.model.LoginResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    private final String baseUrl = "http://localhost:5097";

    public LoginResponse login(String usuario, String contrasena) throws Exception {
        var bodyObj = new LoginRequest(usuario, contrasena);
        var jsonBody = gson.toJson(bodyObj);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return gson.fromJson(response.body(), LoginResponse.class);
        }

        throw new RuntimeException("Login falló (HTTP " + response.statusCode() + "): ");
    }
}
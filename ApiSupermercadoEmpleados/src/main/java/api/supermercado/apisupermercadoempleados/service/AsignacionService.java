package api.supermercado.apisupermercadoempleados.service;

import api.supermercado.apisupermercadoempleados.model.Sesion;

import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;

public class AsignacionService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String baseUrl = "http://localhost:5097";

    public boolean tieneAsignacionBodegaHoy() throws Exception {
        int usuarioId = Sesion.usuarioId();
        LocalDate hoy = LocalDate.now();

        String url = baseUrl + "/api/asignaciones/bodega?usuarioId=" + usuarioId + "&fecha=" + hoy;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + Sesion.token())
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body().trim().equalsIgnoreCase("true");
        }

        throw new RuntimeException("Asignación HTTP " + response.statusCode() + " -> " + response.body());
    }
}

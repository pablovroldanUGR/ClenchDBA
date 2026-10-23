/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author pablo
 */
public class ConexionAPI {

    String obtenerRetos() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest discovery = HttpRequest.newBuilder()
                .uri(URI.create(api + "/challenges"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> found = http.send(discovery, HttpResponse.BodyHandlers.ofString());
        int discoveryStatus = found.statusCode();
        String discoveryJson = found.body();
        return discoveryJson;
    }

    String iniciarReto() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");
        long challengeId = Long.parseLong(System.getenv("DBA_CHALLENGE_ID"));
        String body = "{\"challengeId\":" + challengeId + "}";
        HttpRequest start = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> started = http.send(start, HttpResponse.BodyHandlers.ofString());
        int startStatus = started.statusCode();
        String startJson = started.body();
        return startJson;
    }

    String obtenerTemperatura(String runId) throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");
        String sensorId = "thermostat.temperature";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + runId + "/perceptions/" + sensorId))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    String obtenerLimiteMaximo (String runId) throws java.io.IOException, InterruptedException {
        return obtenerLimite(runId, "upper");
    }

    String obtenerLimiteMinimo (String runId) throws java.io.IOException, InterruptedException {
        return obtenerLimite(runId, "lower");
    }

    private String obtenerLimite(String runId, String limite) throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");
        String sensorId = "thermostat.limits";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + runId + "/perceptions/" + sensorId))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        Pattern pattern = Pattern.compile(
                "\"value\"\\s*:\\s*\\{[^{}]*?\"" + Pattern.quote(limite)
                + "\"\\s*:\\s*(-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?)");
        Matcher matcher = pattern.matcher(response.body());
        if (!matcher.find()) {
            throw new java.io.IOException("La respuesta no contiene value." + limite + " numérico");
        }
        return matcher.group(1);
    }

    String obtenerTermperaturaObjetivo (String runId) throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");
        String sensorId = "thermostat.target";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + runId + "/perceptions/" + sensorId))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    String activarRefrigeracion(String runId) throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");
        String actionCode = "thermostat.refrigeration.on";
        String body = "{\"actionCode\":\"" + actionCode + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + runId + "/actions"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    String desactivarRefrigeracion(String runId) throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");
        String actionCode = "thermostat.refrigeration.off";
        String body = "{\"actionCode\":\"" + actionCode + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + runId + "/actions"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

}

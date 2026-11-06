package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import common.Pair;

public class ConexAPI {

    private String runID;

    public String getRunID() {
        return this.runID;
    }

    public void setRunId(String runID) {
        this.runID = runID;
    }

    public Pair<JsonElement> getChallenges() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest discovery = HttpRequest.newBuilder()
                .uri(URI.create(api + "/challenges"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> found = http.send(discovery, HttpResponse.BodyHandlers.ofString());

        // El listado de retos también puede ser un array JSON y no tiene un run asociado.
        JsonElement json = readResponse(found);
        Control control = new Control();
        if (json.isJsonObject()) {
            return new Pair<>(control, control.update(json.toString()));
        }
        return new Pair<>(control, json);
    }

    public Pair<JsonObject> getStatus() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest statusRequest = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + this.runID))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> found = http.send(statusRequest, HttpResponse.BodyHandlers.ofString());

        return parseResponse(found);
    }

    public Pair<JsonObject> startChallenge() throws java.io.IOException, InterruptedException {
        int challengeId = Integer.parseInt(System.getenv("DBA_CHALLENGE_ID"));
        return startChallenge(challengeId);
    }

    public Pair<JsonObject> startChallenge(int challengeId) throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");
        String body = "{\"challengeId\":" + challengeId + "}";

        HttpRequest start = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> started = http.send(start, HttpResponse.BodyHandlers.ofString());

        Pair<JsonObject> result = parseResponse(started);
        if (result.control.runStatus == null) {
            throw new java.io.IOException("La respuesta de inicio no contiene un control válido");
        }

        this.runID = String.valueOf(result.control.runID);
        System.out.println("Se ha iniciado una nueva run: " + this.runID);
        return result;
    }

    public Pair<JsonObject> getSensor(String sensorId) throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + this.runID + "/perceptions/" + sensorId))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        return parseResponse(response);
    }

    public Pair<JsonObject> getTemperature() throws java.io.IOException, InterruptedException {
        return getSensor("thermostat.temperature");
    }

    public Pair<JsonObject> getControl() throws java.io.IOException, InterruptedException {
        return getStatus();
    }

    public Pair<JsonObject> getMaximumLimit() throws java.io.IOException, InterruptedException {
        return getSensor("thermostat.limits");
    }

    public Pair<JsonObject> getMinimumLimit() throws java.io.IOException, InterruptedException {
        return getSensor("thermostat.limits");
    }

    public Pair<JsonObject> getObjectiveTemperature() throws java.io.IOException, InterruptedException {
        return getSensor("thermostat.target");
    }

    public Pair<JsonObject> executeAction(String actionCode) throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        JsonObject body = new JsonObject();
        body.addProperty("actionCode", actionCode);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + this.runID + "/actions"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        return parseResponse(response);
    }

    public Pair<JsonObject> activateRefrigeration() throws java.io.IOException, InterruptedException {
        return executeAction("thermostat.refrigeration.on");
    }

    public Pair<JsonObject> disableRefrigeration() throws java.io.IOException, InterruptedException {
        return executeAction("thermostat.refrigeration.off");
    }

    public Pair<JsonObject> closeChallenge() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        if (this.runID == null || this.runID.isBlank()) {
            throw new java.io.IOException("No existe un runID para cerrar el reto.");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + this.runID + "/close"))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        Pair<JsonObject> result = parseResponse(response);
        System.out.println("[DBA LAB] Solicitud de cierre aceptada: " + this.runID);
        return result;
    }

    // Todas las respuestas de un run se separan usando el método de Control.
    private Pair<JsonObject> parseResponse(HttpResponse<String> response) throws java.io.IOException {
        JsonElement json = readResponse(response);
        if (!json.isJsonObject()) {
            throw new java.io.IOException("La respuesta de la API no es un objeto JSON");
        }

        Control control = new Control();
        JsonObject particular = control.update(json.toString());
        return new Pair<>(control, particular);
    }

    private JsonElement readResponse(HttpResponse<String> response) throws java.io.IOException {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new java.io.IOException("Error en la API. HTTP " + response.statusCode() + ": " + response.body());
        }

        try {
            return JsonParser.parseString(response.body());
        } catch (JsonParseException e) {
            throw new java.io.IOException("La respuesta de la API no es JSON válido", e);
        }
    }
}

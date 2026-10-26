package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * @author pablo
 */
public class ConexAPI {

    private String runID;

    public String getRunID() {
        return this.runID;
    }

    public void setRunId(String runID) {
        this.runID = runID;
    }

    public String getChallenges() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest discovery = HttpRequest.newBuilder()
                .uri(URI.create(api + "/challenges"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> found = http.send(discovery, HttpResponse.BodyHandlers.ofString());
        return found.body();
    }
    
    public String getStatus() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest statusRequest = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + this.runID))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> found = http.send(statusRequest, HttpResponse.BodyHandlers.ofString());
        String statusJson = found.body();
        
        JsonObject json = JsonParser.parseString(statusJson).getAsJsonObject();

        System.out.println(json.getAsJsonObject("control").getAsString());
        return json.getAsJsonObject("control").get("runStatus").getAsString();
    }

    public String startChallenge() throws java.io.IOException, InterruptedException {
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
        String startJson = started.body();

        // Extraer y guardar el runId automáticamente
        JsonObject json = JsonParser.parseString(startJson).getAsJsonObject();
        
        JsonObject control = json.getAsJsonObject("control");
        
        if(control != null){
            this.runID = json.getAsJsonObject("control").get("runId").getAsString();
            System.out.println("Se ha iniciado una nueva run: " + this.runID);
        }
        else{
            System.out.println("Ha ocurrido un error al intentar iniciar la run: " + json.getAsString());
        }

        return startJson;
    }

    public double getTemperature() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");
        String sensorId = "thermostat.temperature";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + this.runID + "/perceptions/" + sensorId))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        JsonElement json = JsonParser.parseString(response.body());
        if (!json.isJsonObject()) {
            throw new java.io.IOException("La respuesta del sensor no es un objeto JSON");
        }

        JsonObject root = json.getAsJsonObject();
        JsonElement value = root.get("value");
        if (value == null || !value.isJsonObject()) {
            throw new java.io.IOException("La respuesta del sensor no contiene un objeto 'value'");
        }

        JsonElement celsius = value.getAsJsonObject().get("celsius");
        if (celsius == null || !celsius.isJsonPrimitive() || !celsius.getAsJsonPrimitive().isNumber()) {
            throw new java.io.IOException("La respuesta no contiene el campo 'value.celsius' numérico");
        }

        return celsius.getAsDouble();
    }
    
    public String getControl() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + this.runID))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Parseamos el JSON para extraer y devolver únicamente la parte de "control"
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        return json.getAsJsonObject("control").toString();
    }

    public double getMaximumLimit() throws java.io.IOException, InterruptedException {
        return getLimit("upper");
    }

    public double getMinimumLimit() throws java.io.IOException, InterruptedException {
        return getLimit("lower");
    }

    private double getLimit(String limite) throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");
        String sensorId = "thermostat.limits";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + this.runID + "/perceptions/" + sensorId))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement json;
        try {
            json = JsonParser.parseString(response.body());
        } catch (com.google.gson.JsonParseException e) {
            throw new java.io.IOException("La respuesta del sensor no es JSON válido", e);
        }

        if (!json.isJsonObject()) {
            throw new java.io.IOException("La respuesta del sensor no es un objeto JSON");
        }
        JsonObject root = json.getAsJsonObject();
        JsonElement value = root.get("value");
        if (value == null || !value.isJsonObject()) {
            throw new java.io.IOException("La respuesta del sensor no contiene un objeto value");
        }
        JsonElement bound = value.getAsJsonObject().get(limite);
        if (bound == null || !bound.isJsonPrimitive() || !bound.getAsJsonPrimitive().isNumber()) {
            throw new java.io.IOException("La respuesta no contiene value." + limite + " numérico");
        }
        return bound.getAsDouble();
    }

    public double getObjectiveTemperature() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");
        String sensorId = "thermostat.target";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + this.runID + "/perceptions/" + sensorId))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        return json.getAsJsonObject("value").get("celsius").getAsDouble();
    }

    public String activateRefrigeration() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");
        String actionCode = "thermostat.refrigeration.on";
        String body = "{\"actionCode\":\"" + actionCode + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + this.runID + "/actions"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        return json.get("actionStatus").getAsString();
    }

    public String disableRefrigeration() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");
        String actionCode = "thermostat.refrigeration.off";
        String body = "{\"actionCode\":\"" + actionCode + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + this.runID + "/actions"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        return json.get("actionStatus").getAsString();
    }
}
package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConexAPI {
    //                               |
    //                               V
    // --------------------> Revisar GetChallenges <------------- //
    //                               ^
    //                               |
                                               
    public getChallenges() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest discovery = HttpRequest.newBuilder()
                .uri(URI.create(api + "/challenges"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> found = http.send(discovery, HttpResponse.BodyHandlers.ofString());

        // Comprobar que la respuesta HTTP es válida
        validateResponse(found);
        
        /* Comprobar esta parte del código
        ResponseLAB control = new ResponseLAB();
        if (json.isJsonObject()) {
            //return new Pair<>(control, control.update(json.toString()));
        }
            return new Pair<>(control, json);
        */
    }

    public ResponseLAB getStatus() throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest statusRequest = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + this.runID))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> response = http.send(statusRequest, HttpResponse.BodyHandlers.ofString());
        
        try{
            validateResponse(response);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            
            return;
        }
        
        return new ResponseLAB(response);
    }

    public ResponseLAB startChallenge(int challengeId) throws java.io.IOException, InterruptedException {
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
        HttpResponse<String> APIresponse = http.send(start, HttpResponse.BodyHandlers.ofString());

        validateResponse(APIresponse);
        
        try{
            validateResponse(APIresponse);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            
            return;
        }
        
        if (response.runStatus == null) {
            throw new java.io.IOException("La respuesta de inicio no contiene un control válido");
            return;
        }

        this.runID = String.valueOf(result.getFirst().runID);
        System.out.println("Se ha iniciado una nueva run: " + this.runID);
        return response;
    }

    public ResponseLAB getSensor(String sensorId) throws java.io.IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + this.runID + "/perceptions/" + sensorId))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        try{
            validateResponse(response);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            
            return;
        }
        
        return new ResponseLAB(response);
    }

    public ResponseLAB executeAction(String actionCode) throws java.io.IOException, InterruptedException {
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

        try{
            validateResponse(response);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            
            return;
        }
        
        return new ResponseLAB(response);
    }


    public ResponseAPI closeChallenge() throws java.io.IOException, InterruptedException {
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
        
        try{
            validateResponse(response);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            
            return;
        }
        
        System.out.println("[DBA LAB] Solicitud de cierre aceptada: " + this.runID);
        return new ResponseAPI(response);
    }

    private void validateResponse(HttpResponse<String> response) throws java.io.IOException {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new java.io.IOException("Error en la API. HTTP " + response.statusCode() + ": " + response.body());
        }
        
        return;
    }
}

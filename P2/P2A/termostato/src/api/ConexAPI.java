package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import common.Pair;

public class ConexAPI {
    public ArrayList<Pair<Integer, String>> getChallenges(){
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest discovery = HttpRequest.newBuilder()
                .uri(URI.create(api + "/challenges"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        
        try{
            HttpResponse<String> responseHTTP = http.send(discovery, HttpResponse.BodyHandlers.ofString());
            
            // Comprobar que la respuesta HTTP es válida
            try{
                validateResponse(responseHTTP);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                return null;
            }

            String responseString = responseHTTP.body();
            JsonObject json = JsonParser.parseString(responseString).getAsJsonObject();
            JsonArray challengesArray = json.getAsJsonArray("challenges");

            ArrayList<Pair<Integer, String>> listaParejas = new ArrayList<>();

            for (JsonElement elem : challengesArray) {
                JsonObject obj = elem.getAsJsonObject();
                int id = obj.get("id").getAsInt();
                String name = obj.get("name").getAsString();

                listaParejas.add(new Pair<>(id, name));
            }

            return listaParejas;
        }
        catch (Exception e){
            System.out.println("Error al intentar obtener los challenges de la API.");
            return null;
        }
    }

    public ResponseLAB getStatus(int runID){
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest statusRequest = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + String.valueOf(runID)))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        
        try{
            HttpResponse<String> responseHTTP = http.send(statusRequest, HttpResponse.BodyHandlers.ofString());
        
            try{
                validateResponse(responseHTTP);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                return null;
            }

            String responseString = responseHTTP.body();

            return new ResponseLAB(responseString);
        }
        catch (Exception e){
            System.out.println("Error al intentar obtener el estado de la API.");
            return null;
        }
    }

    public ResponseLAB startChallenge(int challengeId){
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
        
        try{
            HttpResponse<String> responseHTTP = http.send(start, HttpResponse.BodyHandlers.ofString());
            
            try{
                validateResponse(responseHTTP);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                return null;
            }

            String responseString = responseHTTP.body();

            System.out.println("Se ha iniciado una nueva run");

            return new ResponseLAB(responseString);
        }
        catch (Exception e){
            System.out.println("Error al conectarse a la API para iniciar el reto " + challengeId + ".");
            return null;
        }
    }

    public ResponseLAB getSensor(int runID, String sensorId){
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + String.valueOf(runID) + "/perceptions/" + sensorId))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        
        try{
            HttpResponse<String> responseHTTP = http.send(request, HttpResponse.BodyHandlers.ofString());
            
            try{
                validateResponse(responseHTTP);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                return null;
            }

            String responseString = responseHTTP.body();

            return new ResponseLAB(responseString);
        }
        catch (Exception e){
            System.out.println("Error al intentar obtener sensores de la API.");
            return null;
        }
    }

    public ResponseLAB executeAction(int runID, String actionCode){
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        JsonObject body = new JsonObject();
        body.addProperty("actionCode", actionCode);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + String.valueOf(runID) + "/actions"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        
        try{
            HttpResponse<String> responseHTTP = http.send(request, HttpResponse.BodyHandlers.ofString());
            try{
                validateResponse(responseHTTP);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                return null;
            }

            String responseString = responseHTTP.body();

            return new ResponseLAB(responseString);
        }
        catch (Exception e){
            System.out.println("Error al intentar ejecutar una acción en la API.");
            return null;
        }
    }


    public ResponseLAB closeChallenge(int runID){
        HttpClient http = HttpClient.newHttpClient();
        String api = System.getenv("DBA_LAB_URL") + "/api/v1";
        String token = System.getenv("DBA_TOKEN");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + "/runs/" + String.valueOf(runID)+ "/close"))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        
        try{
            HttpResponse<String> responseHTTP = http.send(request, HttpResponse.BodyHandlers.ofString());
            
            try{
                validateResponse(responseHTTP);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                return null;
            }

            String responseString = responseHTTP.body();

            System.out.println("Solicitud de cierre aceptada por la API. Id de la run " + String.valueOf(runID));
            return new ResponseLAB(responseString);
        }
        catch (Exception e){
            System.out.println("Error al conectarse a la API para cerrar el reto.");
            return null;
        }
    }

    private void validateResponse(HttpResponse<String> response) throws java.io.IOException {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new java.io.IOException("Error en la API. HTTP " + response.statusCode() + ": " + response.body());
        }        
    }
}

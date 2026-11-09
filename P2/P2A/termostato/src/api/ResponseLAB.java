package api;
import common.RunStatus;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import common.Pair;

// Importante cargar datos nuevos al hacer una acción.
public class ResponseLAB {
    // Respuesta de DBA_LAB a sensores
    public String sensorId;
    public ArrayList<Pair<String, String>> value;
    
    // Respuesta de DBA_LAB a acciones
    public int actionId;
    public String actionStatus;
    public String reasonCodeAction;
    
    // Respuesta de control de DBA_LAB
    public int runId;
    public RunStatus runStatus;
    public Boolean isAlive;
    public String objectivePhase;
    public int remainingObjectives;
    public String reasonCode;
    
    public ResponseLAB(){
        // Sensores
        this.sensorId = "";
        this.value = new ArrayList<>();
        
        // Acciones
        this.actionId = -1;
        this.actionStatus = "";
        this.reasonCodeAction = "";
        
        // Control
        this.runId = -1;
        this.runStatus = null;
        this.isAlive = false;
        this.objectivePhase = "";
        this.remainingObjectives = -1;
        this.reasonCode = "";
    }
     
    public ResponseLAB(String response){
        this();
        update(response);
    }
            
            
    // Actualiza el control y devuelve el JSON con los datos particulares del problema.
    public void update(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isBlank()) {
            return;
        }

        try {
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonObject controlNode;
            
            // SECCIÓN DE CONTROL (raíz o dentro de "control")
            if (json.has("control") && !json.get("control").isJsonNull()) {
                controlNode = json.getAsJsonObject("control");
            } else {
                controlNode = json;
            }
            
            if (controlNode.has("runId") && !controlNode.get("runId").isJsonNull()) {
                this.runId = controlNode.get("runId").getAsInt();
            }

            if (controlNode.has("runStatus") && !controlNode.get("runStatus").isJsonNull()) {
                this.runStatus = RunStatus.valueOf(controlNode.get("runStatus").getAsString());
            }

            if (controlNode.has("isAlive") && !controlNode.get("isAlive").isJsonNull()) {
                this.isAlive = controlNode.get("isAlive").getAsBoolean();
            }

            if (controlNode.has("objectivePhase") && !controlNode.get("objectivePhase").isJsonNull()) {
                this.objectivePhase = controlNode.get("objectivePhase").getAsString();
            } else {
                this.objectivePhase = null;
            }

            if (controlNode.has("remainingObjectives") && !controlNode.get("remainingObjectives").isJsonNull()) {
                this.remainingObjectives = controlNode.get("remainingObjectives").getAsInt();
            }

            if (controlNode.has("reasonCode") && !controlNode.get("reasonCode").isJsonNull()) {
                this.reasonCode = controlNode.get("reasonCode").getAsString();
            } else {
                this.reasonCode = null;
            }
            
            // SECCIÓN DE SENSORES ("sensorId" y objeto "value")
            if (json.has("sensorId") && !json.get("sensorId").isJsonNull()) {
                this.sensorId = json.get("sensorId").getAsString();
            }
            
            if (json.has("value") && !json.get("value").isJsonNull()) {
                controlNode = json.getAsJsonObject("value");
                this.value.clear();
                String valStr = "";
                    for (String key : controlNode.keySet()) {
                        if(controlNode.get(key).isJsonNull()){
                            valStr = null;
                        } else{
                            valStr = controlNode.get(key).getAsString();
                        }
                        this.value.add(new Pair<>(key, valStr));
                    }
            }
            
            // SECCIÓN DE ACCIONES ("actionId", "actionStatus", "reasonCodeAction")
            if (json.has("actionId") && !json.get("actionId").isJsonNull()) {
                this.actionId = json.get("actionId").getAsInt();
            }
            
            if (json.has("actionStatus") && !json.get("actionStatus").isJsonNull()) {
                this.actionStatus = json.get("actionStatus").getAsString();
            }
            
            if (json.has("reasonCode") && !json.get("reasonCode").isJsonNull()) {
                this.reasonCodeAction = json.get("reasonCode").getAsString();
            }
        } catch (Exception e) {
            System.err.println("Error al parsear el JSON de Control: " + e.getMessage());
            return;
        }
    }
}

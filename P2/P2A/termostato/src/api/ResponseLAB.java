package api;
import common.RunStatus;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import common.Pair;

// Importante cargar datos nuevos al hacer una acción.
public class ResponseLAB {
    // Respuesta de DBA_LAB a sensores
    public int sensorID;
    public ArrayList<Pair<String>, <String>> value;
    
    // Respuesta de DBA_LAB a acciones
    public int actionID;
    public String actionStatus;
    public String reasonCodeAction;
    
    // Respuesta de control de DBA_LAB
    public int runID;
    public RunStatus runStatus;
    public Boolean isAlive;
    public String objectivePhase;
    public int remainingObjectives;
    public String reasonCode;
    
    public ResponseLAB(){
        this.sensorID = -1;
        this.value = new ArrayList<>();
        
        this.actionID = -1;
    }
    
    // Actualiza el control y devuelve el JSON con los datos particulares del problema.
    public JsonObject update(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isBlank()) {
            return new JsonObject();
        }

        try {
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();

            JsonObject datos = json.deepCopy();
            JsonObject controlNode;

            if (json.has("control")) {
                datos.remove("control");
                if (json.get("control").isJsonNull()) {
                    return datos;
                }
                controlNode = json.getAsJsonObject("control");
            } else if (json.has("runId") || json.has("runStatus")) {
                // También se admite recibir directamente el objeto de control.
                controlNode = json;
                datos.remove("runId");
                datos.remove("runStatus");
                datos.remove("isAlive");
                datos.remove("objectivePhase");
                datos.remove("remainingObjectives");
                datos.remove("reasonCode");
            } else {
                // Una respuesta sin control conserva todos sus datos.
                return datos;
            }

            // Mapeo seguro de campos comprobando su existencia
            if (controlNode.has("runId") && !controlNode.get("runId").isJsonNull()) {
                this.runID = controlNode.get("runId").getAsInt();
            }

            if (controlNode.has("runStatus") && !controlNode.get("runStatus").isJsonNull()) {
                String statusStr = controlNode.get("runStatus").getAsString();
                try {
                    this.runStatus = RunStatus.valueOf(statusStr); // Convierte el String al Enum exacto
                } catch (IllegalArgumentException e) {
                    this.runStatus = RunStatus.UNINITALIZED; // Fallback por seguridad
                }
            }

            if (controlNode.has("isAlive") && !controlNode.get("isAlive").isJsonNull()) {
                this.isAlive = controlNode.get("isAlive").getAsBoolean();
            }

            if (controlNode.has("objectivePhase") && !controlNode.get("objectivePhase").isJsonNull()) {
                this.objectivePhase = controlNode.get("objectivePhase").getAsString();
            }

            if (controlNode.has("remainingObjectives") && !controlNode.get("remainingObjectives").isJsonNull()) {
                this.remainingObjectives = controlNode.get("remainingObjectives").getAsInt();
            }

            if (controlNode.has("reasonCode") && !controlNode.get("reasonCode").isJsonNull()) {
                this.reasonCode = controlNode.get("reasonCode").getAsString();
            }

            return datos;
        } catch (Exception e) {
            System.err.println("Error al parsear el JSON de Control: " + e.getMessage());
            return new JsonObject();
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;
import common.RunStatus;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * @author yeray
 */

//Importante cargar datos nuevos al hacer una accionó
public class Control {
    public int runID;
    public RunStatus runStatus;
    public Boolean isAlive;
    public String objectivePhase;
    public int remainingObjectives;
    public String reasonCode;
    
    public void update(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isBlank()) {
            return;
        }

        try {
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
            
            JsonObject controlNode = json.has("control") ? json.getAsJsonObject("control") : json;

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
            
        } catch (Exception e) {
            System.err.println("Error al parsear el JSON de Control: " + e.getMessage());
        }
    }
}

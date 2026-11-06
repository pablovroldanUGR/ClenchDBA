package termostato;

import jade.core.Agent;
import jade.core.behaviours.*;
import api.ConexAPI;
import common.Pair;
import com.google.gson.JsonObject;

public class BHThermo extends Behaviour {
    private ConexAPI api;
    private String status = "";
    private int exitCode = 0;
    
    @Override
    public void action() {

        try {
            api = ((AgentTemp) myAgent).getApi();
            
            // Comprobamos estado de la run
            this.status = api.getStatus().control.runStatus.name();
            
            if (status.equals("ACTIVE")) {
                // Obtenemos temperatura
                Pair<JsonObject> temperature = api.getTemperature();
                this.status = temperature.control.runStatus.name();
                if (!status.equals("ACTIVE")) {
                    return;
                }
                double temp = temperature.particular.getAsJsonObject("value").get("celsius").getAsDouble();
                
                ((AgentTemp) myAgent).printMessage("Temperatura obtenida -> " + temp);
                
                // Decidimos acción
                if(temp >= ((AgentTemp) myAgent).getTarget()){
                    Pair<JsonObject> action = api.executeAction("thermostat.refrigeration.on");
                    this.status = action.control.runStatus.name();
                    ((AgentTemp) myAgent).printMessage("CALIENTE: ACTIVAMOS REFRIGERACION");

                    
                } else {
                    Pair<JsonObject> action = api.executeAction("thermostat.refrigeration.off");
                    this.status = action.control.runStatus.name();
                    ((AgentTemp) myAgent).printMessage("FRIO: DESACTIVAMOS REFRIGERACION");
                    
                }
                
            }
            if (status.equals("SUCCEEDED")) {
                // Reto completado
                ((AgentTemp) myAgent).printMessage("RETO COMPLETADO");
                this.exitCode = 1;
            }
        } catch (Exception e) {
            System.err.println("Error al comunicarse con la API durante el setup: " + e.getMessage());
            e.printStackTrace();
            myAgent.doDelete();
        }
    }

    @Override
    public boolean done() {
        // Si no esta activa la run, terminamos
        return !this.status.equals("ACTIVE");
    }
    
    @Override
    public int onEnd(){
        myAgent.doDelete();
        return this.exitCode;
    }
}

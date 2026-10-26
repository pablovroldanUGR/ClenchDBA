package termostato;

import jade.core.Agent;
import jade.core.behaviours.*;
import api.ConexAPI;

public class BHThermo extends Behaviour {
    private ConexAPI api;
    private String status = "";
    private int exitCode = 0;
    
    @Override
    public void action() {

        try {
            api = ((AgentTemp) myAgent).getApi();
            
            // Comprobamos estado de la run
            this.status = api.getStatus();
                
            // Obtenemos temperatura
            double temp = api.getTemperature();
            System.out.println("ESTADO: " + status);
            
            if (status == "ACTIVE") {
                ((AgentTemp) myAgent).printMessage("Temperatura obtenida -> " + temp);
                
                // Decidimos acción
                if(temp >= ((AgentTemp) myAgent).getTarget()){
                    api.activateRefrigeration(); // CALIENTE: ACTIVAMOS REFRIGERACION
                    ((AgentTemp) myAgent).printMessage("CALIENTE: ACTIVAMOS REFRIGERACION");

                    
                } else {
                    api.disableRefrigeration(); // FRIO: DESACTIVAMOS REFRIGERACION
                    ((AgentTemp) myAgent).printMessage("FRIO: DESACTIVAMOS REFRIGERACION");
                    
                }
                
            } else if (status == "SUCCEEDED") {
                // Reto completado
                ((AgentTemp) myAgent).printMessage("RETO COMPLETADO. TEMPERATURA ALCANZADA -> " + temp);
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
        return this.status != "ACTIVE";
    }
    
    @Override
    public int onEnd(){
        myAgent.doDelete();
        return this.exitCode;
    }
}

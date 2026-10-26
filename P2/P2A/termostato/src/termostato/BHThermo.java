package termostato;

import jade.core.Agent;
import jade.core.behaviours.*;
import api.ConexAPI;

public class BHThermo extends Behaviour {
    
    private ConexAPI api = ((AgentTemp) myAgent).getApi();
    private String status = "";
    private int exitCode = 0;
    
    @Override
    public void action() {

        try {
            // Comprobamos estado de la run
            this.status = api.getStatus();

            if (status == "ACTIVE") {
                // Obtenemos temperatura
                double temp = api.getTemperature();
                
                // Decidimos acción
                if(temp >= ((AgentTemp) myAgent).getTarget()){
                    api.activateRefrigeration(); // CALIENTE: ACTIVAMOS REFRIGERACION
                } else {
                    api.disableRefrigeration(); // FRIO: DESACTIVAMOS REFRIGERACION
                }
                
            } else if (status == "SUCCEEDED") {
                // Reto completado
                ((AgentTemp) myAgent).printMessage("COMPLETADO");
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

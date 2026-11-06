package termostato;

import common.RunStatus;
import entorno.EnvironmentThermo;
import jade.core.Agent;
import jade.core.behaviours.*;

public class BHThermo extends Behaviour {
    private String status = "";
    private int exitCode = 0;
    private EnvironmentThermo thermo = ((AgentTemp) myAgent).getEnvironment();
    
    @Override
    public void action() {

        try {    
            thermo.checkStatus();
            
            if (thermo.runStatus == RunStatus.ACTIVE) {
                // Obtenemos temperatura
                double temp = thermo.getTemperature();
                
                ((AgentTemp) myAgent).printMessage("Temperatura obtenida -> " + temp);
                
                // Decidimos acción
                if(temp >= thermo.getObjectiveTemperature()){
                    thermo.activateRefrigeration(); // CALIENTE: ACTIVAMOS REFRIGERACION
                    ((AgentTemp) myAgent).printMessage("CALIENTE: ACTIVAMOS REFRIGERACION");

                    
                } else {
                    thermo.disableRefrigeration(); // FRIO: DESACTIVAMOS REFRIGERACION
                    ((AgentTemp) myAgent).printMessage("FRIO: DESACTIVAMOS REFRIGERACION");
                    
                }
                
            } else if (thermo.runStatus == RunStatus.SUCCEEDED) {
                // Reto completado
                ((AgentTemp) myAgent).printMessage("RETO COMPLETADO");
                this.exitCode = 1;
            }
        } catch (Exception e) {
            System.err.println("Error al comunicarse con la thermo durante el setup: " + e.getMessage());
            e.printStackTrace();
            myAgent.doDelete();
        }
    }

    @Override
    public boolean done() {
        // Si no esta activa la run, terminamos
        return !(thermo.runStatus == RunStatus.ACTIVE);
    }
    
    @Override
    public int onEnd(){
        myAgent.doDelete();
        return this.exitCode;
    }
}

package termostato;

import common.RunStatus;
import entorno.EnvironmentThermo;
import jade.core.behaviours.*;

public class BHThermo extends Behaviour {
    private int exitCode = 0;
    private EnvironmentThermo thermo;
    
    @Override
    public void action() {
        thermo = ((AgentTemp) myAgent).getEnvironment();

            try{
                thermo.checkStatus(); // Tira excepción
                
                if (thermo.runStatus == RunStatus.ACTIVE) {
                    // Obtenemos temperatura
                    double temp = thermo.getTemperature();

                    ((AgentTemp) myAgent).printMessage("Temperatura obtenida -> " + temp);

                    // Decidimos acción
                    if(temp >= thermo.getTargetTemperature()){
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
            }
            catch (Exception e){
                System.out.println("Error por parte del agente al solicitar estado al entorno. " + e.getMessage());
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

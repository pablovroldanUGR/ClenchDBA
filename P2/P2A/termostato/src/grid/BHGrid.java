/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package grid;

import jade.core.behaviours.Behaviour;
import common.RunStatus;
import entorno.EnvironmentGrid;
import jade.core.behaviours.*;
import common.Direction;

/**
 *
 * @author neocofias1258
 */
public class BHGrid extends Behaviour {
    
    private int exitCode = 0;
    private EnvironmentGrid grid;
    private Direction direction = null;
    
    @Override
    public void action() {
        grid = ((AgentGrid) myAgent).getEnvironment();
        direction = ((AgentGrid) myAgent).getDirection();
        

            try{
                grid.checkStatus(); // Tira excepción
                
                if (grid.runStatus == RunStatus.ACTIVE) {
                    // Obtenemos temperatura
                    double temp = grid.getTemperature();

                    ((AgentGrid) myAgent).printMessage("Temperatura obtenida -> " + temp);

                    // Decidimos acción
                    if(temp >= grid.getTargetTemperature()){
                        grid.activateRefrigeration(); // CALIENTE: ACTIVAMOS REFRIGERACION
                        ((AgentGrid) myAgent).printMessage("CALIENTE: ACTIVAMOS REFRIGERACION");


                    } else {
                        grid.disableRefrigeration(); // FRIO: DESACTIVAMOS REFRIGERACION
                        ((AgentGrid) myAgent).printMessage("FRIO: DESACTIVAMOS REFRIGERACION");

                    }

                } else if (grid.runStatus == RunStatus.SUCCEEDED) {
                    // Reto completado
                    ((AgentGrid) myAgent).printMessage("RETO COMPLETADO");
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

package termostato;

import entorno.Environment;
import entorno.EnvironmentThermo;
import entorno.Main;
import jade.core.Agent;

public class AgentTemp extends Agent {
    // Para imprimir mensajes
    private String name = "[THERMOSTAT]: ";
    
    // Conexion al entorno
    private EnvironmentThermo thermo;
    
    @Override
    protected void setup() {
        this.thermo = (EnvironmentThermo) Environment.getInstance("Thermo");

        printMessage("Agente Thermo Iniciado");
        addBehaviour(new BHThermo());
    }

    public void printMessage(String msg) {
        Main.printGUI(name + msg);
    }
    
    public EnvironmentThermo getEnvironment(){
        return this.thermo;
    }

    @Override
    protected void takeDown(){
        printMessage("--->Agente finalizado<---");
        try{
            thermo.closeRun();
            printMessage("Run terminada por el agente.");
        }
        catch (Exception e){
            printMessage("Error al terminar la run por parte del agente. Respuesta del entorno: " + e.getMessage());
        }
    }
}

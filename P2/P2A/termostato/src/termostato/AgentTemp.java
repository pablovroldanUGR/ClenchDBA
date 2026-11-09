package termostato;

import entorno.Environment;
import entorno.EnvironmentThermo;
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
        System.out.println(name + msg);
    }
    
    public EnvironmentThermo getEnvironment(){
        return this.thermo;
    }

    @Override
    protected void takeDown(){
        try{
            thermo.closeRun();
            System.out.println("Run terminada");
        }
        catch (Exception e){
            System.out.println("Error al cerrar la conexión con la API. " + e.getMessage());
        }
        System.exit(0);
    }
}

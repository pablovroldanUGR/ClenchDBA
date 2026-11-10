/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package grid;


import entorno.Environment;
import entorno.EnvironmentGrid;
import jade.core.Agent;
import common.Direction;

/**
 *
 * @author neocofias1258
 */
public class AgentGrid extends Agent{
        // Para imprimir mensajes
    private String name = "[GRID]: ";
    private Direction direction = null;
    
    // Conexion al entorno
    private EnvironmentGrid grid;
    
    @Override
    protected void setup() {
        this.grid = (EnvironmentGrid) Environment.getInstance("Grid");

        printMessage("Agente Grid Iniciado");
        addBehaviour(new BHGrid());
    }

    public void printMessage(String msg) {
        System.out.println(name + msg);
    }
    
    public EnvironmentGrid getEnvironment(){
        return this.grid;
    }
    
    public Direction getDirection(){
        return this.direction;
    }

    @Override
    protected void takeDown(){
        try{
            grid.closeRun();
            System.out.println("Run terminada");
        }
        catch (Exception e){
            System.out.println("Error al cerrar la conexión con la API. " + e.getMessage());
        }
        System.exit(0);
    }
}

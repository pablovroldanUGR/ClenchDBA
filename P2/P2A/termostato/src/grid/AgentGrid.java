/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package grid;


import entorno.Environment;
import entorno.EnvironmentGrid;
import jade.core.Agent;
import common.Direction;
import common.Pair;
import entorno.Main;

/**
 *
 * @author neocofias1258
 */
public class AgentGrid extends Agent{
        // Para imprimir mensajes
    private String name = "[GRID]: ";
    private Direction direction = null;
    public Pair<Integer, Integer> position = new Pair();
    
    // Conexion al entorno
    private EnvironmentGrid grid;
    
    @Override
    protected void setup() {
        System.out.println("[GRID][AgentGrid.setup] Obteniendo entorno Grid");
        this.grid = (EnvironmentGrid) Environment.getInstance("Grid");
        System.out.println("[GRID][AgentGrid.setup] Entorno obtenido");

        printMessage("Agente Grid Iniciado");
        System.out.println("[GRID][AgentGrid.setup] Añadiendo comportamiento BHGrid");
        addBehaviour(new BHGrid());
        System.out.println("[GRID][AgentGrid.setup] Comportamiento añadido; setup terminado");
    }

    public void printMessage(String msg) {
        System.out.println("[GRID][AgentGrid.printMessage] " + msg);
        Main.printGUI(name + msg);
        System.out.println("[GRID][AgentGrid.printMessage] Mensaje enviado a la GUI");
    }
    
    public EnvironmentGrid getEnvironment(){
        System.out.println("[GRID][AgentGrid.getEnvironment] Devolviendo entorno");
        return this.grid;
    }
    
    public Direction getDirection(){
        System.out.println("[GRID][AgentGrid.getDirection] Dirección: " + this.direction);
        return this.direction;
    }

    @Override
    protected void takeDown(){
        System.out.println("[GRID][AgentGrid.takeDown] Iniciando cierre del agente");
        try{
            System.out.println("[GRID][AgentGrid.takeDown] Llamando a closeRun");
            grid.closeRun();
            System.out.println("[GRID][AgentGrid.takeDown] closeRun terminado");
            Main.printGUI("Run terminada");
        }
        catch (Exception e){
            System.out.println("[GRID][AgentGrid.takeDown] Excepción: " + e);
            Main.printGUI("Error al cerrar la conexión con la API. " + e.getMessage());
        }
    }
}

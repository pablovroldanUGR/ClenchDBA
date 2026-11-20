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
import common.RunStatus;
import entorno.Main;

/**
 *
 * @author neocofias1258
 */
public class AgentGrid extends Agent{
        // Para imprimir mensajes
    private String name = "[GRID][AGENTE] ";
    private Direction direction = null;
    public Pair<Integer, Integer> position = new Pair();
    public int energy = -1;
    
    // Conexion al entorno
    private EnvironmentGrid grid;
    
    @Override
    protected void setup() {
        this.grid = (EnvironmentGrid) Environment.getInstance("Grid");
        
        energy = grid.getEnergyLeft();
        
        printMessage("Agente Grid Iniciado");
        addBehaviour(new BHGrid());
    }

    public void printMessage(String msg) {
        Main.printGUI(name + msg);
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
            if (grid.runStatus == RunStatus.ACTIVE) {
                grid.closeRun();
            }
            Main.printGUI("[GRID][AGENTE] Finalizado.");
        }
        catch (Exception e){
            Main.printGUI("[GRID][ERROR] No se pudo cerrar la run: " + e.getMessage());
        }
    }
}

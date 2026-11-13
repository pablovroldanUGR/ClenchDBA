package entorno;

import UI.GUI;
import common.CellType;
import common.RunStatus;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import termostato.AgentTemp;
import java.util.ArrayList;
import grid.*;

//--- LIBRERÍAS PARA PRUEBAS DE API ---//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;

public class Main {
    public static final int MAP_COL = 13;
    public static final int MAP_ROWS = 13;
    
    private static Environment environment;
    private static GUI gui;
        
    public static RunStatus checkRunStatus(){
        try{
            return environment.checkStatus();
        }
        catch (Exception e){
            printGUI("Error en el main al intentar leer el estado del entorno. " + e.getMessage());
        }
        
        return RunStatus.UNINITALIZED;
    }
    
    public static RunStatus getRunStatus(){
        if(environment != null){
            return environment.getStatus();
        }
        
        return RunStatus.UNINITALIZED;
    }
    
    public static void printGUI(String text){
        if(gui != null){
            gui.printText(text);
        }
    }
    
    public static void startChallenge(int challengeID){
        String name = challengeID==1? "termostato" : "grid";
        
        Runtime rt = Runtime.instance();
        
        // Perfil del contenedor
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.CONTAINER_NAME, name);
        
        AgentContainer agentContainer = rt.createAgentContainer(profile);
        
        try{
            if(challengeID == 1){
                environment = Environment.getInstance("Thermo");  
                environment.setChallenge(challengeID);
                environment.startChallenge();
                checkRunStatus();
                
                AgentController thermostatAgent = agentContainer.createNewAgent
                ("Thermostat", AgentTemp.class.getName(), new Object[]{});

                thermostatAgent.start();                
            }
            else{
                environment = Environment.getInstance("Grid"); 
                environment.setChallenge(challengeID);
                environment.startChallenge();
                checkRunStatus();
                
                AgentController gridAgent = agentContainer.createNewAgent
                ("Grid", AgentGrid.class.getName(), new Object[]{});

                gridAgent.start();
                System.out.println("Agente Grid lanzado");
                
            }
        } 
        catch (Exception e){
            printGUI("Error al inicializar la plataforma JADE:");
            e.printStackTrace();
        }
    }
    
    public static void closeRun(){       
        try{
            environment.closeRun();
            printGUI("Cerrando la run manualmente...");
        }
        catch (Exception e){
            printGUI("Error al intentar cerrar la run manualmente. " + e.getMessage());
        }
    }
    
    private static ArrayList<ArrayList<CellType>> createTestMap(){
        int rows = 13;
        int columns = 13;
        ArrayList<ArrayList<CellType>> map = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            ArrayList<CellType> cells = new ArrayList<>();
            for (int column = 0; column < columns; column++) {
                cells.add(CellType.UNKNOWN);
            }
            map.add(cells);
        }

        for (int column = 3; column <= 9; column++) {
            map.get(6).set(column, CellType.FREE);
        }
        for (int row = 3; row <= 6; row++) {
            map.get(row).set(6, CellType.FREE);
        }
        map.get(5).set(7, CellType.WALL);
        map.get(3).set(6, CellType.GOAL);
        map.get(6).set(6, CellType.AGENT);

        return map;
    }
    
    public static void main(String args[]){      
        gui = new GUI();
        gui.updateMap(createTestMap());
        gui.setVisible(true);
    }
}
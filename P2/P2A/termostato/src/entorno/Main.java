package entorno;

import UI.GUI;
import common.CellType;
import common.Pair;
import common.RunStatus;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import termostato.AgentTemp;
import java.util.ArrayList;
import grid.*;

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
    
    public static void updateMap(ArrayList<ArrayList<CellType>> map){
        if(map != null && !map.isEmpty()){
            Pair<Integer, Integer> agentPos = new Pair<>();
            boolean agentFound = false;
            
            for(int i = 0; i < map.size() && !agentFound; i++){
                for(int j = 0; j < map.get(0).size() && !agentFound; j++){
                    agentFound = map.get(i).get(j) == CellType.AGENT;
                    if(agentFound){
                        agentPos.setFirst(j); // x = columnas
                        agentPos.setSecond(i); // y = filas
                    }
                }
            }

            if(agentPos.getFirst() != null && agentPos.getSecond() != null){
                int minColMainMap = agentPos.getFirst() - Main.MAP_COL/2;
                int minRowMainMap = agentPos.getSecond() - Main.MAP_ROWS/2;
                int currentRowMainMap = minRowMainMap;
                int currentColMainMap = minColMainMap;
                
                ArrayList<ArrayList<CellType>> reducedMap = new ArrayList<>(Main.MAP_ROWS);

                for(int i = 0; i < MAP_ROWS; i++){
                    reducedMap.add(new ArrayList<>(Main.MAP_COL));
                    currentColMainMap = minColMainMap;

                    for(int j = 0; j < MAP_COL; j++){
                        if(currentRowMainMap < 0 || currentRowMainMap >= map.size() || currentColMainMap < 0 || currentColMainMap >= map.get(0).size()){
                            reducedMap.get(i).add(CellType.UNKNOWN);
                        }
                        else{
                            reducedMap.get(i).add(map.get(currentRowMainMap).get(currentColMainMap));
                        }

                        currentColMainMap ++;
                    }

                    currentRowMainMap ++;
                }

                gui.updateMap(reducedMap);
            }
        }
    }
    
    public void updateInfo(String info){
        gui.updateInfo(info);
    }
    
    private static ArrayList<ArrayList<CellType>> createTestMap(int rows, int cols) {
        ArrayList<ArrayList<CellType>> map = new ArrayList<>(rows);

        // 1. Inicializar mapa vacío con bordes de muros (WALL)
        for (int i = 0; i < rows; i++) {
            ArrayList<CellType> row = new ArrayList<>(cols);
            for (int j = 0; j < cols; j++) {
                if (i == 0 || i == rows - 1 || j == 0 || j == cols - 1) {
                    row.add(CellType.WALL);
                } else {
                    row.add(CellType.FREE);
                }
            }
            map.add(row);
        }

        // 2. Añadir obstáculos internos (si el mapa es suficientemente grande)
        if (rows > 8 && cols > 8) {
            map.get(2).set(5, CellType.WALL);
            map.get(3).set(5, CellType.WALL);
            map.get(4).set(5, CellType.WALL);
            map.get(6).set(3, CellType.WALL);
            map.get(6).set(4, CellType.WALL);
        }

        // 3. Colocar al agente (AGENT) y la meta (GOAL)
        map.get(3).set(3, CellType.AGENT);
        map.get(7).set(8, CellType.GOAL);

        return map;
    }
    
    public static void main(String args[]){      
        gui = new GUI();
        updateMap(createTestMap(10, 10));
        gui.updateInfo("Reto: 1 \nEnergía: 100 \nEstado: ACTIVO");
        gui.setVisible(true);
    }
}
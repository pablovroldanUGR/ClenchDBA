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
    public static final int MAP_COL = 32;
    public static final int MAP_ROWS = 32;
    
    private static Environment environment;
    private static GUI gui;
        
    public static RunStatus checkRunStatus(){
        try{
            return environment.checkStatus();
        }
        catch (Exception e){
            System.out.println("Error en el main al intentar leer el estado del entorno. " + e.getMessage());
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
                System.out.println("[GRID][Main.startChallenge] Obteniendo entorno para reto " + challengeID);
                environment = Environment.getInstance("Grid"); 
                System.out.println("[GRID][Main.startChallenge] Entorno obtenido; configurando reto");
                environment.setChallenge(challengeID);
                System.out.println("[GRID][Main.startChallenge] Iniciando reto en la API");
                environment.startChallenge();
                System.out.println("[GRID][Main.startChallenge] Inicio del reto terminado; consultando estado");
                checkRunStatus();
                System.out.println("[GRID][Main.startChallenge] Estado consultado; creando agente JADE");
                
                AgentController gridAgent = agentContainer.createNewAgent
                ("Grid", AgentGrid.class.getName(), new Object[]{});

                System.out.println("[GRID][Main.startChallenge] Agente creado; iniciando agente");
                gridAgent.start();
                System.out.println("[GRID][Main.startChallenge] Inicio del agente terminado");
                System.out.println("Agente Grid lanzado");
                
            }
        } 
        catch (Exception e){
            System.out.println("Error al inicializar la plataforma JADE:");
            e.printStackTrace();
        }
    }
    
    public static void closeRun(){       
        try{
            environment.closeRun();
            System.out.println("Cerrando la run manualmente...");
        }
        catch (Exception e){
            System.out.println("Error al intentar cerrar la run manualmente. " + e.getMessage());
        }
    }
    
    public static void updateMap(ArrayList<ArrayList<CellType>> map){
        System.out.println("[GRID][Main.updateMap] Inicio de actualización del mapa");
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
                System.out.println("[GRID][Main.updateMap] Agente encontrado en " + agentPos + "; preparando mapa visible");
                int minColMainMap = agentPos.getFirst() - Main.MAP_COL/2;
                int minRowMainMap = agentPos.getSecond() - Main.MAP_ROWS/2;
                int currentRowMainMap = minRowMainMap;
                int currentColMainMap = minColMainMap;
                
                ArrayList<ArrayList<CellType>> reducedMap = new ArrayList<>(Main.MAP_ROWS);

                for(int i = 0; i < MAP_ROWS; i++){
                    reducedMap.add(new ArrayList<>(Main.MAP_COL));
                    currentColMainMap = minColMainMap;

                    for(int j = 0; j < MAP_COL; j++){
                        // Booleano fuera de limites
                        boolean OutOfBounds = currentRowMainMap < 0 || currentRowMainMap >= map.size() || 
                                              currentColMainMap < 0 || currentColMainMap >= map.get(0).size();
                        
                        if(OutOfBounds){
                            reducedMap.get(i).add(CellType.UNKNOWN);
                        }
                        else{
                            // Pintar mapa del entorno
                            reducedMap.get(i).add(map.get(currentRowMainMap).get(currentColMainMap));
                        }

                        currentColMainMap ++;
                    }

                    currentRowMainMap ++;
                }

                System.out.println("[GRID][Main.updateMap] Enviando mapa visible a la GUI");
                gui.updateMap(reducedMap);
                System.out.println("[GRID][Main.updateMap] Mapa enviado a la GUI");
            }
        }
    }
    
    public static void updateInfo(String info){
        gui.updateInfo(info);
    }
    
    public static void main(String args[]){      
        gui = new GUI();
        gui.setVisible(true);
    }
}

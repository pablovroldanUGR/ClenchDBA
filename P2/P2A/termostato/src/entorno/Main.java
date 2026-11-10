package entorno;

import UI.GUI2;
import common.RunStatus;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import termostato.AgentTemp;

//--- LIBRERÍAS PARA PRUEBAS DE API ---//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;

public class Main {
    private static Environment environment;
    private static GUI2 gui;
        
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
            else{/*
                environment = Environment.getInstance("Grid"); 
                environment.setChallenge(challengeID);
                environment.startChallenge();
                checkRunStatus();
                
                AgentController gridAgent = agentContainer.createNewAgent
                ("Grid", AgentGrid.class.getName(), new Object[]{});

                gridAgent.start();
                System.out.println("Agente Grid lanzado");
                */
            }
        } 
        catch (Exception e){
            System.err.println("Error al inicializar la plataforma JADE:");
            e.printStackTrace();
        }
    }
    
    public static void closeRun(){
        try{
            environment.closeRun();
        }
        catch (Exception e){
            System.out.println("Error en el main al cerrar la run. " + e.getMessage());
        }
    }
    
    public static void main(String args[]){      
        gui = new GUI2();
        gui.setVisible(true);
    }
}
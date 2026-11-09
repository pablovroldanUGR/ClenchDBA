package entorno;

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
    public static void main(String args[]){
        
        Runtime rt = Runtime.instance();
        
        // Perfil del contenedor
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.CONTAINER_NAME, "termostato");
        
        try{
            AgentContainer agentContainer = rt.createAgentContainer(profile);
                                    
            AgentController thermostatAgent = agentContainer.createNewAgent
            ("Thermostat", AgentTemp.class.getName(), new Object[]{});
            

            thermostatAgent.start();
            System.out.println("Agente Termostato lanzado");
            
        }catch (Exception e) {
            System.err.println("Error al inicializar la plataforma JADE:");
            e.printStackTrace();
        }
    }
}
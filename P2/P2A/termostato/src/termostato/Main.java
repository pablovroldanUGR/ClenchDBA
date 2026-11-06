package termostato;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import api.ConexAPI;

//--- LIBRERÍAS PARA PRUEBAS DE API ---//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;

public class Main {
    public static void main(String args[]){
        
        // Comprobamos variables de entorno
        String url = System.getenv("DBA_LAB_URL");
        String token = System.getenv("DBA_TOKEN");
        String challengeId = System.getenv("DBA_CHALLENGE_ID");
        
        if (url == null || url.isBlank() || token == null || token.isBlank() || challengeId == null || challengeId.isBlank()) {
            System.err.println("Error: Debes definir las variables de entorno "
                    + "DBA_LAB_URL, DBA_TOKEN y DBA_CHALLENGE_ID.");
            System.exit(1);
        }
        
        Runtime rt = Runtime.instance();
        
        // Perfil del contenedor
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.CONTAINER_NAME, "termostato");
        
        try{
            AgentContainer agentContainer = rt.createAgentContainer(profile);
            
            ConexAPI api = new ConexAPI();
            
            Object[] agentArgs = new Object[]{ api };
            
            AgentController thermostatAgent = agentContainer.createNewAgent
            ("Thermostat", AgentTemp.class.getName(), agentArgs);
            

            thermostatAgent.start();
            System.out.println("Agente Termostato lanzado");
            
        }catch (Exception e) {
            System.err.println("Error al inicializar la plataforma JADE:");
            e.printStackTrace();
        }
    }
}
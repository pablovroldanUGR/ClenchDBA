package termostato;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import api.ConexAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

//        // --- BLOQUE DE PRUEBA TEMPORAL ---
//        System.out.println("Probando conexión con DBA Lab y métodos de API...");
//        try {
//            ConexionAPI api = new ConexionAPI();
//            
//            System.out.println("\n1. Obteniendo retos disponibles:");
//            System.out.println(api.getChallenges());
//            
//            System.out.println("\n2. Iniciando reto " + challengeId + ":");
//            String inicioJson = api.startChallenge();
//            System.out.println(inicioJson);
//            
//            // Extraer el runId del JSON y asignarlo a la instancia de ConexionAPI
//            JsonObject jsonObject = JsonParser.parseString(inicioJson).getAsJsonObject();
//            String runId = jsonObject.getAsJsonObject("control").get("runId").getAsString();
//            System.out.println("\n[Run ID extraído: " + runId + "]");
//            
//            System.out.println("\n3. Obteniendo temperatura:");
//            double temp = api.getTemperature();
//            System.out.println("Temperatura actual: " + temp + " ºC");
//            
//            System.out.println("\n4. Obteniendo límites permitidos:");
//            System.out.println("Límite Mínimo: " + api.getMinimumLimit());
//            System.out.println("Límite Máximo: " + api.getMaximumLimit());
//            
//            System.out.println("\n5. Obteniendo temperatura objetivo:");
//            System.out.println(api.getObjectiveTemperature());
//            
//            System.out.println("\n6. Probando acción (Activar refrigeración):");
//            System.out.println(api.activateRefrigeration());
//
//        } catch (Exception e) {
//            System.err.println("Error en la prueba de conexión:");
//            e.printStackTrace();
//        }
//        System.out.println("\n-----------------------------------");
//        // --- FIN DEL BLOQUE DE PRUEBA ---
        
        Runtime rt = Runtime.instance();
        rt.setCloseVM(true); // Cerramos JVM auto al terminar
        
        // Perfil del contenedor
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "1099");
        profile.setParameter(Profile.GUI, "false");
        
        try{
            AgentContainer mainContainer = rt.createMainContainer(profile);
            
            ConexAPI api = new ConexAPI();
            
            Object[] agentArgs = new Object[]{ api };
            
            AgentController thermostatAgent = mainContainer.createNewAgent
            ("Thermostat", "thermostat.Agent", agentArgs);
            
            thermostatAgent.start();
            System.err.println("Agente Termostato lanzado");
            
        }catch (Exception e) {
            System.err.println("Error al inicializar la plataforma JADE:");
            e.printStackTrace();
        }
    }
}
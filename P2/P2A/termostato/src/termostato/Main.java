package termostato;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import api.ConexionAPI;
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

        // --- BLOQUE DE PRUEBA TEMPORAL ---
        System.out.println("Probando conexión con DBA Lab y métodos de API...");
        try {
            ConexionAPI api = new ConexionAPI();
            
            System.out.println("\n1. Obteniendo retos disponibles:");
            System.out.println(api.obtenerRetos());
            
            System.out.println("\n2. Iniciando reto " + challengeId + ":");
            String inicioJson = api.iniciarReto();
            System.out.println(inicioJson);
            
            // Extraer el runId del JSON para usarlo en el resto de peticiones
            JsonObject jsonObject = JsonParser.parseString(inicioJson).getAsJsonObject();
            String runId = jsonObject.getAsJsonObject("control").get("runId").getAsString();
            System.out.println("\n[Run ID extraído: " + runId + "]");
            
            System.out.println("\n3. Obteniendo temperatura:");
            System.out.println(api.obtenerTemperatura(runId));
            
            System.out.println("\n4. Obteniendo límites permitidos:");
            System.out.println("Límite Mínimo: " + api.obtenerLimiteMinimo(runId));
            System.out.println("Límite Máximo: " + api.obtenerLimiteMaximo(runId));
            
            System.out.println("\n5. Obteniendo temperatura objetivo:");
            System.out.println(api.obtenerTermperaturaObjetivo(runId));
            
            System.out.println("\n6. Probando acción (Activar refrigeración):");
            System.out.println(api.activarRefrigeracion(runId));

        } catch (Exception e) {
            System.err.println("Error en la prueba de conexión:");
            e.printStackTrace();
        }
        System.out.println("\n-----------------------------------");
        // --- FIN DEL BLOQUE DE PRUEBA ---
        
        Runtime rt = Runtime.instance();
        rt.setCloseVM(true); // Cerramos JVM auto al terminar
        
        // Perfil del contenedor
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "1099");
        profile.setParameter(Profile.GUI, "false");
        
        try{
            AgentContainer mainContainer = rt.createMainContainer(profile);
            
            Object[] agentArgs = new Object[]{url, token, "THERMOSTAT"};
            
            AgentController termostatoAgent = mainContainer.createNewAgent
            ("Termostato", "dba.thermostat.ThermostatAgent", agentArgs);
            
            termostatoAgent.start();
            System.err.println("Agente Termostato lanzado");
            
        }catch (Exception e) {
            System.err.println("Error al inicializar la plataforma JADE:");
            e.printStackTrace();
        }
    }
}
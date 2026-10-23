package termostato;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import api.ConexionAPI; // Importamos la clase de conexión

public class Main {
    public static void main(String args[]){
        
        // Comprobamos variables de entorno
        String url = System.getenv("DBA_LAB_URL");
        String token = System.getenv("DBA_TOKEN");
        
        if (url == null || url.isBlank() || token == null || token.isBlank()) {
            System.err.println("Error: Debes definir las variables de entorno "
                    + "DBA_LAB_URL y DBA_TOKEN.");
            System.exit(1);
        }

        // --- BLOQUE DE PRUEBA TEMPORAL ---
        System.out.println("Probando conexión con DBA Lab...");
        try {
            ConexionAPI api = new ConexionAPI();
            String retos = api.obtenerRetos();
            System.out.println("Lista de retos obtenida correctamente:\n" + retos);
        } catch (Exception e) {
            System.err.println("Error en la prueba de conexión:");
            e.printStackTrace();
        }
        System.out.println("-----------------------------------");
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
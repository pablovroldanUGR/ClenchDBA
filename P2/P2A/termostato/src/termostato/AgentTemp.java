package termostato;

import api.ConexAPI;
import common.Pair;
import com.google.gson.JsonObject;
import jade.core.Agent;

public class AgentTemp extends Agent {
    // Para imprimir mensajes
    private String name = "[THERMOSTAT]: ";
    
    // Conexion a la API
    private ConexAPI thermo;
    
    // Estado del entorno
    private double limit_inf;
    private double limit_sup;
    private double target;
    
    // Id del desafio
    private int challenge_id = 0;
    
    // Id de la run
    private String run_id = "";

    @Override
    protected void setup() {
        
        this.thermo = new ConexAPI();

        try {
            this.thermo.startChallenge();
                
            this.challenge_id = Integer.parseInt(System.getenv("DBA_CHALLENGE_ID"));
            this.run_id = this.thermo.getRunID();
                
            printMessage("Reto " + this.challenge_id + " iniciado. Run ID: " + this.run_id);
                
            Pair<JsonObject> limits = this.thermo.getSensor("thermostat.limits");
            this.limit_inf = limits.particular.getAsJsonObject("value").get("lower").getAsDouble();
            this.limit_sup = limits.particular.getAsJsonObject("value").get("upper").getAsDouble();
            Pair<JsonObject> target = this.thermo.getObjectiveTemperature();
            this.target = target.particular.getAsJsonObject("value").get("celsius").getAsDouble();
                
            printMessage("Estado inicial -> Objetivo: " + this.target + " | Limites: [" + this.limit_inf + ", " + limit_sup + "]");
            addBehaviour(new BHThermo());

        } catch (Exception e) {
            System.err.println("Error al comunicarse con la API durante el setup: " + e.getMessage());
            e.printStackTrace();
            doDelete(); 
        }
    }

    public void printMessage(String msg) {
        System.out.println(name + msg);
    }

    public ConexAPI getApi() {
        return this.thermo;
    }
    
    public int getChallengeId() {
        return this.challenge_id;
    }
    
    public String getRunId() {
        return this.run_id;
    }
    
    public double getTarget() {
        return target;
    }
    

    @Override
    protected void takeDown(){
        try{
            thermo.closeChallenge();
            System.out.println("API cerrada");
        }
        catch (Exception e2){
            System.out.println("Error al cerrar la conexión con la API." + e2.getMessage());
        }
        System.exit(0);
    }
}

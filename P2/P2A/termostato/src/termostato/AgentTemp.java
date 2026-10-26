package termostato;

import api.ConexAPI;
import jade.core.Agent;
import jade.core.behaviours.*;

public class AgentTemp extends Agent {
    
    // Para imprimir mensajes
    private String name = "[THERMOSTAT]: ";
    
    // Conexion a la API
    private ConexAPI thermo;
    
    // Estado del entorno
    private double limit_inf;
    private double limit_sup;
    private double target;
    private double current_temp;
    
    // Id del desafio
    private int challenge_id = 0;
    
    // Id de la run
    private String run_id = "";

    @Override
    protected void setup() {
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            this.thermo = (ConexAPI) args[0];

            try {
                this.thermo.startChallenge();
                
                this.challenge_id = Integer.parseInt(System.getenv("DBA_CHALLENGE_ID"));
                this.run_id = this.thermo.getRunID();
                
                printMessage("Reto " + this.challenge_id + " iniciado. Run ID: " + this.run_id);
                
                this.limit_inf = this.thermo.getMinimumLimit();
                this.limit_sup = this.thermo.getMaximumLimit();
                this.target = this.thermo.getObjectiveTemperature();
                
                printMessage("Estado inicial -> Objetivo: " + this.target + " | Limites: [" + this.limit_inf + ", " + limit_sup + "]");

            } catch (Exception e) {
                System.err.println("Error al comunicarse con la API durante el setup: " + e.getMessage());
                e.printStackTrace();
                doDelete(); 
            }
        } else {
            System.out.println("No se ha pasado la conexión a la API");
            doDelete();
        }
        
        addBehaviour(new BHThermo());
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
    

}

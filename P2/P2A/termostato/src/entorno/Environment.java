package entorno;

import common.RunStatus;
import api.ConexAPI;

public abstract class Environment {
    protected ConexAPI api;
    private static Environment instance; 
    public RunStatus runStatus;
    protected int runID;
    protected int challengeID;
    
    protected Environment(){
        this.runStatus = RunStatus.UNINITALIZED;
        this.api = new ConexAPI();
        this.runID = -1;
        this.challengeID = -1;
    }
    
    public static Environment getInstance(String name){
        if(instance == null){
            if(name.equalsIgnoreCase("Thermo")){
                instance = new EnvironmentThermo();
            }
            else if (name.equalsIgnoreCase("Grid")){
                instance = new EnvironmentGrid();
            }
            else{
                throw new IllegalArgumentException("Error al crear el entorno. Entorno no válido");
            }
        }
        
        return instance;
    }
    
    public void setChallenge(int challengeID){
        this.challengeID = challengeID;
    }
    
    public void startChallenge(){
        if(this.challengeID != -1){
            Control controlInfo = api.startChallenge(this.challengeID);
            this.runID = controlInfo.runID;
            this.runStatus = controlInfo.runStatus;
        }
        else{
            System.out.println("Error al intentar iniciar el reto. Debes seleccionar uno primero");
        }
    }
    
    public void checkStatus(){
        if(this.runID != -1){
            Control controlInfo = api.getStatus(this.runID);
            this.runStatus = controlInfo.runsStatus;
        }
        else{
            System.out.println("Error al intentar obtener el estado de la run " + runID);
        }
    }
    
    public void closeRun(){
        if(this.runID != -1){
            Control controlInfo = api.closeRun(this.runID);
            this.runStatus = controlInfo.runsStatus;
        }
        else{
            System.out.println("Error al intentar cerrar la run " + runID);
        }
    }
}

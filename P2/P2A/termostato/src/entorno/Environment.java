package entorno;

import api.ConexAPI;

public abstract class Environment {
    protected ConexAPI api;
    private static Environment instance; 
    public RunStatus runStatus;
    protected int runID;
    protected int challengeID;
    
    protected Environment(){
        runStatus = RunStatus.UNINITALIZED;
        api = new ConexAPI();
        runID = -1;
        challengeID = -1;
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
}

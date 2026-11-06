package entorno;

import java.util.ArrayList;

public class EnvironmentThermo extends Environment{
    private final String SENSOR_TEMP = "temperature";
    private final String SENSOR_LIMITS = "limits";
    private final String SENSOR_TARGET = "target";
    
    private double temperature;
    private ArrayList<Double> limits;   //limits[0]=limit.low, limits[1]=limit.up
    private double target;
    
    protected EnvironmentThermo(){
        super();
        
        this.temperature = Double.NaN;
        this.target = Double.NaN;
        this.limits = new ArrayList<>();
    }
    
    public ArrayList<Double> getLimits(){
        if(this.runID != -1){
            Control controlInfo = api.getSensor(SENSOR_LIMITS);
            this.runStatus = controlInfo.runsStatus;
            this.limits = controlInfo.limits;
        }
        else{
            System.out.println("Error al intentar obtener el estado de la run " + runID);
        }
        
        return limits;
    }
}
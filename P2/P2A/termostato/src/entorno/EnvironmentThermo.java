package entorno;

import java.util.ArrayList;

public class EnvironmentThermo extends Environment{
    private final String ENV_TYPE = "thermostat.";
    private final String SENSOR_TEMP = "temperature";
    private final String SENSOR_LIMITS = "limits";
    private final String SENSOR_TARGET = "target";
    private final String SENSOR_OBJECTIVE = "objective";
    private final String ACTION_ON = "refrigeration.on";
    private final String ACTION_OFF = "refrigeration.off";
    
    private double temperature;
    private ArrayList<Double> limits;   //limits[0]=limit.low, limits[1]=limit.up
    private double target;
    
    protected EnvironmentThermo(){
        super();
        
        this.temperature = Double.NaN;
        this.target = Double.NaN;
        this.limits = new ArrayList<>();
    }
    
    private void getLimits(){
        if(this.runID != -1){
            Control controlInfo = api.getSensor(this.ENV_TYPE + this.SENSOR_LIMITS);
            this.runStatus = controlInfo.runsStatus;
            this.limits = null;
        }
        else{
            System.out.println("Error al intentar obtener el estado de la run " + runID);
        }
    }
    
    public double getMinimumLimit(){
        this.getLimits();
        return this.limits.get(0);
    }
    
    public double getMaximumLimit(){
        this.getLimits();
        return this.limits.get(1);
    }
    
    public double getTemperature(){
        if(this.runID != -1){
            Control controlInfo = api.getSensor(this.ENV_TYPE + this.SENSOR_TEMP);
            this.temperature = 0;
        }
        else{
            System.out.println("Error al intentar obtener la temperatura de la run " + runID);
        }
        
        return this.temperature;
    }
    
    public double getObjectiveTemperature(){
        
    }
    
    public void activateRefrigeration(){
        if(this.runID != -1){
            Control controlInfo = api.doAction(this.ENV_TYPE + this.ACTION_ON);
            this.temperature = 0;
        }
        else{
            System.out.println("Error al intentar activar la refrigeración en la run " + runID);
        }
    }
    
     public void disableRefrigeration(){
        if(this.runID != -1){
            Control controlInfo = api.doAction(this.ENV_TYPE + this.ACTION_OFF);
            this.temperature = 0;
        }
        else{
            System.out.println("Error al intentar desactivar la refrigeración en la run " + runID);
        }
    }
}
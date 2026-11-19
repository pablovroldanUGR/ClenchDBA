package entorno;

import api.ResponseLAB;
import common.Pair;

public class EnvironmentThermo extends Environment{
    private final String ENV_TYPE = "thermostat.";
    private final String SENSOR_TEMP = "temperature";
    private final String SENSOR_LIMITS = "limits";
    private final String SENSOR_TARGET = "target";
    private final String ACTION_ON = "refrigeration.on";
    private final String ACTION_OFF = "refrigeration.off";
    
    private double temperature;
    private Pair<Double, Double> limits;   //limits[0]=limit.low, limits[1]=limit.up
    private double target;
    
    protected EnvironmentThermo(){
        super();
        
        this.temperature = Double.NaN;
        this.target = Double.NaN;
        this.limits = new Pair<>();
    }
    
    private void getLimits(){
        if(this.runID != -1){
            ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_LIMITS);
            if(response != null){
                this.runStatus = response.runStatus;
                this.limits.setFirst(Double.valueOf(response.value.get(0).getSecond()));
                this.limits.setSecond(Double.valueOf(response.value.get(1).getSecond()));
            }
        }
        else{
            Main.printGUI("[THERMO][ERROR] No se pueden leer los límites: falta una run activa.");
        }
    }
    
    public double getMinimumLimit(){
        this.getLimits();
        return this.limits.getFirst();
    }
    
    public double getMaximumLimit(){
        this.getLimits();
        return this.limits.getSecond();
    }
    
    public double getTemperature(){
        if(this.runID != -1){
            ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_TEMP);

            if(response != null){
                this.runStatus = response.runStatus;
                this.temperature = Double.valueOf(response.value.get(0).getSecond());
            }
        }
        else{
            Main.printGUI("[THERMO][ERROR] No se puede leer la temperatura: falta una run activa.");
        }
        
        return this.temperature;
    }
    
    public double getTargetTemperature(){
        if(this.runID != -1){
            ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_TARGET);
            
            if(response != null){
                this.runStatus = response.runStatus;
                this.target = Double.valueOf(response.value.get(0).getSecond());
            }
        }
        else{
            Main.printGUI("[THERMO][ERROR] No se puede leer la temperatura objetivo: falta una run activa.");
        }
        
        return this.target;
    }
    
    public void activateRefrigeration(){
        if(this.runID != -1){
            ResponseLAB response = api.executeAction(this.runID, this.ENV_TYPE + this.ACTION_ON);

            if(response != null){
                this.runStatus = response.runStatus;
            }
        }
        else{
            Main.printGUI("[THERMO][ERROR] No se puede activar la refrigeración: falta una run activa.");
        }
    }
    
     public void disableRefrigeration(){
        if(this.runID != -1){
            ResponseLAB response = api.executeAction(this.runID, this.ENV_TYPE + this.ACTION_OFF);
            
            if(response != null){
                this.runStatus = response.runStatus;
            }
        }
        else{
            Main.printGUI("[THERMO][ERROR] No se puede desactivar la refrigeración: falta una run activa.");
        }
    }
}

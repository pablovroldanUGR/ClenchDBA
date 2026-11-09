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
            try{
                ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_LIMITS);
                this.runStatus = response.runStatus;
                this.limits.setFirst(Double.valueOf(response.value.get(0).getSecond()));
                this.limits.setSecond(Double.valueOf(response.value.get(1).getSecond()));
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        else{
            System.out.println("Error al intentar obtener los límites. RunID no inicailizado");
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
            try{
                ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_TEMP);
                this.runStatus = response.runStatus;
                this.temperature = Double.valueOf(response.value.get(0).getSecond());
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        else{
            System.out.println("Error al intentar obtener la temperatura. RunID no inicailizado");
        }
        
        return this.temperature;
    }
    
    public double getTargetTemperature(){
        if(this.runID != -1){
            try{
                ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_TARGET);
                this.runStatus = response.runStatus;
                this.target = Double.valueOf(response.value.get(0).getSecond());
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        else{
            System.out.println("Error al intentar obtener la temperatura objetivo. RunID no inicailizado");
        }
        
        return this.target;
    }
    
    public void activateRefrigeration(){
        if(this.runID != -1){
            try{
                ResponseLAB response = api.executeAction(this.runID, this.ENV_TYPE + this.ACTION_ON);
                this.runStatus = response.runStatus;
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        else{
            System.out.println("Error al intentar activar la refrigeración. RunID no inicializado");
        }
    }
    
     public void disableRefrigeration(){
        if(this.runID != -1){
            try{
                ResponseLAB response = api.executeAction(this.runID, this.ENV_TYPE + this.ACTION_OFF);
                this.runStatus = response.runStatus;
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        else{
            System.out.println("Error al intentar desactivar la refrigeración. RunID no inicializado");
        }
    }
}
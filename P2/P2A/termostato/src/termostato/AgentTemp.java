package termostato;

import api.ConexionAPI;
import jade.core.Agent;
import jade.core.behaviours.*;

public class AgentTemp extends Agent {

    private String name = "[TERMOSTATO]: ";

    private ConexionAPI termostato;
    private double limit_inf;
    private double limit_sup;
    private double target;
    private double current_temp;

    @Override
    protected void setup() {
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            this.termostato = (ConexionAPI) args[0];

            try {
                this.limit_inf = this.termostato.getMaximumLimit();
                this.limit_sup = this.termostato.getMinimumLimit();
                this.target = this.termostato.getObjectiveTemperature();

            } catch (Exception e) {
                System.err.println("Error al comunicarse con la API durante el setup: " + e.getMessage());
                e.printStackTrace();
                doDelete(); 
            }
        } else {
            System.out.println("No se ha pasado la conexión a la API");
            doDelete();
        }
    }

    public void ImprimirMensaje(String mensaje) {
        System.out.println(name + mensaje);
    }

    public ConexionAPI getApi() {
        return this.termostato;
    }

}

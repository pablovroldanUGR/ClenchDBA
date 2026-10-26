package termostato;

import jade.core.Agent;
import jade.core.behaviours.*;
import api.ConexAPI;

public class BHThermo extends Behaviour {

    @Override
    public void action() {

        try {
            String status = ((AgentTemp) myAgent).getApi().getStatus();

            if (status == "ACTIVE") {

            } else if (status == "SUCCEEDED") {
                ((AgentTemp) myAgent).printMessage("COMPLETADO");
            }
        } catch (Exception e) {
            System.err.println("Error al comunicarse con la API durante el setup: " + e.getMessage());
            e.printStackTrace();
            myAgent.doDelete();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}

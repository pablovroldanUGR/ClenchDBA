/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package termostato;

/**
 *
 * @author PabloHI
 */

import jade.core.Agent;
import jade.core.behaviours.*;  
import api.ConexAPI;

public class BHTermo {
    
    public class comprobarTemperatura extends OneShotBehaviour{
        @Override
        public void action(){
           ConexAPI api = ((AgentTemp) myAgent).getApi();
           
           double temperatura = api.getTemperature();
        }
    }
}

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

public class BHThermoFSM {
    
    public class checkStatus extends OneShotBehaviour{
        
        @Override
        public void action(){
           String status = ((AgentTemp)myAgent).getApi().getStatus();
           getDataStore().put("STATUS", status);
        }
    }
}

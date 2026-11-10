/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package grid;

import jade.core.behaviours.Behaviour;
import common.RunStatus;
import entorno.EnvironmentGrid;
import jade.core.behaviours.*;
import common.Direction;
import java.util.*;
import common.Pair;

/**
 *
 * @author neocofias1258
 */
public class BHGrid extends Behaviour {

    private int exitCode = 0;
    private EnvironmentGrid grid;
    private Direction direction = null;

    //orden: norte, este, sur, oeste
    private ArrayList<Double> distancias = new ArrayList<>();
    private ArrayList<Pair<Integer, Integer>> casillas = new ArrayList<>();
    private Pair<Integer,Integer> optima = null;

    @Override
    public void action() {
        grid = ((AgentGrid) myAgent).getEnvironment();
        direction = ((AgentGrid) myAgent).getDirection();

        try {
            grid.checkStatus(); // Tira excepción

            if (grid.runStatus == RunStatus.ACTIVE) {
                // Consultar la posicion del bot donde spawnea
                Pair<Integer, Integer> posicion = grid.getPos();
                int fila = posicion.getFirst();
                int columna = posicion.getSecond();

                casillas.clear();

                casillas.add(new Pair<>(fila - 1, columna)); // Norte
                casillas.add(new Pair<>(fila, columna + 1)); // Este
                casillas.add(new Pair<>(fila + 1, columna)); // Sur
                casillas.add(new Pair<>(fila, columna - 1)); // Oeste

                // Calculamos distancia manhattan
                for (Pair<Integer, Integer> casilla : casillas) {
                    double distancia = Math.abs(casilla.getFirst() - grid.getTargetPos().getFirst()) + Math.abs(casilla.getSecond() - grid.getTargetPos().getSecond());
                    distancias.add(distancia);
                }
                // Seleccionamos la casilla mas optima
                if (!distancias.isEmpty()){
                    double maxima = Collections.max(distancias);
                    int indice = distancias.indexOf(maxima);
                    
                    optima = casillas.get(indice);
                }
                
                //Consultamos el estado de las casillas con la api
                
                
                //Si resulta que la casilla mas optima es un muro, activamos mano derecha
                //si no, nos movemos y actualizamos la matriz

                ((AgentGrid) myAgent).printMessage("Temperatura obtenida -> " + temp);

                // si no estamos en mano derecha
                if (temp >= grid.) {
                    grid.activateRefrigeration(); // CALIENTE: ACTIVAMOS REFRIGERACION
                    ((AgentGrid) myAgent).printMessage("CALIENTE: ACTIVAMOS REFRIGERACION");

                } else {
                    //si estamos en mano derecha
                    grid.disableRefrigeration(); // FRIO: DESACTIVAMOS REFRIGERACION
                    ((AgentGrid) myAgent).printMessage("FRIO: DESACTIVAMOS REFRIGERACION");

                }

            } else if (grid.runStatus == RunStatus.SUCCEEDED) {
                // Reto completado
                ((AgentGrid) myAgent).printMessage("RETO COMPLETADO");
                this.exitCode = 1;
            }
        } catch (Exception e) {
            System.out.println("Error por parte del agente al solicitar estado al entorno. " + e.getMessage());
            myAgent.doDelete();
        }
    }

    @Override
    public boolean done() {
        // Si no esta activa la run, terminamos
        return !(thermo.runStatus == RunStatus.ACTIVE);
    }

    @Override
    public int onEnd() {
        myAgent.doDelete();
        return this.exitCode;
    }

}

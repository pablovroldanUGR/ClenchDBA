/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package grid;

import jade.core.behaviours.Behaviour;
import common.RunStatus;
import entorno.EnvironmentGrid;
import jade.core.behaviours.*;
import common.CellType;
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
    private ArrayList<Pair<Integer, Integer>> casillas_adyacentes = new ArrayList<>();
    private Pair<Integer, Integer> optima = null;
    private int indice_optima = -1;
    private ArrayList<CellType> bumpers = new ArrayList<>();
    private Boolean mano_derecha = false;
    Direction[] direcciones = {
        Direction.NORTH,
        Direction.EAST,
        Direction.SOUTH,
        Direction.WEST
    };

    @Override
    public void action() {
        grid = ((AgentGrid) myAgent).getEnvironment();

        try {
            grid.checkStatus();

            if (grid.runStatus == RunStatus.ACTIVE) {
                bumpers = grid.getBumpers();

                if (!mano_derecha) {
                    // Comportamiento normal: acercarse al objetivo.
                    Pair<Integer, Integer> posicion = grid.getPos();
                    Pair<Integer, Integer> objetivo = grid.getTargetPos();

                    int fila = posicion.getFirst();
                    int columna = posicion.getSecond();

                    casillas_adyacentes.clear();
                    distancias.clear();

                    // Mismo orden que el array direcciones.
                    casillas_adyacentes.add(new Pair<>(fila - 1, columna)); // Norte
                    casillas_adyacentes.add(new Pair<>(fila, columna + 1)); // Este
                    casillas_adyacentes.add(new Pair<>(fila + 1, columna)); // Sur
                    casillas_adyacentes.add(new Pair<>(fila, columna - 1)); // Oeste

                    for (Pair<Integer, Integer> casilla : casillas_adyacentes) {
                        double distancia
                                = Math.abs(casilla.getFirst() - objetivo.getFirst())
                                + Math.abs(casilla.getSecond() - objetivo.getSecond());

                        distancias.add(distancia);
                    }

                    double minima = Collections.min(distancias);
                    indice_optima = distancias.indexOf(minima);
                    optima = casillas_adyacentes.get(indice_optima);

                    if (bumpers.get(indice_optima) == CellType.WALL) {
                        mano_derecha = true;
                    } else if (bumpers.get(indice_optima) == CellType.FREE) {
                        direction = direcciones[indice_optima];
                        grid.move(direction);
                        grid.getPos();
                    }

                } else {
                    // TODO: implementar el comportamiento de mano derecha.
                }

            } else if (grid.runStatus == RunStatus.SUCCEEDED) {
                ((AgentGrid) myAgent).printMessage("RETO COMPLETADO");
                this.exitCode = 1;
            }

        } catch (Exception e) {
            System.out.println(
                    "Error por parte del agente al solicitar estado al entorno. "
                    + e.getMessage()
            );
            myAgent.doDelete();
        }
    }

    @Override
    public boolean done() {
        // Si no esta activa la run, terminamos
        return !(grid.runStatus == RunStatus.ACTIVE);
    }

    @Override
    public int onEnd() {
        myAgent.doDelete();
        return this.exitCode;
    }

}

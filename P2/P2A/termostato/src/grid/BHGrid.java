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
import entorno.Main;

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
    private Direction pared = null;
    private Direction siguiente_move_pared = null;
    
    

    @Override
    public void action() {
        System.out.println("[GRID][BHGrid.action] Inicio de iteración; mano_derecha=" + mano_derecha);
        grid = ((AgentGrid) myAgent).getEnvironment();
        System.out.println("[GRID][BHGrid.action] Entorno obtenido");

        try {
            System.out.println("[GRID][BHGrid.action] Consultando estado de la run");
            grid.checkStatus();
            System.out.println("[GRID][BHGrid.action] Estado recibido: " + grid.runStatus);
            
            if (grid.runStatus == RunStatus.ACTIVE) {
                System.out.println("[GRID][BHGrid.action] Obteniendo posicion");
                ((AgentGrid) myAgent).position = grid.getPos();
                Pair<Integer, Integer> objetivo = grid.getTargetPos();
                System.out.println("[GRID][BHGrid.action] Consultando bumpers");
                bumpers = grid.getBumpers();
                System.out.println("[GRID][BHGrid.action] Bumpers recibidos (N, E, S, O): " + bumpers);

                if (!mano_derecha) {
                    System.out.println("[GRID][BHGrid.action] Comportamiento normal; consultando posición");
                    // Comportamiento normal: acercarse al objetivo.
                    ((AgentGrid) myAgent).position = grid.getPos();
                    System.out.println("[GRID][BHGrid.action] Posición recibida: " + ((AgentGrid) myAgent).position + "; consultando objetivo");
                    objetivo = grid.getTargetPos();
                    System.out.println("[GRID][BHGrid.action] Objetivo recibido: " + objetivo);

                    int fila = ((AgentGrid) myAgent).position.getFirst();
                    int columna = ((AgentGrid) myAgent).position.getSecond();

                    casillas_adyacentes.clear();
                    distancias.clear();
                    System.out.println("[GRID][BHGrid.action] Calculando casillas adyacentes desde fila=" + fila + ", columna=" + columna);

                    // Mismo orden que el array direcciones.
                    casillas_adyacentes.add(new Pair<>(fila - 1, columna)); // Norte
                    casillas_adyacentes.add(new Pair<>(fila, columna + 1)); // Este
                    casillas_adyacentes.add(new Pair<>(fila + 1, columna)); // Sur
                    casillas_adyacentes.add(new Pair<>(fila, columna - 1)); // Oeste
                    System.out.println("[GRID][BHGrid.action] Casillas adyacentes: " + casillas_adyacentes);

                    for (Pair<Integer, Integer> casilla : casillas_adyacentes) {
                        double distancia
                                = Math.abs(casilla.getFirst() - objetivo.getFirst())
                                + Math.abs(casilla.getSecond() - objetivo.getSecond());

                        distancias.add(distancia);
                        System.out.println("[GRID][BHGrid.action] Casilla=" + casilla + ", distancia=" + distancia);
                    }

                    double minima = Collections.min(distancias);
                    indice_optima = distancias.indexOf(minima);
                    optima = casillas_adyacentes.get(indice_optima);
                    System.out.println("[GRID][BHGrid.action] Óptima=" + optima + ", índice=" + indice_optima + ", dirección=" + direcciones[indice_optima] + ", bumper=" + bumpers.get(indice_optima));

                    if (bumpers.get(indice_optima).equals(true)) {
                        System.out.println("[GRID][BHGrid.action] Bumper equals(true); activando mano derecha");
                        mano_derecha = true;
                    } else if (bumpers.get(indice_optima).equals(false)) {
                        System.out.println("[GRID][BHGrid.action] Bumper equals(false); preparando movimiento");
                        direction = direcciones[indice_optima];
                        System.out.println("[GRID][BHGrid.action] Llamando a move: " + direction);
                        grid.move(direction);
                        System.out.println("[GRID][BHGrid.action] Movimiento terminado; consultando posición");
                        grid.getPos();
                        System.out.println("[GRID][BHGrid.action] Consulta de posición tras movimiento terminada");
                    }
                    System.out.println("[GRID][BHGrid.action] Fin de evaluación del bumper: " + bumpers.get(indice_optima) + "; mano_derecha=" + mano_derecha + ", dirección=" + direction);

                } else {
                    System.out.println("[GRID][BHGrid.action] Rama de mano derecha pendiente de implementar");
                    // TODO: implementar el comportamiento de mano derecha.
                                        
                    
                    
                }

            } else if (grid.runStatus == RunStatus.SUCCEEDED) {
                System.out.println("[GRID][BHGrid.action] Reto completado");
                ((AgentGrid) myAgent).printMessage("RETO COMPLETADO");
                this.exitCode = 1;
            }

        } catch (Exception e) {
            System.out.println("[GRID][BHGrid.action] Excepción: " + e + "; traza=" + Arrays.toString(e.getStackTrace()));
            Main.printGUI(
                    "Error por parte del agente al solicitar estado al entorno. "
                    + e.getMessage()
            );
            System.out.println("[GRID][BHGrid.action] Solicitando eliminación del agente");
            myAgent.doDelete();
        }
        System.out.println("[GRID][BHGrid.action] Fin de iteración; estado=" + grid.runStatus);
    }

    @Override
    public boolean done() {
        System.out.println("[GRID][BHGrid.done] Estado=" + grid.runStatus + ", done=" + !(grid.runStatus == RunStatus.ACTIVE));
        // Si no esta activa la run, terminamos
        System.out.println("RUN TERMINADA CON STATUS: " + grid.runStatus);
        return !(grid.runStatus == RunStatus.ACTIVE);
    }

    @Override
    public int onEnd() {
        System.out.println("[GRID][BHGrid.onEnd] Eliminando agente; exitCode=" + this.exitCode);
        myAgent.doDelete();
        System.out.println("[GRID][BHGrid.onEnd] Eliminación solicitada");
        return this.exitCode;
    }

}

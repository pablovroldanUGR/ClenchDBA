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
    private boolean mano_derecha = false;
    private int wallFollowStartDistance = -1;
    Direction[] direcciones = {
        Direction.NORTH,
        Direction.EAST,
        Direction.SOUTH,
        Direction.WEST
    };
    private Direction last_move = null;
    

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
                Pair<Integer, Integer> position = grid.getPos();
                ((AgentGrid) myAgent).position = position;
                Pair<Integer, Integer> objetivo = grid.getTargetPos();
                System.out.println("[GRID][BHGrid.action] Consultando bumpers");
                bumpers = grid.getBumpers();
                System.out.println("[GRID][BHGrid.action] Bumpers recibidos (N, E, S, O): " + bumpers);

                calculateOptimalCell(position, objetivo);
                CellType optimalCellType = bumpers.get(indice_optima);
                int currentDistance = manhattanDistance(position, objetivo);
                System.out.println("[GRID][BHGrid.action] Óptima=" + optima
                        + ", dirección=" + direcciones[indice_optima]
                        + ", tipo=" + optimalCellType
                        + ", distancia al objetivo=" + currentDistance
                        + ", distancia al entrar en mano derecha=" + wallFollowStartDistance);

                if (mano_derecha
                        && optimalCellType == CellType.FREE
                        && currentDistance < wallFollowStartDistance) {
                    // La ruta preferida está libre y ya hemos progresado desde el obstáculo.
                    mano_derecha = false;
                    wallFollowStartDistance = -1;
                    move(direcciones[indice_optima]);
                } else if (!mano_derecha && optimalCellType == CellType.FREE) {
                    move(direcciones[indice_optima]);
                } else if (!mano_derecha && optimalCellType == CellType.WALL) {
                    // Se intentaba avanzar hacia la pared;
                    mano_derecha = true;
                    wallFollowStartDistance = currentDistance;
                    last_move = direcciones[indice_optima];
                    followRightWall();
                } else if (mano_derecha) {
                    followRightWall();
                } else {
                    System.out.println("[GRID][BHGrid.action] La casilla óptima es UNKNOWN; no se mueve.");
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

    private void calculateOptimalCell(Pair<Integer, Integer> position,
                                      Pair<Integer, Integer> target) {
        int row = position.getFirst();
        int col = position.getSecond();

        casillas_adyacentes.clear();
        distancias.clear();
        // El orden coincide con bumpers y direcciones
        casillas_adyacentes.add(new Pair<>(row - 1, col));
        casillas_adyacentes.add(new Pair<>(row, col + 1));
        casillas_adyacentes.add(new Pair<>(row + 1, col));
        casillas_adyacentes.add(new Pair<>(row, col - 1));

        for (Pair<Integer, Integer> cell : casillas_adyacentes) {
            double distance = Math.abs(cell.getFirst() - target.getFirst())
                    + Math.abs(cell.getSecond() - target.getSecond());
            distancias.add(distance);
        }

        double minimum = Collections.min(distancias);
        indice_optima = distancias.indexOf(minimum);
        optima = casillas_adyacentes.get(indice_optima);
    }

    private int manhattanDistance(Pair<Integer, Integer> from,
                                  Pair<Integer, Integer> to) {
        return Math.abs(from.getFirst() - to.getFirst())
                + Math.abs(from.getSecond() - to.getSecond());
    }

    /** Sigue la pared por la derecha: derecha, recto, izquierda y atrás. */
    private void followRightWall() {
        if (last_move == null) {
            // Por si acaso
            last_move = direcciones[indice_optima];
        }

        Direction[] choices = {
            turnRight(last_move),
            last_move,
            turnLeft(last_move),
            opposite(last_move)
        };

        for (Direction choice : choices) {
            if (bumperFor(choice) == CellType.FREE) {
                move(choice);
                return;
            }
        }

        System.out.println("[GRID][BHGrid.action] Mano derecha: no hay movimiento libre conocido.");
    }

    private void move(Direction nextDirection) {
        direction = nextDirection;
        System.out.println("[GRID][BHGrid.action] Moviendo hacia " + direction);
        grid.move(direction);
        last_move = direction;
    }

    private CellType bumperFor(Direction requestedDirection) {
        for (int i = 0; i < direcciones.length; i++) {
            if (direcciones[i] == requestedDirection) {
                return bumpers.get(i);
            }
        }
        return CellType.UNKNOWN;
    }

    private Direction turnRight(Direction current) {
        switch (current) {
            case NORTH: return Direction.EAST;
            case EAST: return Direction.SOUTH;
            case SOUTH: return Direction.WEST;
            case WEST: return Direction.NORTH;
            default: throw new IllegalArgumentException("Dirección no válida: " + current);
        }
    }

    private Direction turnLeft(Direction current) {
        switch (current) {
            case NORTH: return Direction.WEST;
            case WEST: return Direction.SOUTH;
            case SOUTH: return Direction.EAST;
            case EAST: return Direction.NORTH;
            default: throw new IllegalArgumentException("Dirección no válida: " + current);
        }
    }

    private Direction opposite(Direction current) {
        switch (current) {
            case NORTH: return Direction.SOUTH;
            case SOUTH: return Direction.NORTH;
            case EAST: return Direction.WEST;
            case WEST: return Direction.EAST;
            default: throw new IllegalArgumentException("Dirección no válida: " + current);
        }
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

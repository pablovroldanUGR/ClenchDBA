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
    private boolean active_hand = false;
    private String hand_decision = "";
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

        grid = ((AgentGrid) myAgent).getEnvironment();


        try {
            grid.checkStatus();


            if (grid.runStatus == RunStatus.ACTIVE) {

                Pair<Integer, Integer> position = grid.getPos();
                ((AgentGrid) myAgent).position = position;
                Pair<Integer, Integer> objetivo = grid.getTargetPos();

                bumpers = grid.getBumpers();

                calculateOptimalCell(position, objetivo);
                CellType optimalCellType = bumpers.get(indice_optima);
                int currentDistance = manhattanDistance(position, objetivo);


                if (active_hand
                        && (optimalCellType == CellType.FREE || optimalCellType == CellType.GOAL)
                        && currentDistance < wallFollowStartDistance) {
                    // La ruta preferida está libre y ya hemos progresado desde el obstáculo.
                    active_hand = false;
                    wallFollowStartDistance = -1;
                    move(direcciones[indice_optima]);
                    Main.printGUI("[COMPORTAMIENTO]: Se pasa de mano derecha a greedy");
                } else if (!active_hand && (optimalCellType == CellType.FREE || optimalCellType == CellType.GOAL)) {
                    move(direcciones[indice_optima]);
                    Main.printGUI("[COMPORTAMIENTO]: Greedy");
                } else if (!active_hand && optimalCellType == CellType.WALL) {
                    // Orientarse en paralelo al muro para dejarlo a la derecha.
                    active_hand = true;
                    wallFollowStartDistance = currentDistance;
                    
                    hand_decision = getOptimalHandType(objetivo); 
                    last_move = (hand_decision.equals("right"))? turnLeft(direcciones[indice_optima]): turnRight(direcciones[indice_optima]);
                    followWall(hand_decision);
                    Main.printGUI("[COMPORTAMIENTO]: Se pasa de greedy a mano " + hand_decision);
                } else if (active_hand) {
                    followWall(hand_decision);
                    Main.printGUI("[COMPORTAMIENTO]: Mano " + hand_decision);
                } else {

                }

            } else if (grid.runStatus == RunStatus.SUCCEEDED) {

                ((AgentGrid) myAgent).printMessage("RETO COMPLETADO");
                this.exitCode = 1;
            }

        } catch (Exception e) {
            Main.printGUI("[GRID][ERROR] El agente no pudo consultar el entorno: " + e.getMessage());
            myAgent.doDelete();
        }

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
            double distance = manhattanDistance(cell, target);
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

    /** Sigue la pared con la mano derecha o izquierda*/
    private void followWall(String hand) {
        if (last_move == null) {
            // Por si acaso
            last_move = direcciones[indice_optima];
        }
        
        Direction[] choices = new Direction[4];
        
        if(hand.equals("right")){
            choices[0] = turnRight(last_move);
            choices[2] = turnLeft(last_move);
        }
        else{
            choices[0] = turnLeft(last_move);
            choices[2] = turnRight(last_move);
        }
        
        choices[1] = last_move;
        choices[3] = opposite(last_move);

        for (Direction choice : choices) {
            if (bumperFor(choice) == CellType.FREE || bumperFor(choice) == CellType.GOAL) {
                move(choice);
                return;
            }
        }
    }
    
    private String getOptimalHandType(Pair<Integer, Integer> objetivo){        
        // Partimos de casillas_adyacentes (Norte, Este, Sur, Oeste)
        // Tenemos que mapear casillas adyacentes a delante, atrás, izquierda, derecha a orientación
        // Primero obtenemos como dirección de referencia el frente y a partir de ella sacamos los demás
        Direction frontDir = direcciones[indice_optima];
        Direction leftDir = getDirectionByOrientation("left", frontDir);
        Direction rightDir = getDirectionByOrientation("right", frontDir);
        Direction backDir = getDirectionByOrientation("back", frontDir);
        
        CellType leftType = bumperFor(leftDir);
        CellType rightType = bumperFor(rightDir);
        
        Direction newLeftDir = (leftType==CellType.FREE || leftType==CellType.GOAL)? leftDir : backDir;
        Direction newRightDir = (rightType==CellType.FREE || rightType==CellType.GOAL)? rightDir : backDir;
        
        Pair<Integer, Integer> newLeftPos = getPosByDirection(newLeftDir);
        Pair<Integer, Integer> newRightPos = getPosByDirection(newRightDir);
        
        double newLeftToGoal = manhattanDistance(newLeftPos, objetivo);
        double newRightToGoal = manhattanDistance(newRightPos, objetivo);
        
        return (newRightToGoal <= newLeftToGoal)? "left" : "right";
    }
    
    private Pair<Integer, Integer> getPosByDirection(Direction direction){
        for (int i = 0; i < direcciones.length; i++) {
            if (direcciones[i] == direction) {
                return casillas_adyacentes.get(i);
            }
        }
        
        return null;
    }
    
    private Direction getDirectionByOrientation(String orientation, Direction referenceDir){
        if (referenceDir == null) {
            return direcciones[indice_optima];
        }

        switch (orientation.toLowerCase()) {
            case "front":
                return referenceDir;
            case "left":
                return turnLeft(referenceDir);
            case "right":
                return turnRight(referenceDir);
            case "back":
                return opposite(referenceDir);
            default:
                throw new IllegalArgumentException("Orientación no válida: " + orientation);
        }
    }

    private void move(Direction nextDirection) {
        direction = nextDirection;
        ((AgentGrid) myAgent).energy = grid.getEnergyLeft();
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

        return !(grid.runStatus == RunStatus.ACTIVE);
    }

    @Override
    public int onEnd() {
        Main.printGUI("[GRID][RUN] Terminada con estado: " + grid.runStatus);
        myAgent.doDelete();

        return this.exitCode;
    }

}

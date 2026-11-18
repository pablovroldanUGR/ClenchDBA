package entorno;

import api.ResponseLAB;
import common.Pair;
import common.CellType;
import common.Direction;
import java.util.ArrayList;

public class EnvironmentGrid extends Environment {

    private final String ENV_TYPE = "grid.";
    private final String SENSOR_POSITION = "position";
    private final String SENSOR_BUMPERS = "bumpers";
    private final String SENSOR_TAGET_POS = "target";
    private final String SENSOR_ENERGY = "energy";
    private final String ACTION_MOVE_NORTH = "move.north";
    private final String ACTION_MOVE_EAST = "move.east";
    private final String ACTION_MOVE_WEST = "move.west";
    private final String ACTION_MOVE_SOUTH = "move.south";

    // Estado del entorno
    private Pair<Integer, Integer> position;
    private ArrayList<CellType> directions_status; // 0 = north, 1 = east, 2 = south, 3 = west
    private Pair<Integer, Integer> target_pos;
    private int energy_left;

    // Representación del entorno
    private ArrayList<ArrayList<CellType>> grid_map;

    protected EnvironmentGrid() {
        super();
        System.out.println("[GRID][EnvironmentGrid] Inicializando entorno");

        this.position = new Pair<>();
        this.directions_status = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            this.directions_status.add(CellType.UNKNOWN);
        }
        this.target_pos = new Pair<>();
        this.energy_left = -1;

        this.grid_map = new ArrayList<>();
        System.out.println("[GRID][EnvironmentGrid] Entorno inicializado");
    }

    private void updateGridMap(Pair<Integer, Integer> new_pos, CellType type) {
        System.out.println("[GRID][updateGridMap] Inicio; posición=" + new_pos + ", tipo=" + type + ", filas actuales=" + this.grid_map.size());
        
        int desiredRows = new_pos.getFirst() + 2;
        int desiredCols = new_pos.getSecond() + 2;
        
        // El mapa esta vacio
        if (this.grid_map.isEmpty()) {
            System.out.println("[GRID][updateGridMap] Creando mapa inicial");
            for (int i = 0; i <= desiredRows; i++) {
                ArrayList<CellType> newRow = new ArrayList<>();
                for (int j = 0; j < desiredCols; j++) {
                    newRow.add(CellType.UNKNOWN);
                }
                this.grid_map.add(newRow);
            }
            this.grid_map.get(new_pos.getFirst()).set(new_pos.getSecond(), type);
            System.out.println("[GRID][updateGridMap] Mapa inicial creado; fin");
            Main.updateMap(grid_map);
            return;
        }

        // Metemos filas nuevas si excede las filas
        while (desiredRows > this.grid_map.size()) {
            System.out.println("[GRID][updateGridMap] Añadiendo fila " + this.grid_map.size());
            ArrayList<CellType> newRow = new ArrayList<>();
            for (int j = 0; j < this.grid_map.get(0).size(); j++) {
                newRow.add(CellType.UNKNOWN); // Rellenamos las nuevas filas con UNKNOWN
            }
            this.grid_map.add(newRow);
        }

        // Metemos columnas nuevas si excede las columnas
        if (desiredCols > this.grid_map.get(0).size()) {
            System.out.println("[GRID][updateGridMap] Ampliando columnas hasta " + desiredCols);
            for (ArrayList<CellType> row : this.grid_map) {
                while (row.size() < desiredCols) {
                    row.add(CellType.UNKNOWN);
                }
            }
        }
        
        if(type == CellType.AGENT){
            System.out.println("[GRID][updateGridMap] Marcando agente en " + this.position);
            this.grid_map.get(this.position.getFirst()).set(this.position.getSecond(), type);
        }
        
        else if(type == CellType.GOAL){
            System.out.println("[GRID][updateGridMap] Marcando objetivo en " + this.target_pos);
            this.grid_map.get(this.target_pos.getFirst()).set(this.target_pos.getSecond(), type);
        } else {
            this.grid_map.get(new_pos.getFirst()).set(new_pos.getSecond(), type);
        }
        
        System.out.println("[GRID][updateGridMap] Enviando mapa a la GUI");
        Main.updateMap(grid_map);
        System.out.println("[GRID][updateGridMap] Actualización de la GUI terminada");
    }

    public Pair<Integer, Integer> getPos() {
        System.out.println("[GRID][getPos] Inicio; runID=" + this.runID);
        if (this.runID != -1) {
            System.out.println("[GRID][getPos] Solicitando sensor " + this.ENV_TYPE + this.SENSOR_POSITION);
            ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_POSITION);
            System.out.println("[GRID][getPos] Respuesta API recibida; null=" + (response == null));
            if (response != null) {
                System.out.println("[GRID][getPos] Datos=" + response.value + ", estado=" + response.runStatus);
                this.runStatus = response.runStatus;
                this.position.setFirst(Integer.valueOf(response.value.get(1).getSecond()) + 1);
                this.position.setSecond(Integer.valueOf(response.value.get(0).getSecond()) + 1);
                System.out.println("[GRID][getPos] Posición=" + this.position + "; actualizando mapa");
                updateGridMap(this.position, CellType.AGENT);
            } else {
               System.out.println("Algo salio mal. Response = null (Grid.getPos)");
            }
        } else {
           System.out.println("Error: RUNID = -1 (Grid.getPos)");
        }

        System.out.println("[GRID][getPos] Fin; posición=" + this.position);
        return this.position;
    }

    public ArrayList<CellType> getBumpers() {
        System.out.println("[GRID][getBumpers] Inicio; runID=" + this.runID + ", posición=" + this.position);
        if (this.runID != -1) {
            System.out.println("[GRID][getBumpers] Solicitando sensor " + this.ENV_TYPE + this.SENSOR_BUMPERS);
            ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_BUMPERS);
            System.out.println("[GRID][getBumpers] Respuesta API recibida; null=" + (response == null));
            if (response != null) {
                System.out.println("[GRID][getBumpers] Datos=" + response.value + ", estado=" + response.runStatus);
                this.runStatus = response.runStatus;
                for (int i = 0; i < this.directions_status.size(); i++) {
                    System.out.println("[GRID][getBumpers] Procesando dirección " + i);
                    CellType type = CellType.FREE;

                    if (Boolean.valueOf(response.value.get(i).getSecond())) {
                        type = CellType.WALL;
                    }

                    directions_status.set(i, type);
                    System.out.println("[GRID][getBumpers] Dirección=" + i + ", tipo=" + type + "; calculando casilla adyacente desde " + this.position);

                    int adjRow = this.position.getFirst();
                    int adjCol = this.position.getSecond();

                    switch (i) {
                        case 0:
                            adjRow -= 1;
                            break; // 0 = NORTH (Fila anterior)
                        case 1:
                            adjCol += 1;
                            break; // 1 = EAST  (Columna siguiente)
                        case 2:
                            adjRow += 1;
                            break; // 2 = SOUTH (Fila siguiente)
                        case 3:
                            adjCol -= 1;
                            break; // 3 = WEST  (Columna anterior)
                    }
                    
                    if(isValidGridPos(new Pair<>(adjRow, adjCol))){
                        System.out.println("[GRID][getBumpers] Marcando casilla (" + adjRow + ", " + adjCol + ") como " + type);
                        this.grid_map.get(adjRow).set(adjCol, type);
                    }
                }
            } else {
               System.out.println("Algo salio mal. Response = null (Grid.getBumpers)");
            }
        } else {
           System.out.println("Error: RUNID = -1 (Grid.getBumpers)");
        }

        System.out.println("[GRID][getBumpers] Fin; bumpers=" + this.directions_status);
        return this.directions_status;
    }

    public Pair<Integer, Integer> getTargetPos() {
        System.out.println("[GRID][getTargetPos] Inicio; runID=" + this.runID);
        if (this.runID != -1) {
            System.out.println("[GRID][getTargetPos] Solicitando sensor " + this.ENV_TYPE + this.SENSOR_TAGET_POS);
            ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_TAGET_POS);
            System.out.println("[GRID][getTargetPos] Respuesta API recibida; null=" + (response == null));
            if (response != null) {
                System.out.println("[GRID][getTargetPos] Datos=" + response.value + ", estado=" + response.runStatus);
                this.runStatus = response.runStatus;
                this.target_pos.setFirst(Integer.valueOf(response.value.get(1).getSecond()) + 1);
                this.target_pos.setSecond(Integer.valueOf(response.value.get(0).getSecond()) + 1);
                System.out.println("[GRID][getTargetPos] Objetivo=" + this.target_pos + "; actualizando mapa");
                updateGridMap(this.target_pos, CellType.GOAL);
            } else {
               System.out.println("Algo salio mal. Response = null (Grid.getTargetPos)");
            }
        } else {
           System.out.println("Error: RUNID = -1 (Grid.getTargetPos)");
        }

        System.out.println("[GRID][getTargetPos] Fin; objetivo=" + target_pos);
        return target_pos;
    }

    public int getEnergyLeft() {
        System.out.println("[GRID][getEnergyLeft] Inicio; runID=" + this.runID);
        if (this.runID != -1) {
            System.out.println("[GRID][getEnergyLeft] Solicitando sensor " + this.ENV_TYPE + this.SENSOR_ENERGY);
            ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_ENERGY);
            System.out.println("[GRID][getEnergyLeft] Respuesta API recibida; null=" + (response == null));
            if (response != null) {
                System.out.println("[GRID][getEnergyLeft] Datos=" + response.value + ", estado=" + response.runStatus);
                this.runStatus = response.runStatus;
                this.energy_left = Integer.valueOf(response.value.get(0).getSecond());
            } else {
               System.out.println("Algo salio mal. Response = null (Grid.getEnergyLeft)");
            }
        } else {
           System.out.println("Error: RUNID = -1 (Grid.getEnergyLeft)");
        }

        System.out.println("[GRID][getEnergyLeft] Fin; energía=" + energy_left);
        return energy_left;
    }

    public void move(Direction dir) {
        System.out.println("[GRID][move] Inicio; runID=" + this.runID + ", dirección=" + dir + ", posición=" + this.position);
        if (this.runID != -1) {
            String action = "";
            switch (dir) {
                case NORTH:
                    action = ACTION_MOVE_NORTH;
                    break;
                case EAST:
                    action = ACTION_MOVE_EAST;
                    break;
                case SOUTH:
                    action = ACTION_MOVE_SOUTH;
                    break;
                case WEST:
                    action = ACTION_MOVE_WEST;
                    break;
                default:
                   System.out.println("Opción no válida, error en (Grid.Move");
                    return;
            };

            System.out.println("[GRID][move] Solicitando " + this.ENV_TYPE + action);
            ResponseLAB response = api.executeAction(this.runID, this.ENV_TYPE + action);
            System.out.println("[GRID][move] Respuesta API recibida; null=" + (response == null));
            System.out.println("[GRID][move] Marcando posición anterior como libre: " + this.position);
            this.grid_map.get(this.position.getFirst()).set(this.position.getSecond(), CellType.FREE);
            System.out.println("[GRID][move] Consultando nueva posición");
            getPos();
            System.out.println("[GRID][move] Marcando nueva posición del agente: " + this.position);
            this.grid_map.get(this.position.getFirst()).set(this.position.getSecond(), CellType.AGENT);
            

            if (response != null) {
                System.out.println("[GRID][move] Estado recibido=" + response.runStatus + ", acción=" + response.actionStatus + ", motivo=" + response.reasonCodeAction);
                this.runStatus = response.runStatus;
            }
        } else {
           System.out.println("Error: RUNID = -1 (Grid.move)");
        }
        System.out.println("[GRID][move] Fin; posición=" + this.position + ", estado=" + this.runStatus);
    }

    public Pair<Integer, Integer> getPosGrid(Pair<Integer, Integer> pos) {
        System.out.println("[GRID][getPosGrid] Consultando posición " + pos);
        if (!isValidGridPos(pos)) {
            System.out.println("[GRID][getPosGrid] Posición no válida; devolviendo null");
            return null;
        }

        System.out.println("[GRID][getPosGrid] Posición válida: " + pos);
        return new Pair<>(pos.getFirst(), pos.getSecond());
    }
    
    private boolean isValidGridPos(Pair<Integer, Integer> pos) {
        System.out.println("[GRID][isValidGridPos] Validando " + pos);
        if (this.grid_map == null || this.grid_map.isEmpty()) {
            System.out.println("[GRID][isValidGridPos] Mapa nulo o vacío");
            return false;
        }
        if (pos.getFirst() < 0 || pos.getFirst() >= this.grid_map.size()) {
            System.out.println("[GRID][isValidGridPos] Fila fuera del mapa: " + pos.getFirst());
            return false;
        }
        if (pos.getSecond() < 0 || pos.getSecond() >= this.grid_map.get(0).size()) {
            System.out.println("[GRID][isValidGridPos] Columna fuera del mapa: " + pos.getSecond());
            return false;
        }
        System.out.println("[GRID][isValidGridPos] Posición válida");
        return true;
    }
    
    public String toString(){
        System.out.println("[GRID][EnvironmentGrid.toString] Posición=" + this.position + ", objetivo=" + this.target_pos + ", energía=" + this.energy_left);
        return "Posición Agente: [ " +  this.position.getFirst() + " ][ " + this.position.getSecond() + " ]\n" +
               "Posición Objetivo : [ " +  this.target_pos.getFirst() + " ][ " + this.target_pos.getSecond() + " ]\n" +
               "Energía: " + this.energy_left;
    }
}

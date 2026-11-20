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

        this.position = new Pair<>();
        this.directions_status = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            this.directions_status.add(CellType.UNKNOWN);
        }
        this.target_pos = new Pair<>();
        this.energy_left = -1;

        this.grid_map = new ArrayList<>();

    }

    protected void resetEnvironment() {
        this.position.clear();
        this.target_pos.clear();

        for (int i = 0; i < 4; i++) {
            this.directions_status.set(i, CellType.UNKNOWN);
        }

        this.energy_left = -1;
        grid_map.clear();
    }

    private void initGridMap(Pair<Integer, Integer> new_pos, CellType type, int rows, int cols) {

        for (int i = 0; i < rows; i++) {
            ArrayList<CellType> newRow = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                newRow.add(CellType.UNKNOWN);
            }
            this.grid_map.add(newRow);
        }

    }

    private void updateGridMap(Pair<Integer, Integer> new_pos, CellType type) {

        int desiredRows = new_pos.getFirst() + 2;  // Añade una fila y col para fuera de límites y
        int desiredCols = new_pos.getSecond() + 2; // otro para pintar el resto del mapa no explorado

        // El mapa está vacío ->
        if (this.grid_map.isEmpty()) {
            initGridMap(new_pos, type, desiredRows, desiredCols);
        }
        // Metemos filas nuevas si excede las filas
        while (desiredRows > this.grid_map.size()) {

            ArrayList<CellType> newRow = new ArrayList<>();
            for (int j = 0; j < this.grid_map.get(0).size(); j++) {
                newRow.add(CellType.UNKNOWN); // Rellenamos las nuevas filas con UNKNOWN
            }
            this.grid_map.add(newRow);
        }

        // Metemos columnas nuevas si excede las columnas
        if (desiredCols > this.grid_map.get(0).size()) {

            for (ArrayList<CellType> row : this.grid_map) {
                while (row.size() < desiredCols) {
                    row.add(CellType.UNKNOWN);
                }
            }
        }

        this.grid_map.get(new_pos.getFirst()).set(new_pos.getSecond(), type);

        Main.updateMap(grid_map);
        Main.updateInfo(this.toString());

    }

    public Pair<Integer, Integer> getPos() {
        if (this.runID != -1) {

            ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_POSITION);

            if (response != null) {

                this.runStatus = response.runStatus;
                this.position.setFirst(Integer.valueOf(response.value.get(1).getSecond()) + 1);
                this.position.setSecond(Integer.valueOf(response.value.get(0).getSecond()) + 1);

                updateGridMap(this.position, CellType.AGENT);
            }
        } else {
            Main.printGUI("[GRID][ERROR] No se puede leer la posición: no hay una run iniciada.");
        }

        return this.position;
    }

    public ArrayList<CellType> getBumpers() {

        if (this.runID != -1) {

            ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_BUMPERS);

            if (response != null) {

                this.runStatus = response.runStatus;
                for (int i = 0; i < this.directions_status.size(); i++) {

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

                    if (isValidGridPos(new Pair<>(adjRow, adjCol))) {
                        CellType type = CellType.FREE;

                        if (Boolean.valueOf(response.value.get(i).getSecond())) {
                            type = CellType.WALL;
                        } else if (this.grid_map.get(adjRow).get(adjCol) == CellType.GOAL) {
                            type = CellType.GOAL;
                        }

                        directions_status.set(i, type);

                        this.grid_map.get(adjRow).set(adjCol, type);

                        Main.updateMap(grid_map);
                        Main.updateInfo(this.toString());

                    }
                }
            }
        } else {
            Main.printGUI("[GRID][ERROR] No se pueden leer los bumpers: no hay una run iniciada.");
        }

        return this.directions_status;
    }

    public Pair<Integer, Integer> getTargetPos() {

        if (this.runID != -1) {

            ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_TAGET_POS);

            if (response != null) {

                this.runStatus = response.runStatus;
                this.target_pos.setFirst(Integer.valueOf(response.value.get(1).getSecond()) + 1);
                this.target_pos.setSecond(Integer.valueOf(response.value.get(0).getSecond()) + 1);

                updateGridMap(this.target_pos, CellType.GOAL);
            }
        } else {
            Main.printGUI("[GRID][ERROR] No se puede leer el objetivo: no hay una run iniciada.");
        }

        return target_pos;
    }

    public int getEnergyLeft() {
        if (this.runID != -1) {

            ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_ENERGY);

            if (response != null) {

                this.runStatus = response.runStatus;
                
                if(response.value.get(0).getSecond().toString().equals("FINITE")){
                    this.energy_left = Integer.valueOf(response.value.get(1).getSecond());
                }
            } else {
                System.out.println("Algo salio mal. Response = null (Grid.getEnergyLeft)");
            }
        } else {
            Main.printGUI("[GRID][ERROR] No se puede leer la energía: no hay una run iniciada.");
        }

        return energy_left;
    }

    public void move(Direction dir) {

        if (this.runID != -1) {
            String action = "";
            int addRow = 0;
            int addCol = 0;
            switch (dir) {
                case NORTH:
                    action = ACTION_MOVE_NORTH;
                    addRow = -1;
                    break;
                case EAST:
                    action = ACTION_MOVE_EAST;
                    addCol = 1;
                    break;
                case SOUTH:
                    action = ACTION_MOVE_SOUTH;
                    addRow = 1;
                    break;
                case WEST:
                    action = ACTION_MOVE_WEST;
                    addCol = -1;
                    break;
                default:
                    Main.printGUI("[GRID][ERROR] Dirección de movimiento no válida: " + dir);
                    return;
            };

            /*if (this.grid_map.get(addRow).get(addCol) == CellType.GOAL) {
                this.grid_map.get(addRow).set(addCol, CellType.AGENT);
                this.grid_map.get(this.position.getFirst()).set(addCol, CellType.FREE);
            }*/

            ResponseLAB response = api.executeAction(this.runID, this.ENV_TYPE + action);

            if (response != null) {
                this.runStatus = response.runStatus;
                /*Main.printGUI("[GRID][ACTION] El agente realiza acción " + dir
                        + " — " + response.actionStatus
                        + (response.reasonCodeAction == null || response.reasonCodeAction.isBlank()
                                ? ""
                                : " (" + response.reasonCodeAction + ")"));*/
            }

            this.grid_map.get(this.position.getFirst()).set(this.position.getSecond(), CellType.FREE);

            getPos();

            this.grid_map.get(this.position.getFirst()).set(this.position.getSecond(), CellType.AGENT);

        } else {
            Main.printGUI("[GRID][ERROR] No se puede mover el agente: no hay una run iniciada.");
        }

    }

    public Pair<Integer, Integer> getPosGrid(Pair<Integer, Integer> pos) {

        if (!isValidGridPos(pos)) {

            return null;
        }

        return new Pair<>(pos.getFirst(), pos.getSecond());
    }

    private boolean isValidGridPos(Pair<Integer, Integer> pos) {

        if (this.grid_map == null || this.grid_map.isEmpty()) {

            return false;
        }
        if (pos.getFirst() < 0 || pos.getFirst() >= this.grid_map.size()) {

            return false;
        }
        if (pos.getSecond() < 0 || pos.getSecond() >= this.grid_map.get(0).size()) {

            return false;
        }

        return true;
    }

    public String toString() {

        return "Posición Agente: [ " + this.position.getFirst() + " ][ " + this.position.getSecond() + " ]\n"
                + "Posición Objetivo : [ " + this.target_pos.getFirst() + " ][ " + this.target_pos.getSecond() + " ]\n"
                + "Energía: " + this.energy_left;
    }
}

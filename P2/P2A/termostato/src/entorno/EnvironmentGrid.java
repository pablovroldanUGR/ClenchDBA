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
    private ArrayList<CellType> directions;
    private Pair<Integer, Integer> target_pos;
    private int energy_left;

    // Representación del entorno
    private ArrayList<ArrayList<CellType>> grid_map;

    protected EnvironmentGrid() {
        super();

        this.position = new Pair<>();
        this.directions = new ArrayList<>();
        this.target_pos = new Pair<>();
        this.energy_left = -1;

        this.grid_map = new ArrayList<>();
    }

    private void updateGridMap(Pair<Integer, Integer> new_pos) {
        // El mapa esta vacio
        if (this.grid_map.isEmpty()) {
            for (int i = 0; i <= new_pos.getFirst(); i++) {
                ArrayList<CellType> newRow = new ArrayList<>();
                for (int j = 0; j <= new_pos.getSecond(); j++) {
                    newRow.add(CellType.UNKNOWN);
                }
                this.grid_map.add(newRow);
            }
            this.grid_map.get(new_pos.getFirst()).set(new_pos.getSecond(), CellType.FREE);
            return;
        }
        
        // Metemos filas nuevas si excede las filas
        while (new_pos.getFirst() >= this.grid_map.size()) {
            ArrayList<CellType> newRow = new ArrayList<>();
            for (int j = 0; j < this.grid_map.get(0).size(); j++) {
                newRow.add(CellType.UNKNOWN); // Rellenamos las nuevas filas con UNKNOWN
            }
            this.grid_map.add(newRow);
        }
        
        // Metemos columnas nuevas si excede las columnas
        if(new_pos.getSecond() >= this.grid_map.get(0).size()){
            for(ArrayList<CellType> row : this.grid_map){
                while(row.size() <= new_pos.getSecond()){
                    row.add(CellType.UNKNOWN);
                }
            }
        }
        
        // Marcar propia posición como libre si esta marcada como unknown
        if (this.grid_map.get(new_pos.getFirst()).get(new_pos.getSecond()) == CellType.UNKNOWN) {
            this.grid_map.get(new_pos.getFirst()).set(new_pos.getSecond(), CellType.FREE);
        }
    }

    public Pair<Integer, Integer> getPos() {
        if (this.runID != -1) {
            try {
                ResponseLAB response = api.getSensor(this.runID, this.ENV_TYPE + this.SENSOR_POSITION);
                this.runStatus = response.runStatus;
                this.position.setFirst(Integer.valueOf(response.value.get(0).getSecond()));
                this.position.setSecond(Integer.valueOf(response.value.get(1).getSecond()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Error al intentar obtener la temperatura objetivo. RunID no inicailizado");
        }

        return this.position;
    }
    
    public ArrayList<CellType> getBumpers(){
        return new ArrayList<>();
    }
    
    public Pair<Integer, Integer> getTargetPos(){
        return new Pair<>();
    }
    
    public int getEnergyLeft(){
        return  0;
    }
    
    public void move(Direction dir){
        
    }
}

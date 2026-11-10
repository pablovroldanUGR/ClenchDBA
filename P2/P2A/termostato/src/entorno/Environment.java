package entorno;

import common.RunStatus;
import api.ConexAPI;
import api.ResponseLAB;
import java.io.IOException;

public abstract class Environment {

    protected ConexAPI api;
    private static Environment instance;
    public RunStatus runStatus;
    protected int runID;
    protected int challengeID;

    protected Environment() {
        this.runStatus = RunStatus.UNINITALIZED;
        this.api = new ConexAPI();
        this.runID = -1;
        this.challengeID = -1;
    }

    public static Environment getInstance(String name) {
        if (instance == null) {
            if (name.equalsIgnoreCase("Thermo")) {
                instance = new EnvironmentThermo();
            } else if (name.equalsIgnoreCase("Grid")) {
                instance = new EnvironmentGrid();
            } else {
                throw new IllegalArgumentException("Error al crear el entorno. Entorno no válido");
            }
        }

        return instance;
    }

    public void setChallenge(int challengeID) {
        this.challengeID = challengeID;
    }

    public void startChallenge() {
        if (this.challengeID != -1) {
            ResponseLAB responseInfo = api.startChallenge(this.challengeID);
            if(responseInfo != null){
                this.runID = responseInfo.runId;
                this.runStatus = responseInfo.runStatus;
            }
        } else {
            System.out.println("Error al intentar iniciar el reto. Debes seleccionar uno primero");
        }
    }

    public RunStatus checkStatus() throws IOException, InterruptedException {
        if (this.runID != -1) {
            throw new IllegalStateException("No hay una run iniciada.");
        }

        ResponseLAB runInfo = api.getStatus(this.runID);

        if (runInfo == null || runInfo.runStatus == null) {
            throw new IOException("La respuesta no contiene un estado válido.");
        }

        if (runInfo.runId != -1 && runInfo.runId != this.runID) {
            throw new IOException("La API devolvió el estado de otra run.");
        }

        this.runStatus = runInfo.runStatus;
        return this.runStatus;
    }

    public RunStatus closeRun() throws IOException, InterruptedException {
        if (this.runID == -1) {
            throw new IllegalStateException("No hay una run iniciada.");
        }
        
        ResponseLAB responseInfo = api.closeChallenge(this.runID);

        if (responseInfo == null || responseInfo.runStatus == null) {
            throw new IOException("La respuesta de cierre no contiene un estado válido.");
        }

        this.runStatus = responseInfo.runStatus;

        return this.runStatus;
    }
}

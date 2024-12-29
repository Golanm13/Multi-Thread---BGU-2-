package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private int systemRuntime; // Total runtime of the system in ticks
    private int numDetectedObjects; // Cumulative count of objects detected by all cameras
    private int numTrackedObjects; // Cumulative count of objects tracked by LiDAR workers
    private int numLandmarks; // Total unique landmarks identified and mapped

    // Singleton instance
    private static final StatisticalFolder instance = new StatisticalFolder();

    // Constructor
    public StatisticalFolder() {
        this.systemRuntime = 0;
        this.numDetectedObjects = 0;
        this.numTrackedObjects = 0;
        this.numLandmarks = 0;
    }

    // Get the singleton instance
    public static StatisticalFolder getInstance() {
        return instance;
    }


    // Increment methods for updating metrics
    public synchronized void incrementSystemRuntime() {
        this.systemRuntime++;
    }

    public synchronized void incrementNumDetectedObjects(int num) {
        this.numDetectedObjects += num;
    }

    public synchronized void incrementNumTrackedObjects(int num) {
        this.numTrackedObjects += num;
    }

    public synchronized void incrementNumLandmarks() {
        this.numLandmarks++;
    }

    // Getters
    public synchronized int getSystemRuntime() {
        return systemRuntime;
    }

    public synchronized int getNumDetectedObjects() {
        return numDetectedObjects;
    }

    public synchronized int getNumTrackedObjects() {
        return numTrackedObjects;
    }

    public synchronized int getNumLandmarks() {
        return numLandmarks;
    }
}

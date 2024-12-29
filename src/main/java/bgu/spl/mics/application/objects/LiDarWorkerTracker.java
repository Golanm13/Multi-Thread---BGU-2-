package bgu.spl.mics.application.objects;
import java.util.*;


/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private final int id;
    private final int frequency;
    private final LiDarDataBase liDarDataBase;
    private final GPSIMU gpsimu;

    public LiDarWorkerTracker(int id, int frequency, LiDarDataBase liDarDataBase ,GPSIMU gpsimu) {
        this.id = id;
        this.frequency = frequency;
        this.liDarDataBase = liDarDataBase;
        this.gpsimu = gpsimu;
    }

    public int getId() {
        return id;
    }

    /**
     * Updates the tracker state and processes objects for the given tick.
     * Fetches all cloud points from the database and processes only those relevant to the current tick.
     *
     * @param tick The current simulation tick.
     * @return A list of processed TrackedObject instances.
     */
    /**
     * Updates the tracker state and processes objects for the given tick.
     * Fetches cloud points from the database and processes only those relevant to the current tick.
     *
     * @param tick The current simulation tick.
     * @return A list of processed TrackedObject instances.
     */
    public List<TrackedObject> updateTickAndProcess(int tick) {
        // Skip processing if the tick doesn't align with the frequency
        if (tick % frequency != 0) {
            return new ArrayList<>();
        }

        gpsimu.updateTick(tick); // Ensure GPSIMU is updated to the current tick
        Pose poseAtT = gpsimu.getPoseAtTime(tick);
        if (poseAtT == null) {
            throw new IllegalStateException("Pose not available for tick " + tick);
        }

        List<StampedCloudPoints> allCloudPoints = liDarDataBase.getStampedCloudPoints();
        List<TrackedObject> trackedObjects = new ArrayList<>();

        for (StampedCloudPoints stampedCloudPoints : allCloudPoints) {
            if (stampedCloudPoints.getTime() == tick) {
                List<List<Double>> transformedPoints = transformCloudPoints(stampedCloudPoints.getCloudPoints(), poseAtT);
                TrackedObject trackedObject = new TrackedObject(
                        stampedCloudPoints.getId(),
                        tick,
                        "Transformed object from LiDAR",
                        transformedPoints);
                trackedObjects.add(trackedObject);
            }
        }

        return trackedObjects;
    }
    /**
     * Transforms cloud points into the global frame using the robot's pose.
     *
     * @param cloudPoints The list of cloud points in the local frame.
     * @param pose        The robot's pose at the detection timestamp.
     * @return A list of transformed cloud points in the global frame.
     */
    private List<List<Double>> transformCloudPoints(List<List<Double>> cloudPoints, Pose pose) {
        double[][] rotationMatrix = pose.getRotationMatrix();
        double[] translationVector = {pose.getX(), pose.getY(), 0}; // Assuming 2D pose for simplicity

        List<List<Double>> transformedPoints = new ArrayList<>();
        for (List<Double> point : cloudPoints) {
            List<Double> transformedPoint = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                double value = translationVector[i];
                for (int j = 0; j < 3; j++) {
                    value += rotationMatrix[i][j] * point.get(j);
                }
                transformedPoint.add(value);
            }
            transformedPoints.add(transformedPoint);
        }

        return transformedPoints;
    }

    /**
     * Creates a new TrackedObject based on the given stamped cloud points and current tick.
     *
     * @param stampedCloudPoints The stamped cloud points to process.
     * @param tick               The current simulation tick.
     * @return A new TrackedObject instance.
     */
    private TrackedObject createTrackedObject(StampedCloudPoints stampedCloudPoints, int tick) {
        return new TrackedObject(
                stampedCloudPoints.getId(),          // Object ID
                tick,                                // Time (current tick)
                "Processed object from LiDAR",       // Description
                stampedCloudPoints.getCloudPoints());// Cloud points (coordinates)
    }

    /**
     * Updates the statistical folder with the number of tracked objects for the current tick.
     *
     * @param tick            The current simulation tick.
     * @param trackedObjects  The list of tracked objects for the tick.
     */
    public void updateStatisticalFolder(int tick, List<TrackedObject> trackedObjects) {
        int numTrackedObjects = trackedObjects.size();
        StatisticalFolder.getInstance().incrementNumTrackedObjects(numTrackedObjects);

        // Log the update
        System.out.println("LiDAR Worker " + id +
                " updated statistical folder at tick " + tick +
                " with " + numTrackedObjects + " tracked objects.");
    }
}
package bgu.spl.mics.application.objects;
import java.util.*;


public class FusionSlam {
    private final List<LandMark> landmarks;
    private final List<Pose> poses;

    private FusionSlam() {
        this.landmarks = new ArrayList<>();
        this.poses = new ArrayList<>();
    }

    private static class FusionSlamHolder {
        private static final FusionSlam INSTANCE = new FusionSlam();
    }

    public static FusionSlam    getInstance() {
        return FusionSlamHolder.INSTANCE;
    }

    public synchronized void addLandmark(LandMark landmark) {
        landmarks.add(landmark);
        StatisticalFolder.getInstance().incrementNumLandmarks();
    }

    public synchronized void addPose(Pose pose) {
        poses.add(pose);
    }

    public synchronized void updateWithTrackedObjects(List<TrackedObject> trackedObjects) {
        for (TrackedObject trackedObject : trackedObjects) {
            LandMark existingLandmark = findLandmarkById(trackedObject.getId());
            if (existingLandmark != null) {
                // Refine existing landmark
                List<List<Double>> updatedCoordinates = averageCoordinates(existingLandmark.getCoordinates(), trackedObject.getCoordinates());
                existingLandmark.setCoordinates(updatedCoordinates);
            } else {
                // Create new landmark
                LandMark newLandmark = new LandMark(
                        trackedObject.getId(),
                        trackedObject.getDescription(),
                        trackedObject.getCoordinates()
                );
                addLandmark(newLandmark);
            }
        }
    }

    private LandMark findLandmarkById(String id) {
        for (LandMark landmark : landmarks) {
            if (landmark.getId().equals(id)) {
                return landmark;
            }
        }
        return null;
    }

    private List<List<Double>> averageCoordinates(List<List<Double>> existing, List<List<Double>> newCoordinates) {
        List<List<Double>> averaged = new ArrayList<>();
        for (int i = 0; i < existing.size(); i++) {
            List<Double> existingPoint = existing.get(i);
            List<Double> newPoint = (i < newCoordinates.size()) ? newCoordinates.get(i) : existingPoint;
            List<Double> averagedPoint = Arrays.asList(
                    (existingPoint.get(0) + newPoint.get(0)) / 2,
                    (existingPoint.get(1) + newPoint.get(1)) / 2
            );
            averaged.add(averagedPoint);
        }
        return averaged;
    }

    public synchronized void performPeriodicUpdate(int currentTick) {
        System.out.println("Periodic Update at tick: " + currentTick);

        // Transform tracked objects into global coordinates based on the most recent pose
        if (!poses.isEmpty()) {
            Pose latestPose = poses.get(poses.size() - 1);
            for (LandMark landmark : landmarks) {
                List<List<Double>> transformedCoordinates = new ArrayList<>();
                for (List<Double> point : landmark.getCoordinates()) {
                    double x = point.get(0) * Math.cos(latestPose.getYaw()) - point.get(1) * Math.sin(latestPose.getYaw()) + latestPose.getX();
                    double y = point.get(0) * Math.sin(latestPose.getYaw()) + point.get(1) * Math.cos(latestPose.getYaw()) + latestPose.getY();
                    transformedCoordinates.add(Arrays.asList(x, y));
                }
                landmark.setCoordinates(transformedCoordinates);
            }
        }
    }

    public synchronized List<LandMark> getLandmarks() {
        return new ArrayList<>(landmarks);
    }

    public synchronized List<Pose> getPoses() {
        return new ArrayList<>(poses);
    }
}


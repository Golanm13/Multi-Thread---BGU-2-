package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {

    // List of cloud points for each object per time
    private List<StampedCloudPoints> stampedCloudPoints;

    // Singleton instance
    private static LiDarDataBase instance = null;

    // Private constructor to prevent instantiation
    private LiDarDataBase() {
        stampedCloudPoints = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file (currently unused but reserved for future use).
     * @return The singleton instance of LiDarDataBase.
     */
    public static synchronized LiDarDataBase getInstance(String filePath) {
        if (instance == null) {
            instance = new LiDarDataBase();
        }
        return instance;
    }

    /**
     * Adds a new stamped cloud point to the database.
     *
     * @param stampedCloudPoint The stamped cloud point to add.
     */
    public synchronized void addStampedCloudPoint(StampedCloudPoints stampedCloudPoint) {
        stampedCloudPoints.add(stampedCloudPoint);
    }

    /**
     * Retrieves all stamped cloud points in the database.
     *
     * @return A list of all stamped cloud points.
     */
    public synchronized List<StampedCloudPoints> getStampedCloudPoints() {
        return new ArrayList<>(stampedCloudPoints); // Return a copy to ensure thread safety
    }

    /**
     * Clears all cloud points in the database.
     * This can be used for resetting the system or testing purposes.
     */
    public synchronized void clearCloudPoints() {
        stampedCloudPoints.clear();
    }
}


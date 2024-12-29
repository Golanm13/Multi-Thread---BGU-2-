package bgu.spl.mics.application.objects;
import java.util.List;
/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private int time;
    private List<CloudPoint.DetectedObject> detectedObjects;

    public StampedDetectedObjects(int time, List<CloudPoint.DetectedObject> detectedObjects) {
        this.time = time;
        this.detectedObjects = detectedObjects;
    }

    // Getters and Setters
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public List<CloudPoint.DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

    public void setDetectedObjects(List<CloudPoint.DetectedObject> detectedObjects) {
        this.detectedObjects = detectedObjects;
    }
    public int size() {
        return detectedObjects.size();
        }
    }

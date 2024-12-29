package bgu.spl.mics.application.objects;
import bgu.spl.mics.application.messages.*;
import java.util.*;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private int id;
    private int frequency;
    private STATUS status;
    private List<StampedDetectedObjects> detectedObjectsList;

    public Camera(int id, int frequency, STATUS status , List<StampedDetectedObjects> detectedObjectsList) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.detectedObjectsList = detectedObjectsList;
    }



    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public List<StampedDetectedObjects> getDetectedObjectsList() {
        return detectedObjectsList;
    }

    public void setDetectedObjectsList(List<StampedDetectedObjects> detectedObjectsList) {
        this.detectedObjectsList = detectedObjectsList;
    }


    /**
     * Retrieves a list of detected object events for a specific tick.
     *
     * @param currentTick The tick for which to retrieve detected object events.
     * @return A list of DetectObjectsEvent instances corresponding to the objects detected at the specified tick.
     */
    public List<DetectObjectsEvent> getDetectedEventsForTick(int currentTick) {
        List<DetectObjectsEvent> events = new ArrayList<>();
        for (StampedDetectedObjects stamped : detectedObjectsList) {
            if (stamped.getTime() == currentTick) {
                events.add(new DetectObjectsEvent(stamped));
            }
        }
        return events;
    }

    /**
     * Updates the StatisticalFolder with the number of detected objects.
     *
     * @param event The DetectObjectsEvent to use for updating statistics.
     */
    public void UpdateStatisticalFolder(DetectObjectsEvent event , int tick) {
        int num = event.getDetectedObjects().size();
        StatisticalFolder.getInstance().incrementNumDetectedObjects(num);

        // Log the update
        System.out.println("camera-" + id +
                " updated statistical folder at tick " + tick + " with "
                + num + " stampedDetectedObjects objects.");
    }
}


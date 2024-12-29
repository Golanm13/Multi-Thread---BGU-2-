package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.*;

import java.util.List;

public class DetectObjectsEvent implements Event<List<CloudPoint.DetectedObject>> {
    /**
     * TrackedObjectsEvent is sent by a LiDAR worker to notify
     * the Fusion-SLAM service about tracked objects.
     */
    private final StampedDetectedObjects stampedDetectedObjects;// The list of detected objects

    /**
     * Constructor for DetectObjectsEvent.
     *
     * @param stampedDetectedObjects The list of objects detected by the camera.
     */
    public DetectObjectsEvent(StampedDetectedObjects stampedDetectedObjects) {
        this.stampedDetectedObjects = stampedDetectedObjects;
    }

    public StampedDetectedObjects getDetectedObjects() {
        return stampedDetectedObjects;
    }
}
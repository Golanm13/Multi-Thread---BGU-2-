package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import java.util.*;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 *
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService-" + camera.getId());
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        // Subscribe to TickBroadcast to detect objects periodically
        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            int currentTick = tickBroadcast.getTick();

            // Fetch events for the current tick from the Camera
            List<DetectObjectsEvent> events = camera.getDetectedEventsForTick(currentTick);

            // Send each event
            for (DetectObjectsEvent event : events) {
                sendEvent(event);
                camera.UpdateStatisticalFolder(event, currentTick);
            }
        });

        // Subscribe to TerminatedBroadcast to shut down gracefully
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {
            System.out.println(getName() + " received TerminatedBroadcast. Terminating...");
            terminate();
        });
        // Subscribe to CrashedBroadcast for error handling
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            terminate(); // Stop the service if another sensor fails
        });
    }
}

package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.*;

import java.util.*;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 *
 * This service interacts with the LiDarTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private LiDarWorkerTracker liDarTracker;
    /**
     * Constructor for LiDarService.
     *
     * @param liDarTracker The LiDAR tracker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker liDarTracker) {
        super("LiDarWorkerService-" + liDarTracker.getId());
        this.liDarTracker = liDarTracker;
    }


    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */

    @Override
    protected void initialize() {
        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            int currentTick = tickBroadcast.getTick();

            // Delegate processing and transformation to LiDarWorkerTracker
            List<TrackedObject> transformedObjects = liDarTracker.updateTickAndProcess(currentTick);

            if (!transformedObjects.isEmpty()) {
                // Send a TrackedObjectsEvent with the transformed objects
                sendEvent(new TrackedObjectsEvent(transformedObjects));
                liDarTracker.updateStatisticalFolder(currentTick ,transformedObjects);
            }
        });


        // Subscribe to TerminatedBroadcast for graceful shutdown
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> terminate());

        // Subscribe to CrashedBroadcast for error handling
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> terminate());
    }
}

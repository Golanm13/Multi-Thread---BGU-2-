package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.Message.*;

import java.util.List;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam fusionSlam;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        // Handle pose updates
        subscribeEvent(PoseEvent.class, event -> {
            Pose pose = event.getPose();
            fusionSlam.addPose(pose);
            complete(event, true);
        });

        // Handle tracked objects
        subscribeEvent(TrackedObjectsEvent.class, event -> {
            List<TrackedObject> trackedObjects = event.getTrackedObjects();
            fusionSlam.updateWithTrackedObjects(trackedObjects); 
            complete(event, true);
        });

        // Handle periodic updates
        subscribeBroadcast(TickBroadcast.class, broadcast -> {
            fusionSlam.performPeriodicUpdate(broadcast.getTick());
        });

        // Handle system termination
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {
            terminate();
        });

        // Handle system crashes
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            terminate();
        });
    }
}
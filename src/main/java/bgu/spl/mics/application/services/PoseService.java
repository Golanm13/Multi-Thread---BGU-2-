package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;

import bgu.spl.mics.application.messages.*;



/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private final GPSIMU gpsimu;

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        // Handle tick broadcasts to update and send pose
        subscribeBroadcast(TickBroadcast.class, tick -> {
            gpsimu.updateTick(tick.getTick());
            Pose currentPose = gpsimu.getPoseAtTime(tick.getTick());
            if (currentPose != null) {
                sendEvent(new PoseEvent(currentPose));
            }
        });

        // Handle service termination
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {
            terminate();
        });

        // Handle system crashes as required
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            terminate();
        });
    }
}
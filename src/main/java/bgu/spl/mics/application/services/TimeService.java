package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    private final int TickTime; // Duration of each tick in milliseconds
    private final int Duration; // Total number of ticks before termination
    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.TickTime = TickTime;
        this.Duration = Duration;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        new Thread(() -> {
            try {
                for (int currentTick = 1; currentTick <= Duration; currentTick++) {
                    // Log the current tick or send an update as needed
                    System.out.println("Tick: " + currentTick);
                    sendBroadcast(new TickBroadcast(currentTick));
                    // Sleep for the duration of the tick
                    Thread.sleep(TickTime);
                }

                // Log termination message or signal termination directly
                System.out.println("TimeService has completed all ticks and is terminating.");
                terminate();

            } catch (InterruptedException e) {
                // Handle interruption gracefully
                Thread.currentThread().interrupt();
                terminate();
            }
            // Subscribe to CrashedBroadcast for error handling
            subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
                terminate(); // Stop the service if another sensor fails
            });
        }).start();

    }
}

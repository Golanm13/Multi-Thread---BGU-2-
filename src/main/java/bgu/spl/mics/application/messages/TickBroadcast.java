package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * TickBroadcast is sent by the TimeService to notify
 * all services of the current tick in the simulation.
 */
public class TickBroadcast implements Broadcast {
    private final int tick;

    public TickBroadcast(int tick) {
        this.tick = tick;
    }

    public int getTick() {
        return tick;
    }
}
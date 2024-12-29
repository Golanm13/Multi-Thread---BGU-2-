package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * CrashedBroadcast is sent by a sensor or service
 * to notify others of a system crash.
 */
public class CrashedBroadcast implements Broadcast {
    private final String errorMessage;

    public CrashedBroadcast(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

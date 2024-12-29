package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    // Fields as specified in the assignment
    private int currentTick;
    private STATUS status;
    private List<Pose> poseList;

    // Constructor
    public GPSIMU() {
        this.currentTick = 0;
        this.status = STATUS.UP;
        this.poseList = new ArrayList<>();
    }

    // Getters and setters
    public int getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public List<Pose> getPoseList() {
        return poseList;
    }

    // Method to add a new pose
    public void addPose(Pose pose) {
        poseList.add(pose);
    }

    // Method to get pose at specific time
    public Pose getPoseAtTime(int time) {
        for (Pose pose : poseList) {
            if (pose.getTime() == time) {
                return pose;
            }
        }
        return null;
    }

    // Method to check if sensor is operational
    public boolean isOperational() {
        return status == STATUS.UP;
    }

    // Method to update current tick and check for errors
    public void updateTick(int newTick) {
        this.currentTick = newTick;
        // Check if we have pose data for this tick
        if (getPoseAtTime(newTick) == null && isOperational()) {
            setStatus(STATUS.ERROR);
        }
    }

    // Method to clear all poses (useful for cleanup)
    public void clearPoses() {
        poseList.clear();
    }
}

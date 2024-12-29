package bgu.spl.mics.application.objects;

/**
 * Represents the robot's pose (position and orientation) in the environment.
 * Includes x, y coordinates and the yaw angle relative to a global coordinate system.
 */
public class Pose {
    private float x;   // X-coordinate of the pose.
    private float y;   // Y-coordinate of the pose.
    private float yaw; // Orientation angle relative to the coordinate system.
    private int time;  // The time when the robot reaches the pose.

    // Constructor
    public Pose(float x, float y, float yaw, int time) {
        this.x = x;
        this.y = y;
        this.yaw = yaw;
        this.time = time;
    }

    // Getters and Setters
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    /**
     * Calculates and retrieves the 3x3 rotation matrix based on the current yaw of the pose.
     * The yaw is converted from degrees to radians, and the matrix is configured for
     * rotation about the z-axis in a 2D plane.
     *
     * @return A 2D array representing the 3x3 rotation matrix. The first two rows
     *         define the rotation transformation in the x-y plane, while the third
     *         row remains fixed for the z-axis.
     */
    public double[][] getRotationMatrix() {
        // Convert yaw from degrees to radians
        double radians = Math.toRadians(yaw);

        // Calculate cosine and sine of the yaw angle
        double cosYaw = Math.cos(radians);
        double sinYaw = Math.sin(radians);

        // Return the rotation matrix
        return new double[][]{
                {cosYaw, -sinYaw, 0},  // First row
                {sinYaw, cosYaw, 0},   // Second row
                {0, 0, 1}              // Third row (no change in z)
        };
    }
}




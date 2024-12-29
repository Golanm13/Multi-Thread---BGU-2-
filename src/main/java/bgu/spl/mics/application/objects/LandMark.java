package bgu.spl.mics.application.objects;

import java.util.*;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private String id; // The internal ID of the landmark.
    private String description; // The description of the landmark.
    private List<List<Double>> coordinates;// List of coordinates of the object.

    // Constructor
    public LandMark(String id, String description, List<List<Double>> coordinates) {
        this.id = id;
        this.description = description;
        this.coordinates = coordinates;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }
}


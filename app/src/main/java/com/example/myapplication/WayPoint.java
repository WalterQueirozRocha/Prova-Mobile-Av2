package com.example.myapplication;

public class WayPoint {
    private int id;
    private double latitude;
    private double longitude;
    private String startDate;
    private double avgSpeed;
    private double totalDistance;
    private long duration;


    public WayPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public WayPoint(int id, String startDate, double avgSpeed, double distance, long duration) {
        this.id = id;
        this.startDate = startDate;
        this.avgSpeed = avgSpeed;
        this.totalDistance = distance;
        this.duration = duration;
    }

    public int getId() { return id; }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public String getStartDate() { return startDate; }
    public double getAvgSpeed() { return avgSpeed; }
    public double getTotalDistance() { return totalDistance; }
    public long getDuration() { return duration; }
}
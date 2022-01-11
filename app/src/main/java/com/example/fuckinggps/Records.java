package com.example.fuckinggps;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "runner_tracker")
public class Records {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    private Double Longitude;

    private Double Latitude;

    private Float distance;

    private Float speed;

    private int duration;

    private long time;

    private float rating;


    public Records(Double Longitude,Double Latitude,Float distance,Float speed,int duration, long time, float rating) {
        this.Latitude=Latitude;
        this.Longitude=Longitude;
        this.distance=distance;
        this.speed=speed;
        this.duration=duration;
        this.time = time;
        this.rating = rating;
    }

    public Records() {

    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public void setSpeed(Float speed) {
        this.speed=speed;
    }



    public int getDuration() {
        return duration;
    }



    public Double getLongitude() {
        return Longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public Float getSpeed() {
        return speed;
    }

    public Float getDistance() {
        return distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Records{" +
                "id=" + id +
                ", Longitude=" + Longitude +
                ", Latitude=" + Latitude +
                ", distance=" + distance +
                ", speed=" + speed +
                ", duration=" + duration +
                ", time=" + time +
                '}';
    }
}

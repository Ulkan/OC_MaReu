package com.ulkan.meetingapp.model;

public class Room {

    private final long id;
    private final String name;
    private final int color;


    public String getName() {
        return name;
    }

    public long getId() { return id; }

    public int getColor() { return color;}

    public Room(String name, long id, int color){
        this.name = name;
        this.id = id;
        this.color = color;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

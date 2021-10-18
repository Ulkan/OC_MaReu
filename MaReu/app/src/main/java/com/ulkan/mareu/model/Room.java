package com.ulkan.mareu.model;

public class Room {
    private final String name;
    private final String location;

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public Room(String name, String location){
        this.name = name;
        this.location = location;
    }

}

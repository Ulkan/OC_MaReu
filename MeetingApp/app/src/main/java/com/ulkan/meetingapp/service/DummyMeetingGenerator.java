package com.ulkan.meetingapp.service;


import com.ulkan.meetingapp.model.Meeting;
import com.ulkan.meetingapp.model.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class DummyMeetingGenerator {

    public static List<Meeting> DUMMY_MEETINGS = Arrays.asList(
            //new Meeting((long) 1, new Room("Black room", 2, 0xFFDD0000), new Date(2021 - 1900, 11, 25), "Develop photograph", Arrays.asList("YABertrand@gmail.com", "NickUt@gmail.com", "SteveMcCurry@gmail.com")),
            //new Meeting((long) 2, new Room("Library", 4, 0xFFAA8855), new Date(2021 - 1900, 8, 2), "Speak about Literature", Arrays.asList("TPratchett@gmail.com", "JButcher@gmail.com", "RHobb@gmail.com", "DEddings@gmail.com"))
    );

    public static List<Room> DUMMY_ROOMS = Arrays.asList(
            new Room("Oval office",  1, 0xFFDDDDDD ),
            new Room("Black room",  2, 0xFFDD0000),
            new Room("Round table",  3, 0xFF22DD22),
            new Room("Library",  4, 0xFFDDCC55)
    );

    public static List<Meeting> generateMeetings() {
        return new ArrayList<>(DUMMY_MEETINGS);
    }

    public static List<Room> generateRooms() {
        return new ArrayList<>(DUMMY_ROOMS);
    }
}

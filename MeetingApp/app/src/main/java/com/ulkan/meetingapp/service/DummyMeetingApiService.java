package com.ulkan.meetingapp.service;


import com.ulkan.meetingapp.model.Meeting;
import com.ulkan.meetingapp.model.Room;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DummyMeetingApiService implements MeetingApiService{

    private final List<Meeting> meetings = DummyMeetingGenerator.generateMeetings();
    private final List<Room> rooms = DummyMeetingGenerator.generateRooms();

    List<Meeting> temporaryList = new ArrayList<>();

    @Override
    public List<Meeting> getMeetings() {
        return meetings;
    }

    @Override
    public List<Meeting> filterMeetingsByRoom(Room room) {
        ArrayList<Meeting> toAdd = new ArrayList<>();
        for (Meeting meeting : meetings) {
            if (meeting.getRoom().getId() == room.getId()) {
                toAdd.add(meeting);
            }
        }
        temporaryList.addAll(toAdd);
        return temporaryList;
    }

    @Override
    public void clearTemp() {
        temporaryList.clear();
    }

    @Override
    public List<Meeting> filterMeetingsByDate(Date date) {
        Date thresholdDate = (Date) date.clone();
        thresholdDate.setHours(23);
        thresholdDate.setMinutes(59);
        ArrayList<Meeting> toRemove = new ArrayList<>();
        for (Meeting meeting : temporaryList) {
            if (!((meeting.getDate().after(date) || meeting.getDate().equals(date)) && meeting.getDate().before(thresholdDate))) {
                toRemove.add(meeting);
            }
        }
        temporaryList.removeAll(toRemove);
        return temporaryList;
    }

    @Override
    public void removeFilter() {
        temporaryList.clear();
        temporaryList.addAll(meetings);
    }

    @Override
    public List<Room> getRooms() {
        return rooms;
    }

    @Override
    public Meeting getMeetingById(long id) {
        Meeting toReturn = null;
        for (Meeting meeting : meetings) {
            if (meeting.getId() == id) {
                toReturn = meeting;
            }
        }
        return toReturn;
    }

    @Override
    public Room getRoomByName(String name) {
        Room ret = null;
        for (Room room : rooms) {
            if (room.getName().equals(name)) {
                ret = room;
            }
        }
        return ret;
    }

    @Override
    public void deleteMeeting(Meeting meeting) {
        meetings.remove(meeting);
        temporaryList.remove(meeting);
    }

    @Override
    public void createMeeting(Meeting meeting) {
        meetings.add(meeting);
    }
}

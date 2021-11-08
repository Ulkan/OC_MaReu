package com.ulkan.meetingapp.service;


import com.ulkan.meetingapp.model.Meeting;
import com.ulkan.meetingapp.model.Room;

import java.util.Date;
import java.util.List;


/**
 * Meeting API service
 **/
public interface MeetingApiService {

    /**
     * Get all my Meetings
     * @return {@link List}
     */
    List<Meeting> getMeetings();

    /**
     * Get all meetings for a room
     * @return {@link List}
     */
    List<Meeting> filterMeetingsByRoom(Room room);

    void clearTemp();

    /**
     * Get all meetings at a date
     * @return {@link List}
     */
    List<Meeting> filterMeetingsByDate(Date date);

    /**
     * Get all tjhe rooms
     * @return {@link List}
     */
    List<Room> getRooms();

    /**
     * Return specified meeting
     * @param id
     * @return {@link Meeting}
     */
    Meeting getMeetingById(long id);

    /**
     * Return specified meeting
     * @param name
     * @return {@link Room}
     */
    Room getRoomByName(String name);

    /**
     * Remove filters
     * @return {@link Room}
     */
    void removeFilter();

    /**
     * Deletes a meeting
     * @param meeting
     */
    void deleteMeeting(Meeting meeting);

    /**
     * Create a meeting
     * @param meeting
     */
    void createMeeting(Meeting meeting);

}

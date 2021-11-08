package com.ulkan.meetingapp.model;


import java.util.Date;
import java.util.List;

public class Meeting {

    /** Identifier */
    private long id;
    /** Room **/
    private Room room;
    /** Date **/
    private Date date;
    /** Subject **/
    private String subject;
    /** Participants **/
    private List<String> participants;

    public long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public Meeting(Long id, Room room, Date date, String subject, List<String> participants) {
        this.id = id;
        this.room = room;
        this.date = date;
        this.subject = subject;
        this.participants = participants;
    }

    public Meeting() {
    }
}

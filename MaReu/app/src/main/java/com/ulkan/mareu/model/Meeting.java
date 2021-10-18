package com.ulkan.mareu.model;

import java.util.Date;
import java.util.List;

public class Meeting {
    private final Room room;
    private final Date date;
    private final String subject;
    private final List<String> participants;

    public Room getRoom() {
        return room;
    }

    public Date getDate() {
        return date;
    }

    public String getSubject() {
        return subject;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public Meeting(Room room, Date date, String subject, List<String> participants) {
        this.room = room;
        this.date = date;
        this.subject = subject;
        this.participants = participants;
    }
}

package com.ulkan.meetingapp;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import com.ulkan.meetingapp.DI.DI;
import com.ulkan.meetingapp.model.Meeting;
import com.ulkan.meetingapp.model.Room;
import com.ulkan.meetingapp.service.MeetingApiService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(JUnit4.class)
public class UnitTests {

    private MeetingApiService service;

    @Before
    public void setup() {
        service = DI.getNewInstanceApiService();
    }

    @Test
    public void canCreateMeeting() {
        long id = 42;
        Room room = service.getRooms().get(1);
        Date date = new Date(82, 8,1);
        String subject = "La vie, l'univers et le reste";
        List<String> participants = Arrays.asList(
                "ArthurDent@gmail.com",
                "FordPrefect@gmail.com",
                "Trillian@gmail.com",
                "ZaphoodBeeblebrox@gmail.com"
        );
        Meeting testMeeting = new Meeting(id, room, date, subject, participants);
        assertThat(testMeeting.getId(), is(id));
        assertThat(testMeeting.getRoom(), is(room));
        assertThat(testMeeting.getDate(), is(date));
        assertThat(testMeeting.getSubject(), is(subject));
        assertThat(testMeeting.getParticipants(), is(participants));

    }

    @Test
    public void editMeeting() {
        Meeting christmas = new Meeting((long)25, service.getRooms().get(0), new Date (121, 11, 25), "Christmas", Arrays.asList("santa@klaus.com", "rudolph@deer.com"));
        christmas.setId((long)31);
        christmas.setRoom(service.getRooms().get(2));
        christmas.setDate(new Date(121,11,31));
        christmas.setSubject("New Year Eve");
        christmas.setParticipants(Arrays.asList("kiss@gmail.com","Alcohol@gmail.com"));

        assertNotSame((long)25, christmas.getId());
        assertNotSame(service.getRooms().get(0), christmas.getRoom());
        assertNotSame(new Date(121, 11, 25), christmas.getDate());
        assertNotSame("Christmas", christmas.getSubject());
        assertNotSame("santa@klaus.com", christmas.getParticipants().get(0));
        assertNotSame("rudolph@deer.com", christmas.getParticipants().get(1));

        assertSame((long)31, christmas.getId());
        assertSame(service.getRooms().get(2), christmas.getRoom());
        assertSame(31, christmas.getDate().getDate());
        assertSame("New Year Eve", christmas.getSubject());
        assertSame("kiss@gmail.com", christmas.getParticipants().get(0));
        assertSame("Alcohol@gmail.com", christmas.getParticipants().get(1));
    }

    @Test
    public void createRoom() {
        Room room = new Room("Ball Room", (long)8, 0xFF0000CC);
        assertThat(room.getName(), is("Ball Room"));
        assertThat(room.getId(), is((long)8));
        assertThat(room.getColor(), is(0xFF0000CC));
        assertSame(room.toString(), room.getName());
    }

    @Test
    public void testMeetingApiService() {
        assertSame(0, service.getMeetings().size());                            // empty at first

        long id = 42;
        Room room = service.getRooms().get(1);
        Date date = new Date(82, 8,1);
        String subject = "La vie, l'univers et le reste";
        List<String> participants = Arrays.asList(
                "ArthurDent@gmail.com",
                "FordPrefect@gmail.com",
                "Trillian@gmail.com",
                "ZaphoodBeeblebrox@gmail.com"
        );
        Meeting testMeeting = new Meeting(id, room, date, subject, participants);
        service.createMeeting(testMeeting);
        assertSame(1, service.getMeetings().size());                            // added correctly
        assertSame((long)42, service.getMeetings().get(0).getId());

        service.createMeeting(new Meeting((long)4,
                service.getRooms().get(3),
                new Date(100, 6, 8),
                "Goblet of Fire",
                Arrays.asList("Harry@potter.com", "Ron@weasley.com", "Hermione@granger.com")));
        assertSame(2, service.getMeetings().size());

        assertSame("Goblet of Fire", service.getMeetingById(4).getSubject());               // test getmeetingbyId
        assertSame("La vie, l'univers et le reste", service.getMeetingById(42).getSubject());
        assertSame(null, service.getMeetingById(1));

        for (Room r : service.getRooms()) {                                              // test filter by room
            switch (r.getName()) {
                case "Black room" :
                case "Library" :
                    service.clearTemp();
                    assertSame(1, service.filterMeetingsByRoom(r).size());
                    break;
                default:
                    service.clearTemp();
                    assertSame(0, service.filterMeetingsByRoom(r).size());
                    break;
            }
        }

        assertSame(4, service.getRooms().size());                               // test get rooms

        service.removeFilter();                                                                                         // test filter by date
        assertSame(1, service.filterMeetingsByDate(new Date(100,6,8)).size());
        assertSame("Goblet of Fire", service.filterMeetingsByDate(new Date(100,6,8)).get(0).getSubject());
        service.removeFilter();
        assertSame(1, service.filterMeetingsByDate(new Date(82,8,1)).size());
        assertSame((long)42, service.filterMeetingsByDate(new Date(82,8,1)).get(0).getId());
        service.removeFilter();
        assertSame(0, service.filterMeetingsByDate(new Date(121,11,25)).size());

        service.deleteMeeting(testMeeting);
        assertSame(1, service.getMeetings().size());
        assertSame((long)4, service.getMeetings().get(0).getId());
    }
}

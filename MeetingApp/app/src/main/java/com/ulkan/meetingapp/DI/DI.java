package com.ulkan.meetingapp.DI;

import com.ulkan.meetingapp.service.DummyMeetingApiService;
import com.ulkan.meetingapp.service.MeetingApiService;

/**
 * Dependency injector to get instance of services
 */
public class DI {

    private static final MeetingApiService service = new DummyMeetingApiService();

    /**
     * Get an instance on @{@link MeetingApiService}
     * @return
     */
    public static MeetingApiService getMeetingApiService() {
        return service;
    }

    /**
     * Get always a new instance on @{@link MeetingApiService}. Useful for tests, so we ensure the context is clean.
     * @return
     */
    public static MeetingApiService getNewInstanceApiService() {
        return new DummyMeetingApiService();
    }
}
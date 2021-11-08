package com.ulkan.meetingapp.ui;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ulkan.meetingapp.utils.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNull.notNullValue;

import android.view.KeyEvent;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.ulkan.meetingapp.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

/**
 * Test class for instrumented tests
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule(MainActivity.class);

    @Before
    public void setUp() {
        MainActivity activity = mActivityRule.getActivity();
        assertThat(activity, notNullValue());
        Intents.init();
    }

    @After
    public void cleanUp() {
        Intents.release();
    }

    /**
     * We ensure that when opening the application the list is empty
     */
    @Test
    public void isEmptyAtStart() {
        onView(Matchers.allOf(ViewMatchers.withId(R.id.myRecyclerView), hasFocus() )).check(withItemCount(0));
    }

    /**
     * We ensure that we can add a meeting and that the shown information are right
     */
    @Test
    public void canAddMeeting() {
        addMeetingTest("Test subject", Arrays.asList(2021,12,31,20,0), "Round table", Arrays.asList("camille@test.fr", "OC@test.fr"));

        onView(allOf(ViewMatchers.withId(R.id.myRecyclerView), isDisplayed())).check(withItemCount(1));
        onView(allOf(withId(R.id.item_title), isDisplayed())).check((matches(withText("Round table - 31/12/2021 20h00 - Test subject"))));
        onView(allOf(withId(R.id.item_participant), isDisplayed())).check((matches(withText("camille@test.fr, OC@test.fr"))));

        onView(allOf(ViewMatchers.withId(R.id.item_list_delete_button), isDisplayed())).perform(click());
    }

    private void addMeetingTest(String subject, List<Integer> dateInts, String room, List<String> mails) {
        onView(allOf(ViewMatchers.withId(R.id.addMeeting))).perform(click());
        onView(allOf(ViewMatchers.withId(R.id.meeting_subject_edit_text))).perform(replaceText(subject), closeSoftKeyboard());
        // Set date
        onView(allOf(withId(R.id.meeting_date_EditText), isDisplayed())).perform(doubleClick());
        onView(allOf(IsInstanceOf.instanceOf(android.widget.DatePicker.class),
                withParent(allOf(withId(android.R.id.custom),
                withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout.class)))),
                        isDisplayed()))
                .perform(PickerActions.setDate(dateInts.get(0), dateInts.get(1), dateInts.get(2)));
        onView(allOf(withId(android.R.id.button1), withText("OK"))).perform(scrollTo(), click());
        // Set time
        onView(allOf(IsInstanceOf.instanceOf(android.widget.TimePicker.class),
                withParent(allOf(withId(android.R.id.custom),
                        withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout.class)))),
                isDisplayed()))
                .perform(PickerActions.setTime(dateInts.get(3),dateInts.get(4)));
        onView(allOf(withId(android.R.id.button1), withText("OK"))).perform(scrollTo(), click());

        // click on round table
        onView(allOf(withId(R.id.meeting_autoCRoom), isDisplayed())).perform(replaceText(room));
        // add mails
        int i = 0;
        for (String mail : mails) {
            i++;
            onView(allOf(withId(R.id.meeting_participants_edit_text), isDisplayed())).perform(click());
            if (i > 2) {
                onView(allOf(withId(R.id.meeting_participants_edit_text), isDisplayed())).perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN));
            }
            onView(allOf(withId(R.id.meeting_participants_edit_text), isDisplayed())).perform(typeTextIntoFocusedView(mail), closeSoftKeyboard());
            onView(allOf(withId(R.id.meeting_participants_edit_text), isDisplayed())).perform(pressImeActionButton());
        }

        onView(allOf(withId(R.id.meeting_ae_button), withText("Add meeting"), isDisplayed())).perform(click());
    }

    /**
     * We ensure that the edit part work properly
     */
    @Test
    public void editMeeting() {
        addMeetingTest("Evil meeting", Arrays.asList(2666,6,6,6,6), "Black room", Arrays.asList("EvilGoat@evil.com", "cthulhu@oldone.org", "BadHorse@neigh.net"));

        onView(allOf(withText("Black room - 06/06/2666 06h06 - Evil meeting"), isDisplayed())).perform(longClick());
        onView(allOf(withId(R.id.meeting_subject_edit_text), isDisplayed())).check(matches(withText("Evil meeting")));
        onView(allOf(withId(R.id.meeting_date_EditText), isDisplayed())).check(matches(withText("06/06/2666 - 06:06")));
        onView(allOf(withId(R.id.meeting_autoCRoom), isDisplayed())).check(matches(withText("Black room")));
        onView(allOf(withId(R.id.meeting_participants_edit_text), isDisplayed())).check(matches(withText("EvilGoat@evil.comcthulhu@oldone.orgBadHorse@neigh.net")));
        // change a value
        onView(allOf(withId(R.id.meeting_subject_edit_text))).perform(replaceText("Not so evil meeting"), closeSoftKeyboard());
        // get back
        onView(isRoot()).perform(pressBack());
        // verify that the meeting as not change
        onView(allOf(withText("Black room - 06/06/2666 06h06 - Evil meeting"), isDisplayed())).perform(longClick());

        onView(allOf(withId(R.id.meeting_subject_edit_text))).perform(replaceText("Not a suspicious meeting"), closeSoftKeyboard());
        onView(allOf(withId(R.id.meeting_ae_button), isDisplayed())).perform(click());

        onView(allOf(withId(R.id.item_title), isDisplayed())).check(matches(withText("Black room - 06/06/2666 06h06 - Not a suspicious meeting")));

        onView(allOf(ViewMatchers.withId(R.id.item_list_delete_button), isDisplayed())).perform(click());
    }

    /**
     * We ensure that the filter are functional
     */
    @Test
    public void testFilter() {
        addMeetingTest( "test 1", Arrays.asList(2021,12,15,9,0), "Oval office", Arrays.asList("Test1@gmail.com", "test2@gmail.com"));
        addMeetingTest( "test 2", Arrays.asList(2021,12,15,9,0), "Black room", Arrays.asList("Test1@gmail.com", "test2@gmail.com"));
        addMeetingTest( "test 3", Arrays.asList(2021,12,25,9,0), "Round table", Arrays.asList("Test1@gmail.com", "test2@gmail.com"));
        addMeetingTest( "test 4", Arrays.asList(2021,12,25,9,0), "Library", Arrays.asList("Test1@gmail.com", "test2@gmail.com"));

        onView(Matchers.allOf(ViewMatchers.withId(R.id.myRecyclerView), isDisplayed())).check(withItemCount(4));

        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText("Filter by date")).perform(click());
        onView(allOf(IsInstanceOf.instanceOf(android.widget.DatePicker.class),
                withParent(allOf(withId(android.R.id.custom),
                        withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout.class)))),
                isDisplayed()))
                .perform(PickerActions.setDate(2021,12,15));
        onView(allOf(withId(android.R.id.button1), withText("OK"))).perform(scrollTo(), click());

        onView(Matchers.allOf(ViewMatchers.withId(R.id.myRecyclerView), isDisplayed())).check(withItemCount(2));

        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText("Remove filter")).perform(click());
        onView(Matchers.allOf(ViewMatchers.withId(R.id.myRecyclerView), isDisplayed())).check(withItemCount(4));

        filterByRoomAndCheck("Oval office", 1);
        filterByRoomAndCheck("Black room", 2);
        filterByRoomAndCheck("Round table", 3);
        filterByRoomAndCheck("Library", 4);

        filterByRoomAndCheck("Oval office", 3);
        filterByRoomAndCheck("Black room", 2);
        filterByRoomAndCheck("Round table", 1);

        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText("Remove filter")).perform(click());
        onView(Matchers.allOf(ViewMatchers.withId(R.id.myRecyclerView), isDisplayed())).check(withItemCount(4));

        for (int i = 0; i<4; i++) {
            onView(allOf(withIndex(withId(R.id.item_list_delete_button), 0), isDisplayed())).perform(click());
        }
    }

    private void filterByRoomAndCheck(String s, int count) {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText("Filter by room")).perform(click());
        onView(withText(s)).perform(click());
        onView(Matchers.allOf(ViewMatchers.withId(R.id.myRecyclerView), isDisplayed())).check(withItemCount(count));
    }

    /**
     * Matcher for Index
     */
    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }
}

package com.ulkan.meetingapp.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.ChipDrawable;
import com.ulkan.meetingapp.DI.DI;
import com.ulkan.meetingapp.R;
import com.ulkan.meetingapp.databinding.FragmentAddMeetingBinding;
import com.ulkan.meetingapp.model.Meeting;
import com.ulkan.meetingapp.model.Room;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddEditMeetingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEditMeetingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MEETING = "MEETING";

    private FragmentAddMeetingBinding binding;

    private Meeting mMeeting;

    private Date mDateTMP;

    private final List<Room> mRooms = DI.getMeetingApiService().getRooms();

    private final String mSimpleDateFormat = "dd/MM/yyyy - HH:mm";

    public AddEditMeetingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param meeting ID if needed.
     * @return A new instance of fragment AddMeetingFragment.
     * <p>
     * Will edit the meeting
     */
    public static AddEditMeetingFragment newInstance(long meeting) {
        AddEditMeetingFragment fragment = new AddEditMeetingFragment();
        Bundle args = new Bundle();
        args.putLong(MEETING, meeting);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mMeeting = null;
        super.onCreate(savedInstanceState);
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mMeeting = DI.getMeetingApiService().getMeetingById(getArguments().getLong(MEETING));
        } else {
            mMeeting = new Meeting();
            mDateTMP = new Date();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddMeetingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setUI();
        setMeetingInfo(mMeeting);
        return view;
    }

    /**
     * Setup UI and logic part
     */
    private void setUI() {
        ArrayAdapter<Room> adapter = new ArrayAdapter<>(requireContext(), R.layout.list_item, mRooms);
        binding.meetingAutoCRoom.setAdapter(adapter);

        binding.meetingDateEditText.setInputType(InputType.TYPE_NULL);
        binding.meetingDate.setOnClickListener(setCalendar());
        binding.meetingDateEditText.setOnClickListener(setCalendar());

        binding.meetingAeButton.setText(R.string.AddMeeting);
        binding.meetingAeButton.setOnClickListener(validate());

        setParticipantsUI();
    }

    private void setParticipantsUI() {
        // If next action, add span if mail valid
        binding.meetingParticipantsEditText.setOnEditorActionListener((v, actionId, event) -> {

            int SpannedLength = 0;
            switch (actionId) {
                case EditorInfo.IME_ACTION_NEXT:
                    if (binding.meetingParticipantsEditText.getText() != null) {
                        SpannedLength = getSpannedPartLength(binding.meetingParticipantsEditText.getText());
                    }

                    if (getContext() != null && isEmailValid(binding.meetingParticipantsEditText.getText().toString().substring(SpannedLength))) {

                        ChipDrawable chip = ChipDrawable.createFromResource(getContext(), R.xml.chip);
                        chip.setText(binding.meetingParticipantsEditText.getText().toString().substring(SpannedLength));
                        chip.setBounds(0, 0, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());
                        ImageSpan span = new ImageSpan(chip);
                        binding.meetingParticipantsEditText.getText().setSpan(span, SpannedLength, binding.meetingParticipantsEditText.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.not_valid_mail_adress), Toast.LENGTH_SHORT).show();
                    }
            }
            return true;
        });
    }

    public int getSpannedPartLength(Spannable str) {
        ImageSpan[] spans = str.getSpans(0, str.length(), ImageSpan.class);
        int length = 0;
        for (ImageSpan span : spans) {
            ChipDrawable ch = (ChipDrawable) span.getDrawable();
            if (ch.getText() != null) {
                length += ch.getText().toString().length();
            }
        }
        return length;
    }

    public void setMeetingInfo(Meeting meeting) {
        int SpannedLength = 0;
        if (meeting.getSubject() != null) {
            binding.meetingSubjectEditText.setText(meeting.getSubject());
            binding.meetingAeButton.setText(R.string.EditMeeting);
        }
        if (meeting.getDate() != null) {
            SimpleDateFormat fmt = new SimpleDateFormat(mSimpleDateFormat, Locale.getDefault());
            binding.meetingDateEditText.setText(fmt.format(meeting.getDate()));
            mDateTMP = new Date(meeting.getDate().getTime());
        }
        if (meeting.getRoom() != null) {
            binding.meetingAutoCRoom.setText(meeting.getRoom().getName(), false);
        }
        if (meeting.getParticipants() != null) {
            for (String participant : meeting.getParticipants()) {
                if (binding.meetingParticipantsEditText.getText() != null) {
                    SpannedLength = getSpannedPartLength(binding.meetingParticipantsEditText.getText());
                }
                binding.meetingParticipantsEditText.append(participant);
                ChipDrawable chip = ChipDrawable.createFromResource(getContext(), R.xml.chip);
                chip.setText(participant);
                chip.setBounds(0, 0, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(chip);
                binding.meetingParticipantsEditText.getText().setSpan(span, SpannedLength, binding.meetingParticipantsEditText.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }



    @NonNull
    private View.OnClickListener setCalendar() {
        return v -> {
            final Calendar cldr = Calendar.getInstance();
            //cldr.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            int hour = cldr.get(Calendar.HOUR_OF_DAY);
            int min = cldr.get(Calendar.MINUTE);

            // time picker dialog
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getContext(),
                    (TimePicker timePicker, int hour1, int minute) -> {
                        mDateTMP.setMinutes(minute);
                        mDateTMP.setHours(hour1);
                        SimpleDateFormat fmt = new SimpleDateFormat(mSimpleDateFormat, Locale.getDefault());
                        binding.meetingDateEditText.setText(fmt.format(mDateTMP));
                    },
                    hour,
                    min,
                    true);
            mTimePicker.updateTime(mDateTMP.getHours(), mDateTMP.getMinutes());
            mTimePicker.show();

            // date picker dialog
            DatePickerDialog mDatePicker;
            mDatePicker = new DatePickerDialog(getContext(),
                    (DatePicker view, int year1, int monthOfYear, int dayOfMonth) -> {
                        mDateTMP.setYear(year1-1900);
                        mDateTMP.setMonth(monthOfYear);
                        mDateTMP.setDate(dayOfMonth);
                        SimpleDateFormat fmt = new SimpleDateFormat(mSimpleDateFormat, Locale.getDefault());
                        binding.meetingDateEditText.setText(fmt.format(mDateTMP));
                    },
                    year,
                    month,
                    day);
            mDatePicker.updateDate(mDateTMP.getYear()+1900, mDateTMP.getMonth(), mDateTMP.getDate());
            mDatePicker.show();

        };
    }


    /**
     * method is used for checking valid email id format.
     *
     * @param email MAIL
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);
        return matcher.matches();
    }

    /**
     * Setup validate 'Edit' or 'Add' logic
     */
    private View.OnClickListener validate() {
        return v -> {
            clearError();
            if(areValidInputs()) {
                updateMeeting();
                getActivity().onBackPressed();
            }
        };
    }

    public void clearError(){
        binding.meetingSubject.setError(null);
        binding.meetingDate.setError(null);
        binding.meetingRoom.setError(null);
        binding.meetingParticipants.setError(null);
    }

    public boolean areValidInputs() {
        boolean valid = true;
        valid = checkSubject(valid, binding.meetingSubjectEditText.getText().toString());
        valid = checkDate(valid, binding.meetingDateEditText.getText().toString());
        valid = checkRoom(valid, binding.meetingAutoCRoom.getText().toString());
        valid = checkParticipants(valid, binding.meetingParticipantsEditText.getText());
        return valid;
    }

    public boolean checkSubject(boolean valid,String subject) {
        if (subject.length() <= 2) {
            binding.meetingSubject.setError(getString(R.string.invalid_subject));
            valid = false;
        }
        return valid;
    }

    public boolean checkDate(boolean valid, String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(mSimpleDateFormat, Locale.FRANCE);
        Date date = new Date(1,0,1);
        boolean parsable;

        try {
            date = sdf.parse(dateString);
            parsable = true;
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
            parsable = false;
        }

        assert date != null;
        if (date.before(Calendar.getInstance().getTime())) {
            binding.meetingDate.setError(getString(R.string.date_passed));
            valid = false;
        }

        if (dateString.isEmpty() || !parsable) {
            binding.meetingDate.setError(getString(R.string.wrong_date_format));
            valid = false;
        }

        return valid;
    }

    public boolean checkRoom(boolean valid,String room) {
        if (room.equals("")) {
            binding.meetingRoom.setError(getString(R.string.no_room_select));
            valid = false;
        }
        return valid;
    }

    private boolean checkParticipants(boolean valid, Editable participants) {
        if (participants.getSpans(0, participants.length(), ImageSpan.class).length < 2) {
            binding.meetingParticipants.setError(getString(R.string.participant_number_error));
            valid = false;
        }
        return valid;
    }

    public void updateMeeting() {
        if (getArguments() == null) {
            mMeeting.setId(DI.getMeetingApiService().getMeetings().size()+1);
        }

        mMeeting.setSubject(binding.meetingSubjectEditText.getText().toString());

        SimpleDateFormat sdf = new SimpleDateFormat(mSimpleDateFormat, Locale.getDefault());
        try {
            mMeeting.setDate(sdf.parse(binding.meetingDateEditText.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mMeeting.setRoom(DI.getMeetingApiService().getRoomByName(binding.meetingAutoCRoom.getText().toString()));

        Editable str = binding.meetingParticipantsEditText.getText();
        ImageSpan[] spans = str.getSpans(0, str.length(), ImageSpan.class);
        List<String> ptcp = new ArrayList<>();
        for (ImageSpan span : spans) {
            ChipDrawable ch = (ChipDrawable) span.getDrawable();
            if (ch.getText() != null) {
                ptcp.add(ch.getText().toString());
            }
        }
        mMeeting.setParticipants(ptcp);

        if (getArguments() == null) {
            DI.getMeetingApiService().createMeeting(mMeeting);
        }
    }
}
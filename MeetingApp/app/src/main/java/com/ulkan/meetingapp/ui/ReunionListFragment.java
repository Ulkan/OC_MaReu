package com.ulkan.meetingapp.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.ulkan.meetingapp.DI.DI;
import com.ulkan.meetingapp.R;
import com.ulkan.meetingapp.databinding.FragmentItemListBinding;
import com.ulkan.meetingapp.model.Meeting;
import com.ulkan.meetingapp.model.Room;
import com.ulkan.meetingapp.service.MeetingApiService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReunionListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReunionListFragment extends Fragment implements View.OnClickListener {

    private ArrayList<Meeting> mMeetingArrayList;
    private final MeetingApiService mMeetingApiService = DI.getMeetingApiService();

    private FragmentItemListBinding binding;

    Date mDate = null;
    List<Room> mRooms = new ArrayList<>();
    private Menu mMenu;

    public ReunionListFragment() {
        // Required empty public constructor
    }

    public static ReunionListFragment newInstance() {
        return new ReunionListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        SubMenu subMenu = menu.findItem(R.id.filterRoom).getSubMenu();

        for (Room room : mMeetingApiService.getRooms()) {
            subMenu.add(R.id.groupItems, (int) room.getId(), Menu.NONE, room.getName()).setCheckable(true);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentItemListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initData();
        initRecyclerView();
        setButton();
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        for (Room room : mMeetingApiService.getRooms()) {
            if (item.getItemId() == ((int)room.getId())) {
                item.setChecked(!item.isChecked());
                if (item.isChecked()) {
                    mRooms.add(room);
                }
                else {
                    mRooms.remove(room);
                }
                updateFilters();
                return true;
            }
        }
        switch(item.getItemId()) {
            case R.id.filterDate:
                final Calendar cldr = Calendar.getInstance();
                cldr.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                // date picker dialog
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getContext(),
                        (DatePicker view, int year1, int monthOfYear, int dayOfMonth) -> {
                            mDate = new Date(year1-1900, monthOfYear, dayOfMonth);
                            updateFilters();
                        },
                        year,
                        month,
                        day);

                mDatePicker.show();
                return true;

            case R.id.reset:
                ((ReunionAdapter)binding.myRecyclerView.getAdapter()).updateList((ArrayList<Meeting>) mMeetingApiService.getMeetings());
                mMeetingApiService.removeFilter();
                for (Room r : mRooms) {
                    mMenu.findItem(R.id.filterRoom).getSubMenu().findItem((int)r.getId()).setChecked(false);
                }
                mRooms.clear();
                mDate = null;
                return true;

            default :
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateFilters() {
        if (mRooms.size()>0) {
            mMeetingApiService.clearTemp();
        }
        else {
            mMeetingApiService.removeFilter();
        }
        ArrayList<Meeting> filterMeetings = new ArrayList<>();
        for (Room room : mRooms) {
            filterMeetings = (ArrayList<Meeting>) mMeetingApiService.filterMeetingsByRoom(room);
        }
        if (mDate != null) {
            filterMeetings = (ArrayList<Meeting>)mMeetingApiService.filterMeetingsByDate(mDate);
        }
        ((ReunionAdapter)binding.myRecyclerView.getAdapter()).updateList(filterMeetings);
    }


    private void setButton() {
        binding.addMeeting.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.addMeeting) {
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainerView, AddEditMeetingFragment.class, null)
                    .addToBackStack(AddEditMeetingFragment.class.getSimpleName())
                    .commit();
        }
    }


    /**
     * Initialize the API data
     */
    private void initData() {
        mMeetingArrayList = new ArrayList<>(mMeetingApiService.getMeetings());
    }

    /**
     * Initialize the RecyclerView in the fragment
     */
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.myRecyclerView.setLayoutManager(layoutManager);
        ReunionAdapter reunionAdapter = new ReunionAdapter(mMeetingArrayList, meeting -> {
            mMeetingArrayList.remove(meeting);
            mMeetingApiService.deleteMeeting(meeting);
            if (binding.myRecyclerView.getAdapter() != null) {
                binding.myRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        binding.myRecyclerView.setAdapter(reunionAdapter);
    }
}
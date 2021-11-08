package com.ulkan.meetingapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ulkan.meetingapp.R;
import com.ulkan.meetingapp.model.Meeting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ReunionAdapter extends RecyclerView.Adapter<ReunionAdapter.ViewHolder> {

    private final ArrayList<Meeting> mMeetings;
    private final ReunionAdapterListener mListener;


    public ReunionAdapter(ArrayList<Meeting> meetings, ReunionAdapterListener listener) {
        this.mMeetings = meetings;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meeting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.DisplayReunion(mMeetings.get(position));

        holder.deleteButton.setOnClickListener(v ->
                mListener.onDeleteMeeting(mMeetings.get(holder.getBindingAdapterPosition())));

        holder.itemView.setOnLongClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            AddEditMeetingFragment fragment = AddEditMeetingFragment.newInstance(mMeetings.get(holder.getBindingAdapterPosition()).getId());
            activity.getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainerView, fragment, null)
                    .addToBackStack(AddEditMeetingFragment.class.getSimpleName())
                    .commit();

            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mMeetings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView color;
        public final TextView title;
        public final TextView participant;
        public final ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            participant = itemView.findViewById(R.id.item_participant);
            color = itemView.findViewById(R.id.item_photo);
            deleteButton = itemView.findViewById(R.id.item_list_delete_button);
        }

        public void DisplayReunion(Meeting reunion) {
            SimpleDateFormat pattern = new SimpleDateFormat("dd/MM/yyyy HH'h'mm", Locale.getDefault());

            title.setText(String.format("%s - %s - %s", reunion.getRoom().getName(), pattern.format(reunion.getDate()), reunion.getSubject()));
            StringBuilder textParticipant = new StringBuilder();
            for (String participant : reunion.getParticipants()) {
                textParticipant.append(", ").append(participant);
            }
            participant.setText(textParticipant.substring(2));
            color.setColorFilter(reunion.getRoom().getColor());
        }
    }

    public interface ReunionAdapterListener {
        void onDeleteMeeting(Meeting meeting);
    }

    public void updateList(ArrayList<Meeting> meetings) {
        mMeetings.clear();
        mMeetings.addAll(meetings);
        notifyDataSetChanged();
    }


}

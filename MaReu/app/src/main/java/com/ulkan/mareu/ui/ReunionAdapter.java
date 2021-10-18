package com.ulkan.mareu.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ulkan.mareu.R;
import com.ulkan.mareu.model.Meeting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ReunionAdapter extends RecyclerView.Adapter<ReunionAdapter.ViewHolder> {

    private final ArrayList<Meeting> mMeetings;

    public ReunionAdapter(ArrayList<Meeting> meetings) { this.mMeetings = meetings;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meeting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.DisplayReunion(mMeetings.get(position));
    }

    @Override
    public int getItemCount() {
        return mMeetings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView title;
        public final TextView participant;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            participant = itemView.findViewById(R.id.item_participant);
        }

        public void DisplayReunion(Meeting reunion) {
            SimpleDateFormat pattern = new SimpleDateFormat("dd/MM/yyyy HH"+"h"+"mm");

            title.setText(reunion.getRoom()+" - "+pattern.format(reunion.getDate())+" - "+reunion.getSubject());
            StringBuilder textParticipant = new StringBuilder();
            for (String participant : reunion.getParticipants()) {
                textParticipant.append(", ").append(participant);
            }
            participant.setText(textParticipant.substring(2));
        }
    }
}

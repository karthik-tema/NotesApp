package com.example.notesapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.DataModelClass.ReminderData;
import com.example.notesapp.R;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    List<Object> list;
    Context context;
    OnReminderCompleteListener listener;

    public interface OnReminderCompleteListener {
        void onComplete(int id);
    }

    // ✔ Correct constructor order (matches HomePage)
    public ReminderAdapter(Context context, List<Object> list, OnReminderCompleteListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof String)
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_header, parent, false);
            return new HeaderHolder(v);

        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.row_reminder, parent, false);
            return new ReminderHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderHolder) {

            ((HeaderHolder) holder).headerTitle.setText((String) list.get(position));

        } else if (holder instanceof ReminderHolder) {

            ReminderData reminder = (ReminderData) list.get(position);
            ReminderHolder rh = (ReminderHolder) holder;

            rh.title.setText(reminder.title);
            rh.desc.setText(reminder.description);
            rh.date.setText(reminder.date);
            rh.time.setText(reminder.time);

            if (reminder.isCompleted) {
                rh.btnComplete.setVisibility(View.GONE);

            } else {
                rh.btnComplete.setVisibility(View.VISIBLE);
                rh.btnComplete.setOnClickListener(v -> listener.onComplete(reminder.id));
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ✔ UPDATE LIST METHOD (Needed!)
    public void updateList(List<Object> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    // ------------ View Holders -------------

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;

        public HeaderHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.headerTitle);
        }
    }

    public static class ReminderHolder extends RecyclerView.ViewHolder {
        TextView title, desc, date, time;
        Button btnComplete;

        public ReminderHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.remTitle);
            desc = itemView.findViewById(R.id.remDesc);
            date = itemView.findViewById(R.id.remDate);
            time = itemView.findViewById(R.id.remTime);
            btnComplete = itemView.findViewById(R.id.btnRemComplete);
        }
    }
}

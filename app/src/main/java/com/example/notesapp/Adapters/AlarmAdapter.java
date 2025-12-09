package com.example.notesapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.DataModelClass.AlarmData;
import com.example.notesapp.R;

import java.util.ArrayList;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    List<Object> list;
    Context context;

    OnAlarmActionListener listener;

    public interface OnAlarmActionListener {
        void onToggle(int alarmId, boolean isOn);
        void onDelete(int alarmId);
        void onEdit(int alarmId);
        void onCompleted(int alarmId);
    }

    public AlarmAdapter(Context context, List<Object> list, OnAlarmActionListener listener) {
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
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.row_header, parent, false);
            return new HeaderHolder(v);
        } else {
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.row_alarm, parent, false);
            return new AlarmHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).headerTitle.setText((String) list.get(position));

        } else if (holder instanceof AlarmHolder) {

            AlarmData alarm = (AlarmData) list.get(position);
            AlarmHolder ah = (AlarmHolder) holder;

            ah.title.setText(alarm.title);
            ah.time.setText(alarm.time);
            ah.date.setText(alarm.date);
            ah.switchBtn.setChecked(alarm.isActive);

            // Toggle Alarm
            ah.switchBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listener.onToggle(alarm.id, isChecked);
            });

            // Edit Alarm
            ah.btnEdit.setOnClickListener(v -> listener.onEdit(alarm.id));

            // Delete Alarm
            ah.btnDelete.setOnClickListener(v -> listener.onDelete(alarm.id));
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void updateList(List<Object> newList) {
        if (this.list == null) this.list = new ArrayList<>();
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }


    public static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;
        public HeaderHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.headerTitle);
        }
    }

    public static class AlarmHolder extends RecyclerView.ViewHolder {

        TextView title, date, time;
        Switch switchBtn;
        Button btnEdit, btnDelete;

        public AlarmHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.alarmTitle);
            date = itemView.findViewById(R.id.alarmDate);
            time = itemView.findViewById(R.id.alarmTime);
            switchBtn = itemView.findViewById(R.id.alarmSwitch);
            btnEdit = itemView.findViewById(R.id.btnEditAlarm);
            btnDelete = itemView.findViewById(R.id.btnDeleteAlarm);
        }
    }
}

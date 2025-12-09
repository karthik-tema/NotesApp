package com.example.notesapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.DataModelClass.TaskData;
import com.example.notesapp.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    List<Object> list;
    Context context;

    OnTaskCompleteListener completeListener;
    OnTaskClickListener clickListener;

    public interface OnTaskCompleteListener {
        void onComplete(int taskId);
    }

    public interface OnTaskClickListener {
        void onClick(int taskId);
    }

    public TaskAdapter(List<Object> list, Context context,
                       OnTaskCompleteListener completeListener,
                       OnTaskClickListener clickListener) {

        this.list = list;
        this.context = context;
        this.completeListener = completeListener;
        this.clickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return (list.get(position) instanceof String) ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_header, parent, false);
            return new HeaderHolder(v);
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.row_task, parent, false);
            return new TaskHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).headerTitle.setText((String) list.get(position));

        } else if (holder instanceof TaskHolder) {

            TaskData task = (TaskData) list.get(position);
            TaskHolder th = (TaskHolder) holder;

            th.title.setText(task.title);
            th.desc.setText(task.description);

            if (task.isCompleted) {
                th.btnComplete.setVisibility(View.GONE);
                th.itemView.setAlpha(0.6f);
                th.itemView.setOnClickListener(null);

            } else {
                th.btnComplete.setVisibility(View.VISIBLE);
                th.itemView.setAlpha(1f);

                th.btnComplete.setOnClickListener(v ->
                        completeListener.onComplete(task.id));

                th.itemView.setOnClickListener(v ->
                        clickListener.onClick(task.id));
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<Object> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;
        public HeaderHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.headerTitle);
        }
    }

    public static class TaskHolder extends RecyclerView.ViewHolder {
        TextView title, desc;
        Button btnComplete;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.rowTaskTitle);
            desc = itemView.findViewById(R.id.rowTaskDesc);
            btnComplete = itemView.findViewById(R.id.rowBtnComplete);
        }
    }
}

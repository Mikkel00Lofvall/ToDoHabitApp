package com.example.todo.habit;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.ThemeController;
import com.example.todo.interfaces.OnHabitChangedListener;
import com.example.todo.interfaces.OnHabitClickListener;
import com.example.todo.R;
import com.example.todo.todo.ToDoViewHolder;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitViewHolder> {

    private List<HabitItem> items = new ArrayList<>();
    private final OnHabitChangedListener listener;
    private final OnHabitClickListener clickListener;

    public HabitAdapter(List<HabitItem> items, OnHabitChangedListener listener, OnHabitClickListener clickListener) {
        this.listener = listener;
        this.items = items;
        this.clickListener = clickListener;
    }

    @NotNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.habit_list_item, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull HabitViewHolder holder, int position) {
        HabitItem item = items.get(position);

        setListItemBackgrounds(holder, position);

        holder.tvTime.setText(item.getCreationTime());
        holder.tvTitle.setText(item.getTitle());

        boolean habitAlreadyDone = item.getIsDone();
        holder.cbDone.setOnCheckedChangeListener(null);
        holder.cbDone.setChecked(habitAlreadyDone);
        holder.cbDone.setEnabled(!habitAlreadyDone);
        holder.cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setLastActivated(currentTime());
            item.setDone(true);
            item.updateStreak();

            notifyDataSetChanged();
            listener.onHabitChanged();
        });

        holder.btnDelete.setOnClickListener(v -> {
            confirmDelete(v.getContext(), item);
        });

        holder.itemView.setOnClickListener(v -> clickListener.onHabitClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void setListItemBackgrounds(@NonNull HabitViewHolder holder, int position) {
        int listColorItem1 = ThemeController.getThemeColor(holder.itemView.getContext(), R.attr.colorListItem1);
        int listColorItem2 = ThemeController.getThemeColor(holder.itemView.getContext(), R.attr.colorListItem2);

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(listColorItem1);
        } else {
            holder.itemView.setBackgroundColor(listColorItem2);
        }
    }

    private String currentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd MMM yyyy");
        return sdf.format(new Date());
    }

    private void confirmDelete(Context context, HabitItem habitItem) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Habit?")
                .setMessage("Are you sure you want to delete this habit?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    items.remove(habitItem);
                    notifyDataSetChanged();

                    listener.onHabitChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void onDataChanged() {
        notifyDataSetChanged();
    }
}

package com.example.todo.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.ThemeController;
import com.example.todo.allowance.TransactionRepository;
import com.example.todo.enums.ListMode;
import com.example.todo.enums.TransactionType;
import com.example.todo.interfaces.OnTaskChangedListener;
import com.example.todo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class ToDoAdapter extends RecyclerView.Adapter<ToDoViewHolder> {

    private List<ToDoItem> allItems;
    private List<ToDoItem> visibleItems = new ArrayList<>();
    private ListMode mode;

    private final OnTaskChangedListener listener;

    public ToDoAdapter(List<ToDoItem> allItems, ListMode mode, OnTaskChangedListener listener) {
        //this.items = todoList;
        this.allItems = allItems;
        this.mode = mode;
        this.listener = listener;
        filterItems();
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_list_item, parent, false);
        return new ToDoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ToDoViewHolder holder, int position) {
        ToDoItem item = visibleItems.get(position);

        setListItemBackgrounds(holder, position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.tvReward.setText((item.getReward() >= 0 ? "+ " : "- ") + Math.abs(item.getReward()));
        holder.tvReward.setTextColor(
                item.getReward() >= 0 ? Color.GREEN : Color.RED
        );
        holder.tvTime.setText(item.getCreationTime());

        // Prevent recycled listeners
        holder.cbDone.setOnCheckedChangeListener(null);
        holder.cbDone.setChecked(item.isDone());

        if (mode == ListMode.FINISHED) {
            holder.cbDone.setEnabled(false);
        } else {
            holder.cbDone.setEnabled(true);
            holder.cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setDone(isChecked);
                filterItems();
                notifyDataSetChanged();

                TransactionRepository.getInstance().addTransaction(item.getReward(), TransactionType.INCOME, String.format("Completed: " + item.getDescription()));

                listener.onTaskChanged();
            });
        }

        holder.btnDelete.setOnClickListener(v ->
                confirmDelete(v.getContext(), item)
        );
    }

    @Override
    public int getItemCount() {
        return visibleItems.size();
    }

    public void onDataChanged() {
        filterItems();
        notifyDataSetChanged();
    }

    private void setListItemBackgrounds(@NonNull ToDoViewHolder holder, int position) {
        int listColorItem1 = ThemeController.getThemeColor(holder.itemView.getContext(), R.attr.colorListItem1);
        int listColorItem2 = ThemeController.getThemeColor(holder.itemView.getContext(), R.attr.colorListItem2);

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(listColorItem1);
        } else {
            holder.itemView.setBackgroundColor(listColorItem2);
        }
    }


    private void filterItems() {
        visibleItems.clear();
        for (ToDoItem item : allItems) {
            if (mode == ListMode.ACTIVE && !item.isDone()) {
                visibleItems.add(item);
            } else if (mode == ListMode.FINISHED && item.isDone()) {
                visibleItems.add(item);
            }
        }
    }

    public void setMode(ListMode mode) {
        this.mode = mode;
        filterItems();
        notifyDataSetChanged();
    }

    private void confirmDelete(Context context, ToDoItem item) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Task?")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    allItems.remove(item);
                    filterItems();
                    notifyDataSetChanged();

                    listener.onTaskChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



}


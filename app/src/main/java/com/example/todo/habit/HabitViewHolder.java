package com.example.todo.habit;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;

import org.jetbrains.annotations.NotNull;

public class HabitViewHolder extends RecyclerView.ViewHolder {
    CheckBox cbDone;

    TextView tvTitle, tvTime, tvFrequency, tvGoal;
    ImageButton btnDelete;

    public HabitViewHolder(@NotNull View itemView) {
        super(itemView);
        cbDone = itemView.findViewById(R.id.habit_cb_done);
        tvTitle = itemView.findViewById(R.id.habit_tv_title);
        tvTime = itemView.findViewById(R.id.habit_tv_time);
        btnDelete = itemView.findViewById(R.id.habit_btn_delete);
    }
}

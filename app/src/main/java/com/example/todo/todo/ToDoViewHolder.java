package com.example.todo.todo;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;

public class ToDoViewHolder extends RecyclerView.ViewHolder {
    CheckBox cbDone;
    TextView tvTitle, tvTime, tvReward, tvDescription;

    ImageButton btnDelete;

    public ToDoViewHolder(@NonNull View itemView) {
        super(itemView);
        cbDone = itemView.findViewById(R.id.todo_cb_done);
        tvTitle = itemView.findViewById(R.id.todo_tv_title);
        tvTime = itemView.findViewById(R.id.todo_tv_time);
        btnDelete = itemView.findViewById(R.id.todo_btn_delete);
        tvDescription = itemView.findViewById(R.id.todo_tv_description);
        tvReward = itemView.findViewById(R.id.todo_tv_reward);
    }
}

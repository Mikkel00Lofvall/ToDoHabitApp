package com.example.todo.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.todo.R;

public class HabitDetailPage extends Fragment {

    private static final String ARG_HABIT = "arg_habit";

    private HabitItem habitItem;

    public static HabitDetailPage newInstance(HabitItem habitItem) {
        HabitDetailPage fragment = new HabitDetailPage();
        Bundle args = new Bundle();
        args.putSerializable(ARG_HABIT, habitItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            habitItem = (HabitItem) getArguments().getSerializable(ARG_HABIT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_habit_detail, container, false);

        TextView tvTitle = view.findViewById(R.id.detail_title);
        TextView tvFrequency = view.findViewById(R.id.detail_frequency);
        TextView tvGoal = view.findViewById(R.id.detail_goal);
        TextView tvTime = view.findViewById(R.id.detail_time);
        TextView tvStreak = view.findViewById(R.id.detail_streak);

        if (habitItem != null) {
            tvTitle.setText(habitItem.getTitle());
            tvFrequency.setText(habitItem.getFrequency());
            tvGoal.setText(habitItem.getGoal());
            tvTime.setText(habitItem.getCreationTime());
            tvStreak.setText(String.valueOf(habitItem.getStreak()));
        }

        return view;
    }
}
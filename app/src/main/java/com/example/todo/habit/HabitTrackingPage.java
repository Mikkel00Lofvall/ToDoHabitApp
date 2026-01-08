package com.example.todo.habit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.todo.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HabitTrackingPage extends Fragment {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final List<HabitItem> habitList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Button btnNewHabit;

    private HabitAdapter adapter;

    public HabitTrackingPage() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habit_tracking_page, container, false);

        this.recyclerView = view.findViewById(R.id.recycler_habit_tracking);
        this.btnNewHabit = view.findViewById(R.id.btn_new_habit);

        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadHabits();

        this.adapter = new HabitAdapter(
                habitList,
                () -> saveHabits(),                // OnHabitChangedListener
                habitItem -> openHabitDetailPage(habitItem) // OnHabitClickListener
        );

        this.recyclerView.setAdapter(adapter);
        this.btnNewHabit.setOnClickListener(v -> addNewHabit());

        return view;
    }

    private void addNewHabit() {
        Context context = getContext();
        if (context == null) return;

        // Create a vertical layout to hold all inputs
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20); // optional padding

        // Habit Name
        final EditText inputName = new EditText(context);
        inputName.setHint("Habit Name");
        layout.addView(inputName);

        // Frequency
        final Spinner frequencySpinner = new Spinner(context);
        String[] frequencies = {"Daily", "Weekly"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                frequencies
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(spinnerAdapter);
        layout.addView(frequencySpinner);

        // Goal
        final EditText inputGoal = new EditText(context);
        inputGoal.setHint("Goal (e.g., 1 per day)");
        layout.addView(inputGoal);

        // Build dialog
        new AlertDialog.Builder(context)
                .setTitle("New Habit")
                .setView(layout)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = inputName.getText().toString().trim();
                    String frequency = frequencySpinner.getSelectedItem().toString();
                    String goal = inputGoal.getText().toString().trim();

                    if (!name.isEmpty() && !frequency.isEmpty() && !goal.isEmpty()) {
                        String startDate = currentTime();
                        HabitItem newItem = new HabitItem(name, frequency, startDate, goal);

                        habitList.add(newItem);
                        adapter.onDataChanged();
                        recyclerView.scrollToPosition(habitList.size() - 1);

                        saveHabits();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void openHabitDetailPage(HabitItem habitItem) {
        HabitDetailPage fragment = HabitDetailPage.newInstance(habitItem);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment) // container in your activity
                .addToBackStack(null)
                .commit();
    }

    private String currentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd MMM yyyy");
        return sdf.format(new Date());
    }

    private void saveHabits() {
        List<HabitItem> snapshot = new ArrayList<>(habitList);

        executorService.execute(() -> {
            SharedPreferences prefs =
                    requireContext().getSharedPreferences("habits", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(snapshot);

            editor.putString("habit_list", json);
            editor.apply();
        });
    }

    private void loadHabits() {
        executorService.execute(() -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("habits", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = prefs.getString("habit_list", null);
            if (json != null) {
                List<HabitItem> loaded = gson.fromJson(json, new TypeToken<List<HabitItem>>(){}.getType());

                for (HabitItem item : loaded) {
                    item.updateStreak();
                }

                requireActivity().runOnUiThread(() -> {
                    habitList.clear();
                    habitList.addAll(loaded);
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }
}
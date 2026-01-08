package com.example.todo.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.todo.ThemeController;
import com.example.todo.enums.ListMode;
import com.example.todo.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ToDoPage extends Fragment {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private RecyclerView recyclerView;
    private ToDoAdapter adapter;
    private final List<ToDoItem> todoList = new ArrayList<>();
    private Button btnNewAssignment;
    private Button btnActive;
    private Button btnFinished;

    public ToDoPage() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_page, container, false);

        this.recyclerView = view.findViewById(R.id.recycler_todo);
        this.btnNewAssignment = view.findViewById(R.id.btn_new_task);
        this.btnActive = view.findViewById(R.id.btn_active);
        this.btnFinished = view.findViewById(R.id.btn_finished);

        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadTasks();

        this.adapter = new ToDoAdapter(todoList, ListMode.ACTIVE, () -> saveTasks());
        setTopButtonsBackground(true);
        this.recyclerView.setAdapter(adapter);

        this.btnNewAssignment.setOnClickListener(v -> addNewItem());
        btnActive.setOnClickListener(v -> {
            adapter.setMode(ListMode.ACTIVE);
            setTopButtonsBackground(true);
        });
        btnFinished.setOnClickListener(v -> {
            adapter.setMode(ListMode.FINISHED);
            setTopButtonsBackground(false);
        });


        return view;
    }

    private void addNewItem() {
        Context context = getContext();
        if (context == null) return;

        // Create a vertical layout for all inputs
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density); // 20dp padding
        layout.setPadding(padding, padding, padding, padding);

        // Title input
        final EditText inputTitle = new EditText(context);
        inputTitle.setHint("Enter Task Title");
        layout.addView(inputTitle);

        // Description input
        final EditText inputDescription = new EditText(context);
        inputDescription.setHint("Enter Task Description");
        layout.addView(inputDescription);

        // Reward input (amount)
        final EditText inputReward = new EditText(context);
        inputReward.setHint("Enter Reward (number)");
        inputReward.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputReward);

        // Show AlertDialog
        new AlertDialog.Builder(context)
                .setTitle("New Task / Reward")
                .setView(layout) // include all inputs
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = inputTitle.getText().toString().trim();
                    String description = inputDescription.getText().toString().trim();
                    String rewardText = inputReward.getText().toString().trim();

                    if (!title.isEmpty() && !description.isEmpty() && !rewardText.isEmpty()) {
                        int reward = 0;
                        try {
                            reward = Integer.parseInt(rewardText);
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Reward must be a number", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Create your ToDoItem or TransactionItem
                        ToDoItem newItem = new ToDoItem(title, reward, currentTime(), false, description);
                        todoList.add(newItem);
                        adapter.onDataChanged();
                        recyclerView.scrollToPosition(0);

                        saveTasks();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private String currentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd MMM yyyy");
        return sdf.format(new Date());
    }

    private void setTopButtonsBackground(boolean isAllowanceSelected) {

        int selectedColor = ThemeController.getThemeColor(requireContext(), R.attr.tabSelectedColor);
        int unselectedColor = ThemeController.getThemeColor(requireContext(), R.attr.tabUnselectedColor);
        int textSelected = ThemeController.getThemeColor(requireContext(), R.attr.tabTextSelected);
        int textUnselected = ThemeController.getThemeColor(requireContext(), R.attr.tabTextUnselected);

        if (isAllowanceSelected) {
            btnActive.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
            btnActive.setTextColor(textSelected);

            btnFinished.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
            btnFinished.setTextColor(textUnselected);
        } else {
            btnActive.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
            btnActive.setTextColor(textUnselected);

            btnFinished.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
            btnFinished.setTextColor(textSelected);
        }
    }


    private void saveTasks() {
        List<ToDoItem> snapshot = new ArrayList<>(todoList);

        executorService.execute(() -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("tasks", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(snapshot);
            editor.putString("todo_list", json);
            editor.apply();
        });
    }

    private void loadTasks() {
        executorService.execute(() -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("tasks", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = prefs.getString("todo_list", null);
            if (json != null) {
                List<ToDoItem> loaded = gson.fromJson(json, new TypeToken<List<ToDoItem>>(){}.getType());

                requireActivity().runOnUiThread(() -> {
                    todoList.clear();
                    todoList.addAll(loaded);

                    adapter.onDataChanged();
                });
            }
        });
    }
}
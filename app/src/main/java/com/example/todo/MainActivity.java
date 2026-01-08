package com.example.todo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todo.allowance.AllowancePage;
import com.example.todo.allowance.TransactionRepository;
import com.example.todo.habit.HabitTrackingPage;
import com.example.todo.todo.ToDoPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TransactionRepository.getInstance().init(this);

        setupBottomBar();

        navigateToTaskPage(savedInstanceState);
    }

    private void navigateToTaskPage(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ToDoPage())
                    .commit();
        }
    }

    private void setupBottomBar() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_todo) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ToDoPage())
                        .commit();
                return true;

            }

            else if (id == R.id.nav_habit_tracking) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new HabitTrackingPage())
                        .commit();
                return true;
            }

            else if (id == R.id.nav_allowance) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new AllowancePage())
                        .commit();
                return true;
            }

            return false;
        });
    }
}
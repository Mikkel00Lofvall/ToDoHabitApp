package com.example.todo.allowance;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.todo.enums.TransactionType;
import com.example.todo.todo.ToDoItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class TransactionRepository {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static TransactionRepository instance;
    private final List<TransactionItem> transactionsList = new ArrayList<>();

    private final String TRANSACTION_PREF_NAME = "transactions";
    private final String BALANCE_PREF_NAME = "balance";
    private final String KEY_TRANSACTIONS = "transactions_list";

    private final String KEY_BALANCE = "balance_amount";

    private Context appContext;

    private int balance = 0;

    private TransactionRepository() {}

    public void init(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public static synchronized TransactionRepository getInstance() {
        if (instance == null) {
            instance = new TransactionRepository();
        }
        return instance;
    }

    public List<TransactionItem> getTransactionsList() {
        return transactionsList;
    }

    public void addTransaction(int amount, TransactionType type, String description) {
        if (type == TransactionType.EXPENSE) {
            this.balance = this.balance - amount;
        }

        else if (type == TransactionType.INCOME) {
            this.balance = this.balance + amount;
        }
        transactionsList.add(new TransactionItem(amount, currentTime(), type, description));
        saveAll();
    }

    public void removeTransaction(TransactionItem item) {
        transactionsList.remove(item);
        saveAll();
    }


    private void saveAll() {
        if (appContext == null) return;

        List<TransactionItem> snapshot = new ArrayList<>(transactionsList);
        int currentBalance = balance;

        executorService.execute(() -> {
            SharedPreferences prefsTrans =
                    appContext.getSharedPreferences(TRANSACTION_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences prefsBalance =
                    appContext.getSharedPreferences(BALANCE_PREF_NAME, Context.MODE_PRIVATE);

            Gson gson = new Gson();
            String jsonTrans = gson.toJson(snapshot);

            prefsTrans.edit().putString(KEY_TRANSACTIONS, jsonTrans).apply();
            prefsBalance.edit().putInt(KEY_BALANCE, currentBalance).apply();
        });
    }

    public void loadBalance(Context context, Runnable onFinished) {
        executorService.execute(() -> {
            SharedPreferences prefs =
                    context.getApplicationContext()
                            .getSharedPreferences(BALANCE_PREF_NAME, Context.MODE_PRIVATE);


            int loaded = prefs.getInt(KEY_BALANCE, 0);
            balance = loaded;

            if (onFinished != null) {
                onFinished.run(); // notify UI
            }
        });
    }


    public void loadTransactions(Context context, Runnable onFinished) {
        executorService.execute(() -> {
            SharedPreferences prefs =
                    context.getApplicationContext()
                            .getSharedPreferences(TRANSACTION_PREF_NAME, Context.MODE_PRIVATE);

            String json = prefs.getString(KEY_TRANSACTIONS, null);

            if (json != null) {
                Gson gson = new Gson();
                List<TransactionItem> loaded =
                        gson.fromJson(json, new TypeToken<List<TransactionItem>>() {}.getType());

                synchronized (transactionsList) {
                    transactionsList.clear();
                    transactionsList.addAll(loaded);
                }
            }

            if (onFinished != null) {
                onFinished.run(); // notify UI
            }
        });
    }
    public int getBalance() {
        return this.balance;
    }

    private String currentTime() {
        return new SimpleDateFormat("HH:mm, dd MMM yyyy").format(new Date());
    }
}

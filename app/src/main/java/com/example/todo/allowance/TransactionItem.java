package com.example.todo.allowance;


import com.example.todo.enums.TransactionType;

import java.io.Serializable;

public class TransactionItem implements Serializable {
    private String date;
    private int amount;
    private String description;
    private TransactionType type;

    public TransactionItem(int amount, String date, TransactionType type, String description) {
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.description = description;
    }

    public String getDate() { return this.date; }
    public int getAmount() { return this.amount; }

    public TransactionType getType() { return this.type; }
    public String getDescription() { return this.description; }
}

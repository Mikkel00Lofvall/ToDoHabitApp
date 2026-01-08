package com.example.todo.allowance;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.ThemeController;
import com.example.todo.enums.TransactionType;
import com.example.todo.interfaces.OnTransactionClickListener;
import com.example.todo.todo.ToDoViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {

    private List<TransactionItem> items = new ArrayList<>();
    private final OnTransactionClickListener clickListener;


    public TransactionAdapter(List<TransactionItem> list, OnTransactionClickListener clickListener) {
        this.items = list;
        this.clickListener = clickListener;
    }

    @NotNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_list_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull TransactionViewHolder holder, int position) {
        TransactionItem item = items.get(position);

        setListItemBackgrounds(holder, position);

        if (item.getType() == TransactionType.INCOME) {
            holder.tvAmount.setText("+ " + item.getAmount());
            holder.tvAmount.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.income_green)
            );
        } else {
            holder.tvAmount.setText("- " + item.getAmount());
            holder.tvAmount.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.expense_red)
            );
        }

        holder.tvTime.setText(item.getDate());
        holder.tvDescription.setText(item.getDescription());

        holder.btnDelete.setOnClickListener(v -> {
            confirmDelete(v.getContext(), item);
        });
    }

    private void setListItemBackgrounds(@NonNull TransactionViewHolder holder, int position) {
        int listColorItem1 = ThemeController.getThemeColor(holder.itemView.getContext(), R.attr.colorListItem1);
        int listColorItem2 = ThemeController.getThemeColor(holder.itemView.getContext(), R.attr.colorListItem2);

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(listColorItem1);
        } else {
            holder.itemView.setBackgroundColor(listColorItem2);
        }
    }

    private void confirmDelete(Context context, TransactionItem item) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Transaction?")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    TransactionRepository.getInstance().removeTransaction(item);
                    this.onDataChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void onDataChanged() {
        this.notifyDataSetChanged();
    }
}

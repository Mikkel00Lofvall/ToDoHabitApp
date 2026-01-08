package com.example.todo.allowance;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;

import org.jetbrains.annotations.NotNull;

public class TransactionViewHolder extends RecyclerView.ViewHolder {

    TextView tvAmount, tvTime, tvDescription;
    ImageButton btnDelete;

    public TransactionViewHolder(@NotNull View itemView) {
        super(itemView);
        tvAmount = itemView.findViewById(R.id.transaction_item_tv_amount);
        tvTime = itemView.findViewById(R.id.transaction_item_tv_time);
        btnDelete = itemView.findViewById(R.id.transaction_item_btn_delete);
        tvDescription = itemView.findViewById(R.id.transaction_item_tv_description);
    }

}

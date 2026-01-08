package com.example.todo.allowance;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todo.R;


public class TransactionPage extends Fragment {

    private static final String ARG_TRANSACTION = "arg_transaction";
    private TransactionItem transactionItem;

    public static TransactionPage newInstance(TransactionItem item) {
        TransactionPage fragment = new TransactionPage();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRANSACTION, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transactionItem = (TransactionItem) getArguments().getSerializable(ARG_TRANSACTION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transaction_page, container, false);


        return view;
    }

}
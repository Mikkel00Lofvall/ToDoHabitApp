package com.example.todo.allowance;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.todo.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.todo.allowance.TransactionItem;
import com.example.todo.enums.TransactionType;


public class TransactionsListPage extends Fragment {

    private Button btnBalance, btnTransactions;
    private List<TransactionItem> transactionItemList;

    private TransactionAdapter adapter;
    private RecyclerView recyclerView;

    public static TransactionsListPage newInstance() {
        TransactionsListPage fragment = new TransactionsListPage();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions_list_page, container, false);

        this.btnTransactions = view.findViewById(R.id.allowance_btn_transaction);
        this.recyclerView = view.findViewById(R.id.recycler_transaction);
        this.btnBalance = view.findViewById(R.id.allowance_btn_balance);

        setTopButtonsBackground(false);

        TransactionRepository.getInstance()
                .loadTransactions(requireContext(), () ->
                        requireActivity().runOnUiThread(() ->
                                adapter.onDataChanged()
                        )
                );

        //.getInstance().addTransaction(1000, TransactionType.INCOME);

        transactionItemList = TransactionRepository.getInstance().getTransactionsList();

        this.btnBalance.setOnClickListener(v -> openBalancePage());


        this.adapter = new TransactionAdapter(transactionItemList, transactionItem -> openTransactionPage(transactionItem));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(adapter);

        adapter.onDataChanged();

        return view;
    }

    private void openBalancePage() {
        AllowancePage fragment = AllowancePage.newInstance();

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }


    private void openTransactionPage(TransactionItem item) {
        TransactionPage fragment = TransactionPage.newInstance(item);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setTopButtonsBackground(boolean isAllowanceSelected) {

        int selectedColor = ThemeController.getThemeColor(requireContext(), R.attr.tabSelectedColor);
        int unselectedColor = ThemeController.getThemeColor(requireContext(), R.attr.tabUnselectedColor);
        int textSelected = ThemeController.getThemeColor(requireContext(), R.attr.tabTextSelected);
        int textUnselected = ThemeController.getThemeColor(requireContext(), R.attr.tabTextUnselected);

        if (isAllowanceSelected) {
            btnBalance.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
            btnBalance.setTextColor(textSelected);

            btnTransactions.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
            btnTransactions.setTextColor(textUnselected);
        } else {
            btnBalance.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
            btnBalance.setTextColor(textUnselected);

            btnTransactions.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
            btnTransactions.setTextColor(textSelected);
        }
    }


}
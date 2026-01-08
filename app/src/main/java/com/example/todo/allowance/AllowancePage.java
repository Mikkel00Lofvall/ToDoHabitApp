package com.example.todo.allowance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todo.R;
import com.example.todo.ThemeController;
import com.example.todo.enums.TransactionType;
import com.example.todo.todo.ToDoItem;

import org.w3c.dom.Text;


public class AllowancePage extends Fragment {

    TextView twBalance;
    private Button btnTransactions, btnAllowance, btnPurchase;

    public static AllowancePage newInstance() {
        AllowancePage fragment = new AllowancePage();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allowance_page, container, false);

        this.btnAllowance = view.findViewById(R.id.allowance_btn_balance);
        this.btnTransactions = view.findViewById(R.id.allowance_btn_transaction);
        this.twBalance = view.findViewById(R.id.allowance_tw_balance);
        this.btnPurchase = view.findViewById(R.id.allowance_btn_new_purchase);

        this.btnTransactions.setOnClickListener(transactionItem -> openTransactionListPage());
        this.btnPurchase.setOnClickListener(v -> openPurchaseDialog());

        setTopButtonsBackground(true);

        loadBalance();


        return view;
    }

    private void openTransactionListPage() {
        TransactionsListPage fragment = TransactionsListPage.newInstance();

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadBalance() {
        TransactionRepository.getInstance().loadBalance(requireContext(), new Runnable() {
            @Override
            public void run() {
                requireActivity().runOnUiThread(() -> {
                    int balance = TransactionRepository.getInstance().getBalance();
                    twBalance.setText("$" + String.valueOf(balance));
                });
            }
        });
    }

    private void openPurchaseDialog() {
        Context context = getContext();
        if (context == null) return;

        // Create a vertical layout for all inputs
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density); // 20dp padding
        layout.setPadding(padding, padding, padding, padding);

        final EditText inputDescription = new EditText(context);
        inputDescription.setHint("Enter Purchase Description");
        layout.addView(inputDescription);

        final EditText inputPrice = new EditText(context);
        inputPrice.setHint("Enter Price Amount (number)");
        inputPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputPrice);

        // Show AlertDialog
        new AlertDialog.Builder(context)
                .setTitle("New Task / Reward")
                .setView(layout) // include all inputs
                .setPositiveButton("Add", (dialog, which) -> {
                    String description = inputDescription.getText().toString().trim();
                    String rewardText = inputPrice.getText().toString().trim();

                    if (!description.isEmpty() && !rewardText.isEmpty()) {
                        int price = 0;
                        try {
                            price = Integer.parseInt(rewardText);
                            TransactionRepository.getInstance().addTransaction(price, TransactionType.EXPENSE, description);
                            loadBalance();
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Reward must be a number", Toast.LENGTH_SHORT).show();
                            return;
                        }


                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void setTopButtonsBackground(boolean isAllowanceSelected) {

        int selectedColor = ThemeController.getThemeColor(requireContext(), R.attr.tabSelectedColor);
        int unselectedColor = ThemeController.getThemeColor(requireContext(), R.attr.tabUnselectedColor);
        int textSelected = ThemeController.getThemeColor(requireContext(), R.attr.tabTextSelected);
        int textUnselected = ThemeController.getThemeColor(requireContext(), R.attr.tabTextUnselected);

        if (isAllowanceSelected) {
            btnAllowance.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
            btnAllowance.setTextColor(textSelected);

            btnTransactions.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
            btnTransactions.setTextColor(textUnselected);
        } else {
            btnAllowance.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
            btnAllowance.setTextColor(textUnselected);

            btnTransactions.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
            btnTransactions.setTextColor(textSelected);
        }
    }
}
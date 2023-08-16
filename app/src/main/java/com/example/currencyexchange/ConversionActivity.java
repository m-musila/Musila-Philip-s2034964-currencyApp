package com.example.currencyexchange;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConversionActivity extends AppCompatActivity {

    private String currencyName;
    private String currencyCode;
    private double exchangeRate;

    private Spinner conversionDirectionSpinner;

    private EditText amountEditText;
    private TextView convertedAmountTextView;

    private void performConversion() {
        String amountText = amountEditText.getText().toString();
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountText);
        double convertedAmount;
        if (conversionDirectionSpinner.getSelectedItemPosition() == 0) {
            convertedAmount = amount * exchangeRate;
        } else {
            convertedAmount = amount / exchangeRate;
        }

        convertedAmountTextView.setText(String.format("%.2f", convertedAmount));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        // Get the data passed from MainActivity
        currencyName = getIntent().getStringExtra("CURRENCY_NAME");
        currencyCode = getIntent().getStringExtra("CURRENCY_CODE");
        exchangeRate = getIntent().getDoubleExtra("EXCHANGE_RATE", 1.0);

        // Set the data to UI elements (for example, TextViews)
        TextView currencyNameTextView = findViewById(R.id.currencyNameTextView);
        TextView currencyCodeTextView = findViewById(R.id.currencyCodeTextView);
        TextView exchangeRateTextView = findViewById(R.id.exchangeRateTextView);

        currencyNameTextView.setText(currencyName);
        currencyCodeTextView.setText(currencyCode);
        exchangeRateTextView.setText(String.valueOf(exchangeRate));

        conversionDirectionSpinner = findViewById(R.id.conversionDirectionSpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"GBP to " + currencyCode, currencyCode + " to GBP"}
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conversionDirectionSpinner.setAdapter(spinnerAdapter);

        amountEditText = findViewById(R.id.amountEditText);
        convertedAmountTextView = findViewById(R.id.convertedAmountTextView);
        Button convertButton = findViewById(R.id.convertButton);

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }
        });
    }

}

package com.example.currencyexchange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ConversionActivity extends AppCompatActivity {

    private double exchangeRate;
    private Spinner conversionDirectionSpinner;
    private EditText amountEditText;
    private TextView convertedAmountTextView;
    private Currency selectedCurrency;
    private TextView currencyNameTextView;
    private TextView currencyCodeTextView;
    private TextView exchangeRateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        // Initialize UI elements
        currencyNameTextView = findViewById(R.id.currencyNameTextView);
        currencyCodeTextView = findViewById(R.id.currencyCodeTextView);
        exchangeRateTextView = findViewById(R.id.exchangeRateTextView);
        conversionDirectionSpinner = findViewById(R.id.conversionDirectionSpinner);
        amountEditText = findViewById(R.id.amountEditText);
        convertedAmountTextView = findViewById(R.id.convertedAmountTextView);
        Button convertButton = findViewById(R.id.convertButton);

        // Pre-load the currency information
        preLoadCurrencyInfo();

        // Set up conversion
        setupConversion();

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }
        });
    }

    private void preLoadCurrencyInfo() {
        // Retrieve the passed Currency object from the Intent
        Intent intent = getIntent();
        selectedCurrency = (Currency) intent.getSerializableExtra("selectedCurrency");

        // Null check for selectedCurrency
        if (selectedCurrency == null) {
            Toast.makeText(this, "No Currency Selected", Toast.LENGTH_LONG).show();
            return;
        }

        // Set the TextViews with the data from the selectedCurrency object
        currencyNameTextView.setText(selectedCurrency.getCountryName());
        currencyCodeTextView.setText(selectedCurrency.getCurrencyCode());
        exchangeRateTextView.setText(String.valueOf(selectedCurrency.getExchangeRate()));
        exchangeRate = selectedCurrency.getExchangeRate();

        // Initialize the Spinner with the conversion directions
        String[] directions = new String[]{
                "GBP to " + selectedCurrency.getCurrencyCode(),
                selectedCurrency.getCurrencyCode() + " to GBP"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, directions);
        conversionDirectionSpinner.setAdapter(adapter);
    }

    private void setupConversion() {
        conversionDirectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                performConversion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing here
            }
        });
    }

    private void performConversion() {
        String amountText = amountEditText.getText().toString();
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountText);
        double convertedAmount;

        if (exchangeRate == 0.0) {
            Toast.makeText(this, "Exchange rate is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        if (conversionDirectionSpinner.getSelectedItemPosition() == 0) {
            convertedAmount = amount * exchangeRate;
        } else {
            convertedAmount = amount / exchangeRate;
        }

        convertedAmountTextView.setText(String.format("%.2f", convertedAmount));
    }
}

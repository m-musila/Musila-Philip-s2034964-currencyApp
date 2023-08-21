/**
 * Musila Philip Musila
 * student ID: s2034964
 */
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
/**
 * Activity for managing currency conversions.
 */

public class ConversionActivity extends AppCompatActivity {

//Member Variables
    private double exchangeRate;
    private Spinner conversionDirectionSpinner;
    private EditText amountEditText;
    private TextView convertedAmountTextView;
    private Currency selectedCurrency;
    private TextView currencyNameTextView;
    private TextView exchangeRateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        // Initializing UI elements
        currencyNameTextView = findViewById(R.id.currencyNameTextView);
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
        selectedCurrency = intent.getParcelableExtra("selectedCurrency");

        // Null check for selectedCurrency
        if (selectedCurrency == null) {
            Toast.makeText(this, "No Currency Selected", Toast.LENGTH_LONG).show();
            return;
        }

        // Set the TextViews with the data from the selectedCurrency object
        currencyNameTextView.setText(selectedCurrency.getCurrencyName());
        exchangeRateTextView.setText(String.valueOf(selectedCurrency.getConversionRate()));
        exchangeRate = selectedCurrency.getConversionRate();

        // Initialize the Spinner with the conversion directions
        String[] directions = new String[]{
                "GBP -> " + selectedCurrency.getCurrencyCode(),
                selectedCurrency.getCurrencyCode() + " -> GBP"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, directions);
        conversionDirectionSpinner.setAdapter(adapter);
    }
// Build conversion setup
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

//    Conversion performance
    private void performConversion() {
        String amountText = amountEditText.getText().toString();
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountText);
        if (amount <= 0) {
            Toast.makeText(this, "Please enter a positive amount", Toast.LENGTH_SHORT).show();
            return;
        }

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

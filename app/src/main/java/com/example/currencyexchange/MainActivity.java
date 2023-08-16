package com.example.currencyexchange;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CurrencyAdapter adapter;
    private List<Currency> currencies;
    private List<Currency> filteredCurrencies;
    private String xmlData;


    private void filterCurrencies(String query) {
        if (query == null || query.isEmpty()) {
            filteredCurrencies = new ArrayList<>(currencies);
        } else {
            filteredCurrencies = new ArrayList<>();
            String lowerCaseQuery = query.toLowerCase();

            for (Currency currency : currencies) {

                String countryName = currency.getCountryName();
                String currencyCode = currency.getCurrencyCode();

                if (countryName != null && countryName.toLowerCase().contains(lowerCaseQuery)) {
                    filteredCurrencies.add(currency);
                } else if (currencyCode != null && currencyCode.toLowerCase().contains(lowerCaseQuery)) {
                    filteredCurrencies.add(currency);
                }
            }

        }

        adapter.updateCurrencies(filteredCurrencies);
    }
    private void openConversionActivity(Currency currency) {
        Intent intent = new Intent(MainActivity.this, ConversionActivity.class);
        intent.putExtra("CURRENCY_NAME", currency.getCountryName());
        intent.putExtra("CURRENCY_CODE", currency.getCurrencyCode());
        intent.putExtra("EXCHANGE_RATE", currency.getExchangeRate());
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerViewExchangeRates);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SearchView searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCurrencies(newText);
                return true;
            }
        });
        // Fetch and parse the XML data in a background thread
        new FetchAndParseDataTask().execute("https://www.fx-exchange.com/gbp/rss.xml");

        adapter = new CurrencyAdapter(currencies);

        adapter.setOnItemClickListener(new CurrencyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Currency selectedCurrency = filteredCurrencies.get(position);
                openConversionActivity(selectedCurrency);
            }
        });


    }

    private String fetchXMLData(String urlString) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            if (output.length() == 0) {
                return null;
            }

            return output.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class FetchAndParseDataTask extends AsyncTask<String, Void, List<Currency>> {

        @Override
        protected List<Currency> doInBackground(String... urls) {
            xmlData = fetchXMLData(urls[0]);
            CurrencyParser parser = new CurrencyParser();
            try {
                List<Currency> parsedCurrencies = parser.parse(xmlData);
                for (Currency currency : parsedCurrencies) {
                    Log.d("FetchAndParseDataTask", "Currency Code: " + currency.getCurrencyCode() + ", Exchange Rate: " + currency.getExchangeRate());
                }
                return parsedCurrencies;
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(List<Currency> result) {
            currencies = result;

            // Log the data here
            for (Currency currency : currencies) {
                Log.d("FetchAndParseDataTask", "Currency Code: " + currency.getCurrencyCode() + ", Exchange Rate: " + currency.getExchangeRate());
            }

            // Create the adapter and attach it to the RecyclerView
            adapter = new CurrencyAdapter(currencies);
            recyclerView.setAdapter(adapter);

            // Set up a click listener for items in the RecyclerView
            setupItemClickListener();
        }

    }

    private void setupItemClickListener() {
        adapter.setOnItemClickListener(new CurrencyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Get the clicked currency
                Currency clickedCurrency = currencies.get(position);

                // Create an intent to navigate to the ConversionActivity
                Intent intent = new Intent(MainActivity.this, ConversionActivity.class);

                // Pass the clicked currency data to the ConversionActivity
                intent.putExtra("CURRENCY_NAME", clickedCurrency.getCountryName());
                intent.putExtra("CURRENCY_CODE", clickedCurrency.getCurrencyCode());
                intent.putExtra("EXCHANGE_RATE", clickedCurrency.getExchangeRate());

                // Start the ConversionActivity
                startActivity(intent);
            }
        });
    }

    // ... (Other methods, including fetchXMLData, CurrencyParser, etc.)
}

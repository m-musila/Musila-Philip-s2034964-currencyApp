package com.example.currencyexchange;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
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

    private TextView lastUpdatedTextView;
    private FetchAndParseDataTask fetchAndParseDataTask;


    private Handler updateHandler = new Handler();
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            refreshCurrencyData();
            updateHandler.postDelayed(this, 300000); // 5 minutes
        }
    };

    private void refreshCurrencyData() {
        if (fetchAndParseDataTask != null) {
            fetchAndParseDataTask.cancel(true);
        }
        fetchAndParseDataTask = new FetchAndParseDataTask(this);
        fetchAndParseDataTask.execute("https://www.fx-exchange.com/gbp/rss.xml");
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateHandler.post(updateRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateHandler.removeCallbacks(updateRunnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerViewExchangeRates);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Country Code: MUR");

        // Initialize the currencies and filteredCurrencies lists
        currencies = new ArrayList<>();
        filteredCurrencies = new ArrayList<>();

        // Get the TextView from the layout
        lastUpdatedTextView = findViewById(R.id.tvLastUpdated);


        ImageButton refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshCurrencyData();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            public boolean onQueryTextChange(String newText) {
                filterCurrencies(newText);
                return true;
            }
        });

        // Fetch and parse the XML data in a background thread
        new FetchAndParseDataTask(this).execute("https://www.fx-exchange.com/gbp/rss.xml");
    }

    private void filterCurrencies(String query) {
        if (query == null || query.isEmpty()) {
            filteredCurrencies = new ArrayList<>(currencies);
        } else {
            filteredCurrencies = new ArrayList<>();
            String lowerCaseQuery = query.toLowerCase();

            for (Currency currency : currencies) {
                String currencyName = currency.getCurrencyName();
                String currencyCode = currency.getCurrencyCode();
                if ((currencyName != null && currencyName.toLowerCase().contains(lowerCaseQuery)) ||
                        (currencyCode != null && currencyCode.toLowerCase().contains(lowerCaseQuery))) {
                    filteredCurrencies.add(currency);
                }
            }
        }

        adapter.updateCurrencies(filteredCurrencies);
        // Notify the adapter about the search query
        adapter.setSearchQuery(query);
        adapter.updateCurrencies(filteredCurrencies);
    }


    private InputStream fetchXMLData(String urlString) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.d("XML_DATA_FETCH", "Response Code: " + responseCode);

            return connection.getInputStream();

        } catch (IOException e) {
            Log.e("XML_DATA_FETCH", "Error fetching XML data", e);
            return null;
        }
    }


    private static class FetchAndParseDataTask extends AsyncTask<String, Void, List<Currency>> {

        private final WeakReference<MainActivity> activityWeakReference;

        FetchAndParseDataTask(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected List<Currency> doInBackground(String... urls) {
            // Check if the urls array is not null and contains at least one URL
            if (urls == null || urls.length == 0) {
                return null;
            }

            InputStream xmlData = activityWeakReference.get().fetchXMLData(urls[0]);
            CurrencyParser parser = new CurrencyParser();

            try {
                return parser.parse(xmlData);
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                // Close the InputStream to avoid potential resource leaks
                if (xmlData != null) {
                    try {
                        xmlData.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(List<Currency> result) {
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                // The activity is no longer available, return null
                Log.w("FetchAndParseDataTask", "Activity is no longer available. Exiting doInBackground.");
                return;
            }

            if (result != null && !result.isEmpty()) {
                Log.d("FetchAndParseDataTask", "Number of currencies fetched: " + result.size());

                activity.currencies = result;
                activity.filteredCurrencies = new ArrayList<>(activity.currencies);

                // Create the adapter and attach it to the RecyclerView
                activity.adapter = new CurrencyAdapter(activity.currencies);
                activity.recyclerView.setAdapter(activity.adapter);

                Log.d("FetchAndParseDataTask", "Adapter is set with " + activity.currencies.size() + " items.");

                // Set up a click listener for items in the RecyclerView
                activity.setupItemClickListener();

                // Update the last updated timestamp TextView
                String lastUpdatedTimestamp = result.get(0).getTimestamp(); // Assuming all have the same timestamp
                activity.lastUpdatedTextView.setText("Last Updated: " + lastUpdatedTimestamp);

            } else {
                Log.e("FetchAndParseDataTask", "Result is null. Exiting onPostExecute.");
            }
        }


    }

    private void setupItemClickListener() {
        adapter.setOnItemClickListener(new CurrencyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Get the clicked currency
                Currency clickedCurrency = filteredCurrencies.get(position);

                // Create an intent to navigate to the ConversionActivity
                Intent intent = new Intent(MainActivity.this, ConversionActivity.class);

                // Pass the clicked currency data to the ConversionActivity
                intent.putExtra("selectedCurrency", clickedCurrency);

                // Start the ConversionActivity
                startActivity(intent);
            }
        });
    }


}

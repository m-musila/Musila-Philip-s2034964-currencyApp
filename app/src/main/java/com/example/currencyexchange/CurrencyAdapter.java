package com.example.currencyexchange;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {

    private List<Currency> currencies;
    private OnItemClickListener listener;
    private String searchQuery = "";

    public CurrencyAdapter(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public void updateCurrencies(List<Currency> newCurrencies) {
        this.currencies = newCurrencies;
        notifyDataSetChanged();
    }

    // Method to update the search query
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_item, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Currency currency = currencies.get(position);
        double exchangeRate = currency.getConversionRate();

        Log.d("CurrencyAdapter", "Setting Data for Position: " + position + " - Name: " + currency.getCurrencyName() + ", Exchange Rate: " + exchangeRate);

        holder.currencyName.setText(currency.getCurrencyName());
        holder.exchangeRate.setText(String.valueOf(exchangeRate));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Log.d("CurrencyAdapter", "Item clicked at Position: " + pos);
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(v, pos);
                }
            }
        });

        int color;
        if (exchangeRate < 1.0) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.very_weak_currency);
        } else if (exchangeRate >= 1.0 && exchangeRate < 5.0) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.weak_currency);
        } else if (exchangeRate >= 5.0 && exchangeRate < 10.0) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.medium_currency);
        } else {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.strong_currency);
        }

        if (searchQuery.equalsIgnoreCase("GBP") ||
                searchQuery.equalsIgnoreCase("USD") ||
                searchQuery.equalsIgnoreCase("EUR") ||
                searchQuery.equalsIgnoreCase("JPY")) {
            holder.tvCurrencyName.setBackgroundColor(Color.parseColor("#FF018786"));
        } else {
            holder.tvCurrencyName.setBackgroundColor(Color.TRANSPARENT); // Reset to default
        }

        Log.d("CurrencyAdapter", "Setting Text Color for Exchange Rate: " + color);

        holder.exchangeRate.setTextColor(color);
    }


    @Override
    public int getItemCount() {
        return currencies.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView currencyName;
        public TextView exchangeRate;
        final TextView tvCurrencyName;

        public ViewHolder(View view) {
            super(view);
            currencyName = view.findViewById(R.id.tvCurrencyName);
            exchangeRate = view.findViewById(R.id.tvConversionRate);
            tvCurrencyName = itemView.findViewById(R.id.tvCurrencyName);
        }
    }
}

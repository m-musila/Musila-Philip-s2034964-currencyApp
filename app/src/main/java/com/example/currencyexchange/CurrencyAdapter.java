package com.example.currencyexchange;

import android.graphics.Color;
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

    public CurrencyAdapter(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public void updateCurrencies(List<Currency> newCurrencies) {
        this.currencies = newCurrencies;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Currency currency = currencies.get(position);
        double exchangeRate = currency.getExchangeRate();

        holder.countryName.setText(currency.getCountryName());
        holder.currencyCode.setText(currency.getCurrencyCode());
        holder.exchangeRate.setText(String.valueOf(exchangeRate));

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

        holder.exchangeRate.setTextColor(color);

        if (currency.getCurrencyCode() != null) {
            // Highlight major currencies
            if (currency.getCurrencyCode().equals("USD") ||
                    currency.getCurrencyCode().equals("EUR") ||
                    currency.getCurrencyCode().equals("JPY")) {
                holder.itemView.setBackgroundColor(Color.LTGRAY); // or any other color
            } else {
                holder.itemView.setBackgroundColor(Color.WHITE); // default color
            }
        }
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
        public TextView countryName;
        public TextView currencyCode;
        public TextView exchangeRate;

        public ViewHolder(View view) {
            super(view);
            countryName = view.findViewById(R.id.countryName);
            currencyCode = view.findViewById(R.id.currencyCode);
            exchangeRate = view.findViewById(R.id.exchangeRate);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(v, position);
                        }
                    }
                }
            });
        }
    }
}

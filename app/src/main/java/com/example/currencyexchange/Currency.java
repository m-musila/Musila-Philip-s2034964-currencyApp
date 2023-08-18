package com.example.currencyexchange;

import android.os.Parcel;
import android.os.Parcelable;

public class Currency implements Parcelable {
    private String currencyName;
    private double conversionRate;
    private String timestamp;
    private String url;

    private String currencyCode;

    public Currency(String currencyName, double conversionRate, String timestamp, String currencyCode,String url) {
        this.currencyName = currencyName;
        this.conversionRate = conversionRate;
        this.timestamp = timestamp;
        this.currencyCode = currencyCode;
        this.url = url;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currencyName);
        dest.writeDouble(conversionRate);
        dest.writeString(timestamp);
        dest.writeString(currencyCode);
        dest.writeString(url);
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    private Currency(Parcel in) {
        currencyName = in.readString();
        conversionRate = in.readDouble();
        timestamp = in.readString();
        currencyCode = in.readString();
        url = in.readString();
    }
}

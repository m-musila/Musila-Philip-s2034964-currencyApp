/**
 * Musila Philip Musila
 * student ID: s2034964
 */
package com.example.currencyexchange;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The currency class provides details like name, conversion rate, timestamp, etc about a currency.
 * In addition the class implements Parcelable to help with passing currency objects between the application components.
 */
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
    // Source url: https://developer.android.com/reference/android/os/Parcelable
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
    // Constructor to initialize a currency object from a parcel
    private Currency(Parcel in) {
        currencyName = in.readString();
        conversionRate = in.readDouble();
        timestamp = in.readString();
        currencyCode = in.readString();
        url = in.readString();
    }
}

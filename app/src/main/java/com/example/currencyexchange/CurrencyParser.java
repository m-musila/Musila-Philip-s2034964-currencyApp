package com.example.currencyexchange;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CurrencyParser {

    public List<Currency> parse(String xmlData) throws XmlPullParserException, IOException {
        List<Currency> currencies = new ArrayList<>();
        Currency currentCurrency = null;
        String text = "";

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();

        parser.setInput(new StringReader(xmlData));
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagName.equals("item")) {
                        currentCurrency = new Currency();
                    }
                    break;

                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;

                case XmlPullParser.END_TAG:
                    if (currentCurrency != null) {
                        if (tagName.equals("title")) {
                            currentCurrency.setCountryName(text);
                        } else if (tagName.equals("description")) {
                            if (text.contains("=")) {
                                String[] parts = text.split("=");
                                if (parts.length == 2) {
                                    String ratePart = parts[1].split(" ")[1];
                                    int index = parts[1].indexOf(' ', 3);
                                    if (index != -1) {
                                        String currencyName = parts[1].substring(index).trim();
                                        currentCurrency.setCurrencyCode(currencyName);
                                        try {
                                            currentCurrency.setExchangeRate(Double.parseDouble(ratePart));
                                        } catch (NumberFormatException e) {
                                            Log.e("CurrencyParser", "Error parsing exchange rate: " + ratePart, e);
                                        }
                                    }
                                }
                            }
                        } else if (tagName.equals("item")) {
                            currencies.add(currentCurrency);
                        }
                    }
                    break;
            }
            eventType = parser.next();
        }
        return currencies;
    }


}

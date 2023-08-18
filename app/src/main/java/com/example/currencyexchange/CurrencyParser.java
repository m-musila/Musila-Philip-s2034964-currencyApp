package com.example.currencyexchange;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.InputStream;

public class CurrencyParser {

    private List<Currency> currencies;

    public CurrencyParser() {
        this.currencies = new ArrayList<>();
    }

    private static final String TAG = "CurrencyParser"; // TAG for log messages

    public List<Currency> parse(InputStream in) throws XmlPullParserException, IOException {
        Log.d(TAG, "parse() called with InputStream"); // Log when method is called

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        Log.d(TAG, "XmlPullParserFactory instance created"); // Log after factory instance creation

        XmlPullParser parser = factory.newPullParser();
        Log.d(TAG, "XmlPullParser instance created"); // Log after parser instance creation

        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            Log.d(TAG, "XmlPullParser FEATURE_PROCESS_NAMESPACES set to false"); // Log after setting parser feature

            parser.setInput(in, null);
            Log.d(TAG, "XmlPullParser input set with InputStream"); // Log after setting input to parser

            return readFeed(parser);
        } finally {
            in.close();
            Log.d(TAG, "InputStream closed"); // Log when InputStream is closed
        }
    }
    private List<Currency> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d(TAG, "readFeed() called");

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            Log.d(TAG, "Current XML tag: " + name);

            if (name.equals("rss")) {
                readChannel(parser);
            } else {
                Log.d(TAG, "Unexpected tag (" + name + "), skipping");
                skip(parser);
            }
        }

        Log.d(TAG, "Number of currencies parsed: " + currencies.size());
        return currencies;
    }

    private void readChannel(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "rss");
        Log.d(TAG, "Inside <rss> tag");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            Log.d(TAG, "Current XML tag inside <rss>: " + name);

            if (name.equals("channel")) {
                readItems(parser);
            } else {
                Log.d(TAG, "Unexpected tag (" + name + ") inside <rss>, skipping");
                skip(parser);
            }
        }
    }

    private void readItems(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "channel");
        Log.d(TAG, "Inside <channel> tag");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            Log.d(TAG, "Current XML tag inside <channel>: " + name);

            if (name.equals("item")) {
                Log.d(TAG, "Found <item> tag, reading currency");
                currencies.add(readCurrency(parser));
            } else {
                Log.d(TAG, "Unexpected tag (" + name + ") inside <channel>, skipping");
                skip(parser);
            }
        }
    }




    private Currency readCurrency(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "item");

        double conversionRate = 0.0;
        String currencyName = null;
        String currencyCode = null;
        String timestamp = null;
        String url = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("description")) {
                String description = readText(parser);
                Log.d("CurrencyParser", "Read description tag: " + description); // Log description

                String[] parts = description.split(" = ");
                Log.d("CurrencyParser", "Description split into parts: " + Arrays.toString(parts)); // Log split parts

                if (parts.length >= 2) {
                    try {
                        conversionRate = Double.parseDouble(parts[1].split(" ")[0]);
                        Log.d("CurrencyParser", "Parsed Conversion Rate: " + conversionRate); // Log parsed conversion rate

                        currencyName = parts[1].substring(parts[1].split(" ")[0].length()).trim();
                        Log.d("CurrencyParser", "Parsed Currency Name: " + currencyName); // Log parsed currency name

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Log.e("CurrencyParser", "NumberFormatException while parsing conversion rate", e); // Log exception
                    }
                }

            } else if (name.equals("pubDate")) {
                timestamp = readText(parser);
                Log.d("CurrencyParser", "Read pubDate tag: " + timestamp); // Log timestamp

            } else if (name.equals("link")) {
                url = readText(parser);
                Log.d("CurrencyParser", "Read link tag: " + url); // Log URL

            } else if (name.equals("title")) {
                String title = readText(parser);
                Log.d("CurrencyParser", "Read title tag: " + title); // Log title

                int startIdx = title.lastIndexOf("/") + 1;
                int endIdx = title.lastIndexOf("(");
                int codeStartIdx = title.lastIndexOf("(") + 1;
                int codeEndIdx = title.lastIndexOf(")");

                if (startIdx >= 0 && endIdx > startIdx && codeStartIdx > endIdx && codeEndIdx > codeStartIdx) {
                    currencyName = title.substring(startIdx, endIdx).trim();
                    currencyCode = title.substring(codeStartIdx, codeEndIdx).trim();
                }
            } else {
                skip(parser);
            }
        }

        Log.d("CurrencyParser", "Creating Currency Object - Name: " + currencyName + ", Code: " + currencyCode + ", Conversion Rate: " + conversionRate + ", Timestamp: " + timestamp + ", URL: " + url); // Log before returning the Currency object

        // Modify the Currency constructor or setter methods to include currencyCode
        return new Currency(currencyName, conversionRate, timestamp, currencyCode,url);
    }


    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}

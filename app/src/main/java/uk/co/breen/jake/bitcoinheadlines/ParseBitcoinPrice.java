package uk.co.breen.jake.bitcoinheadlines;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static uk.co.breen.jake.bitcoinheadlines.HttpContract.TAG_LAST_RATE;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.rateType;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.sourceList;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.tvPrice;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.bitcoinPrice;

/**
 * Created by Jacob on 30/05/2017.
 */

public class ParseBitcoinPrice extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String usdRate = "0", gbpRate = "0", eurRate = "0";

    public ParseBitcoinPrice(Context contextIn) {
        context = contextIn;
        this.execute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (!rateType.isEmpty() && bitcoinPrice != null) {
            if (rateType.equals("usd")) {
                tvPrice.setText("$" + bitcoinPrice.getUsd());
            } else if (rateType.equals("gbp")) {
                tvPrice.setText("£" + bitcoinPrice.getGbp());
            } else if (rateType.equals("eur")) {
                tvPrice.setText("€" + bitcoinPrice.getEur());
            }
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        HttpHandler httpHandler = new HttpHandler(context);
        String jsonStr = httpHandler.makeServiceCall(HttpContract.URL_GET_BITCOIN_PRICE);

        if (jsonStr != null) {
            try{

                    JSONObject rootObject = new JSONObject(jsonStr);

                    JSONObject usdObject = rootObject.getJSONObject(HttpContract.TAG_USD);
                    usdRate = String.valueOf(usdObject.getString(TAG_LAST_RATE));

                    JSONObject gbpObject = rootObject.getJSONObject(HttpContract.TAG_GBP);
                    gbpRate = String.valueOf(gbpObject.getString(TAG_LAST_RATE));

                    JSONObject eurObject = rootObject.getJSONObject(HttpContract.TAG_EUR);
                    eurRate = String.valueOf(eurObject.getString(TAG_LAST_RATE));

                    bitcoinPrice = new BitcoinPrice (usdRate, gbpRate, eurRate);


            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                ((Activity)context).runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        //Toast.makeText(context, "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "Couldn't retrieve Bitcoin price ticker.", Toast.LENGTH_LONG).show();
                    }
                });
            }

        } else {
            Log.e(TAG, "Couldn't get json from server.");
            ((Activity)context).runOnUiThread(new Runnable()
            {
                public void run()
                {
                    //Toast.makeText(context, "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Couldn't retrieve Bitcoin price ticker.", Toast.LENGTH_LONG).show();
                }
            });
        }

        return null;
    }
}

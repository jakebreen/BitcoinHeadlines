package uk.co.breen.jake.bitcoinheadlines;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.ContentValues.TAG;
import static uk.co.breen.jake.bitcoinheadlines.FragmentHeadlines.swipeRefreshLayout;
import static uk.co.breen.jake.bitcoinheadlines.FragmentSource.lv_source;
import static uk.co.breen.jake.bitcoinheadlines.FragmentSource.swipeRefreshLayoutSOURCE;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.sAdapter;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.sourceList;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.subscriptionList;

/**
 * Created by Jake on 05/03/2017.
 */

public class ParseSource extends AsyncTask<Void, Void, Void> {

    private Context context;
    String id;

    public ParseSource(Context contextIn) {
        context = contextIn;
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.e("AsyncTask", "onPreExecute");
        if (swipeRefreshLayoutSOURCE != null) swipeRefreshLayoutSOURCE.setRefreshing(true);
    }

    @Override
    protected Void doInBackground(Void... params) {

        HttpHandler httpHandler = new HttpHandler(context);
        String jsonStr = httpHandler.makeServiceCall(HttpContract.URL_GET_SOURCE_LIST);

        if (jsonStr != null) {
            try{
                JSONArray jsonArray = new JSONArray(jsonStr);

                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject rootObject = jsonArray.getJSONObject(i);

                    id = rootObject.getString(HttpContract.TAG_SOURCE_ID);
                    String title = rootObject.getString(HttpContract.TAG_SOURCE_TITLE);
                    String description = rootObject.getString(HttpContract.TAG_SOURCE_DESCRIPTION);
                    String dateArray = rootObject.getString(HttpContract.TAG_SOURCE_DATE_ARRAY);
                    String rssURL = rootObject.getString(HttpContract.TAG_SOURCE_RSS_URL);
                    String image;
                    if (rootObject.getString(HttpContract.TAG_SOURCE_IMAGE).equals(null)) {
                        image = "";
                    } else {
                        image = rootObject.getString(HttpContract.TAG_SOURCE_IMAGE);
                    }

                    sourceList.add(new Source(id, title, description, dateArray, image, rssURL));

                    //Log.e(TAG,"Article: " + id + " " + title + " " + image);
                }

            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                ((Activity)context).runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        //Toast.makeText(context, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "Error retrieving news please try again later." + id, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(context, "Error retrieving news please try again later.", Toast.LENGTH_LONG).show();
                }
            });
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Collections.sort(sourceList, new Comparator<Source>() {
            public int compare(Source source1, Source source2) {
                // ## Ascending order
                return source1.getTitle().compareTo(source2.getTitle());
            }
        });

        sAdapter = new SourceAdapter(context, 0, sourceList);
        //sAdapter.notifyDataSetChanged();
        if (!sAdapter.isEmpty() && sAdapter != null) lv_source.setAdapter(sAdapter);

        if (swipeRefreshLayoutSOURCE != null) swipeRefreshLayoutSOURCE.setRefreshing(false);
    }
}

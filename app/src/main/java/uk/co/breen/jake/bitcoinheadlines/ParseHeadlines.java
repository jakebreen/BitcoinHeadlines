package uk.co.breen.jake.bitcoinheadlines;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;
import static uk.co.breen.jake.bitcoinheadlines.FragmentHeadlines.lv_headlines;
import static uk.co.breen.jake.bitcoinheadlines.FragmentHeadlines.swipeRefreshLayout;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.adapter;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.articleID;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.headlineList;

/**
 * Created by Jake on 21/02/2017.
 */

public class ParseHeadlines extends AsyncTask<Void, Void, Void> {

    private ProgressDialog pDialog;
    private Context context;

    public ParseHeadlines(Context contextIn) {
        context = contextIn;
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        swipeRefreshLayout.setRefreshing(true);

        Log.e("AsyncTask", "onPreExecute");

        // Showing progress dialog
        //pDialog = new ProgressDialog(context);
        //pDialog.setMessage("Fetching headlines...");
        //pDialog.setCancelable(true);

        //((Activity)context).runOnUiThread(new Runnable()
        //{
        //    public void run()
        //    {
        //        pDialog.show();
        //    }
        //});
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        Log.v("AsyncTask", "doInBackground");

        HttpHandler httpHandler = new HttpHandler(context);
        String jsonStr = httpHandler.makeServiceCall(HttpContract.URL_GET_RSS_HEADLINES + "?id=" + articleID);


        if (jsonStr != null) {
            try{
                JSONArray jsonArray = new JSONArray(jsonStr);

                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject rootObject = jsonArray.getJSONObject(i);

                    String title = rootObject.getString(HttpContract.TAG_HEADLINE_TITLE);
                    String dateTime = rootObject.getString(HttpContract.TAG_HEADLINE_DATETIME);
                    String description = rootObject.getString(HttpContract.TAG_HEADLINE_DESCRIPTION);
                    String link = rootObject.getString(HttpContract.TAG_HEADLINE_LINK);
                    String author = rootObject.getString(HttpContract.TAG_HEADLINE_AUTHOR);
                    String categories = rootObject.getString(HttpContract.TAG_HEADLINE_CATEGORIES);
                    String image;
                    String fullDateTime = rootObject.getString(HttpContract.TAG_HEADLINE_FULLDATETIME);
                    if (rootObject.getString(HttpContract.TAG_HEADLINE_IMAGE).equals(null)) {
                        image = "";
                    } else {
                        image = rootObject.getString(HttpContract.TAG_HEADLINE_IMAGE);
                    }


                    headlineList.add(new Headline(title, dateTime, description, link, author, categories, image, fullDateTime));

                    //Log.e(TAG,"Article: " + title + " " + dateTime + " " + description + " " + link + " " + author + " " + categories + " ");
                }

            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                ((Activity)context).runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        //Toast.makeText(context, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "Error retrieving articles please try again later.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(context, "Error retrieving articles please try again later.", Toast.LENGTH_LONG).show();
                }
            });
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        //if (pDialog.isShowing())
        //    pDialog.dismiss();

        Log.e("ended", "ended");
        adapter = new HeadlineAdapter(context, 0, headlineList);
        adapter.notifyDataSetChanged();
        lv_headlines.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(false);
    }
}

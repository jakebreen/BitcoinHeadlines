package uk.co.breen.jake.bitcoinheadlines;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static uk.co.breen.jake.bitcoinheadlines.MainActivity.articleURLshort;

/**
 * Created by Jacob on 05/07/2017.
 */

public class ShortUrlAsync extends AsyncTask<Void,Void,String> {

    private String longUrl, shortUrl;
    private JSONObject jsonArray;

    public ShortUrlAsync(String link) {
        longUrl = link;
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //progressBar.setVisibility(View.GONE);
        System.out.println("JSON RESP:" + s);
        String response=s;
        try {
            JSONObject jsonObject=new JSONObject(response);
            String id=jsonObject.getString("id");
            articleURLshort = id;
            System.out.println("ID:"+id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected String doInBackground(Void... params) {
        BufferedReader reader;
        StringBuffer buffer;
        String res=null;
        String json = "{\"longUrl\": \""+longUrl+"\"}";
        try {
            URL url = new URL("***REMOVED***");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(40000);
            con.setConnectTimeout(40000);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            writer.write(json);
            writer.flush();
            writer.close();
            os.close();

            int status=con.getResponseCode();
            InputStream inputStream;
            if(status== HttpURLConnection.HTTP_OK)
                inputStream=con.getInputStream();
            else
                inputStream = con.getErrorStream();

            reader= new BufferedReader(new InputStreamReader(inputStream));

            buffer= new StringBuffer();

            String line="";
            while((line=reader.readLine())!=null)
            {
                buffer.append(line);
            }

            res= buffer.toString();

            jsonArray = new JSONObject(res);
            shortUrl = jsonArray.getString("id");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
}

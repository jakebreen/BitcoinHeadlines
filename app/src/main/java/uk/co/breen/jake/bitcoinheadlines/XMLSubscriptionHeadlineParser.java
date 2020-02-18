package uk.co.breen.jake.bitcoinheadlines;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import static uk.co.breen.jake.bitcoinheadlines.FragmentHeadlines.lv_headlines;
import static uk.co.breen.jake.bitcoinheadlines.FragmentHeadlines.swipeRefreshLayout;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.adapter;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.headlineList;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.lastDate;

/**
 * Created by Jacob on 23/05/2017.
 */

public class XMLSubscriptionHeadlineParser extends AsyncTask <Void, Void, Void> {

    private URL url;
    private Headline headline;
    private Context context;
    private String title;
    private String dateTime;
    private String description;
    private String link;
    private String author;
    private String categories;
    private String image;
    private String fullDateTime;
    private XmlPullParser xpp;
    private String error;
    private ArrayList<Source> subArrayList;
    private String rssDateTime;
    private String parsedDate;
    private String sortDateStr;
    private ArrayList<Headline> headlineArrayList;
    private ArrayList<Headline> subHeadlineArrayList;
    private Source source;
    private int newHeadlines;
    private int footerToggle;

    public XMLSubscriptionHeadlineParser(Context contextIn, ArrayList<Source> list, int newHeadlinesIn, int footerToggleIn) {
        context = contextIn;
        subArrayList = list;
        newHeadlines = newHeadlinesIn;
        footerToggle = footerToggleIn;
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        headlineArrayList = new ArrayList<Headline>();
        subHeadlineArrayList = new ArrayList<Headline>();
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //Collections.sort(subHeadlineArrayList);

        Collections.sort(headlineList, new Comparator<Headline>() {
            public int compare(Headline h1, Headline h2) {
                if (h2.getSortDate() == null || h1.getSortDate() == null) return 0;
                return h2.getSortDate().compareTo(h1.getSortDate());
            }
        });

        adapter = new SubscribedHeadlineAdapter(context, 0, headlineList);

        adapter.notifyDataSetChanged();
        lv_headlines.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        if (footerToggle == 1) FragmentHeadlines.toggleFooter(1);
        if (footerToggle == 0) FragmentHeadlines.toggleFooter(0);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        for (Source s : subArrayList) {
            source = s;

            outerLoop:
            if (!s.getRssURL().equals(null)) {

                try {
                    url = new URL(s.getRssURL());

                    try {
                        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                        urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                        urlConn.setReadTimeout(3000);
                        urlConn.connect();
                        if (urlConn.getResponseCode() == 200) {
                            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                            factory.setNamespaceAware(false);
                            xpp = factory.newPullParser();

                            // We will get the XML from an input stream
                            xpp.setInput(getInputStream(url), "UTF_8");

        /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
         * However, we should take in consideration that the rss feed name also is enclosed in a "<title>" tag.
         * As we know, every feed begins with these lines: "<channel><title>Feed_Name</title>...."
         * so we should skip the "<title>" tag which is a child of "<channel>" tag,
         * and take in consideration only "<title>" tag which is a child of "<item>"
         *
         * In order to achieve this, we will make use of a boolean variable.
         */
                            boolean insideItem = false;

                            // Returns the type of current event: START_TAG, END_TAG, etc..
                            int eventType = xpp.getEventType();
                            while (eventType != XmlPullParser.END_DOCUMENT) {

                                if (eventType == XmlPullParser.START_TAG) {

                                    if (xpp.getName().equalsIgnoreCase("item")) {
                                        insideItem = true;
                                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                                        if (insideItem) title = (xpp.nextText());
                                    } else if (xpp.getName().equalsIgnoreCase("link")) {
                                        if (insideItem) link = (xpp.nextText());
                                    } else if (xpp.getName().equalsIgnoreCase("description")) {
                                        if (insideItem)
                                            description = Html.fromHtml((xpp.nextText().replaceAll("\\<.*?\\>", ""))).toString();
                                    } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                                        if (insideItem) rssDateTime = (xpp.nextText());
                                    } else if (xpp.getName().equalsIgnoreCase("dc:creator")) {
                                        if (insideItem) author = (xpp.nextText());
                                    } else if (xpp.getName().equalsIgnoreCase("category")) {
                                        if (insideItem) categories = (xpp.nextText());
                                    }

                                    if (author != null && !author.equals("")) {
                                        //do something
                                    } else {
                                        author = " ";
                                    }

                                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {

                                    String subRssDateTime1 = rssDateTime.replaceAll("\\s+", " ");
                                    String subRssDateTime2 = subRssDateTime1.replaceAll("EST", "-0500");

                                    try {
                                        Calendar cal = Calendar.getInstance();
                                        TimeZone tz = cal.getTimeZone();
                                        SimpleDateFormat readDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                                        readDate.setTimeZone(TimeZone.getTimeZone("UTC"));
                                        Date date = readDate.parse(subRssDateTime2);
                                        SimpleDateFormat writeDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                                        writeDate.setTimeZone(tz);
                                        parsedDate = writeDate.format(date);

                                        SimpleDateFormat sortDate = new SimpleDateFormat("yyMMddHHmmssZ");
                                        sortDate.setTimeZone(tz);
                                        sortDateStr = sortDate.format(date);

                                        Date savedDate = writeDate.parse(lastDate);

                                        if (date.before(savedDate) && newHeadlines == 1) {
                                            break outerLoop;
                                        }


                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    fullDateTime = parsedDate;
                                    String newDesc = truncate(description, 400);

                                    String sourceId = s.getId();
                                    String sourceTitle = s.getTitle();

                                    headlineList.add(new Headline(sourceId, sourceTitle, title, parsedDate, sortDateStr, newDesc, link, author, categories, image, fullDateTime));
                                    //Log.e("Headline created: ", "" + headlineArrayList);
                                    insideItem = false;
                                }

                                eventType = xpp.next(); //move to next element

                            }

                        } else {
                            this.cancel(true);
                            ((Activity) context).runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(context, source.getTitle() + " is currently unreachable.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (MalformedURLException e) {
                        error = String.valueOf(e);
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, source.getTitle() + " is currently unreachable.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return null;
                    } catch (IOException e) {
                        error = String.valueOf(e);
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, source.getTitle() + " is currently unreachable.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (MalformedURLException e) {
                    //ERROR LOG DATABASE
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    //ERROR LOG DATABASE
                    e.printStackTrace();
                } catch (IOException e) {
                    //ERROR LOG DATABASE
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public static String truncate(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len) + "...";
        } else {
            return str;
        }
    }
}



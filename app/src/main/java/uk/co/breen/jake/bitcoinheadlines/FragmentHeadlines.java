package uk.co.breen.jake.bitcoinheadlines;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static uk.co.breen.jake.bitcoinheadlines.FragmentArticle.articleWebView;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.articleURL;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.headlineList;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.subscriptionArrayList;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.vpPager;

/**
 * Created by Jake on 04/03/2017.
 */

public class FragmentHeadlines extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected static ListView lv_headlines;
    protected static TextView tv_header;
    protected static ImageView iv_header;
    protected static SwipeRefreshLayout swipeRefreshLayout;

    // Store instance variables
    private String title;
    private int page;
    private ParseHeadlines parseHeadlines;
    private static Source source;
    private static LayoutInflater hLayoutInflater;
    private static ViewGroup viewFooter;
    private XMLSubscriptionHeadlineParser xmlSubscriptionHeadlineParser;

    // newInstance constructor for creating fragment with arguments
    public static FragmentHeadlines newInstance(int page, String title) {
        FragmentHeadlines fragmentFirst = new FragmentHeadlines();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        hLayoutInflater = getLayoutInflater(getArguments());
    }

    public static Source getSource(Source s, Context c) {
        source = s;

        if (source != null) {
            String bitmapLoc = "ic_" + source.getId() + "_60";
            int resId = c.getResources().getIdentifier(bitmapLoc, "drawable", c.getPackageName());
            iv_header.setImageResource(resId);

            tv_header.setText(source.getTitle());
            //new XMLHeadlineParser(c, source.getRssURL());
        }

        return source;
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_headlines, container, false);

        lv_headlines = (ListView) view.findViewById(R.id.ListView_Headlines);

        ViewGroup viewHeader = (ViewGroup) hLayoutInflater.inflate(R.layout.listview_header_headlines, lv_headlines, false);
        lv_headlines.addHeaderView(viewHeader, null, false);

        viewFooter = (ViewGroup) hLayoutInflater.inflate(R.layout.listview_footer_headlines, lv_headlines, false);

        viewFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headlineList = new ArrayList<Headline>();
                new XMLSubscriptionHeadlineParser(getActivity(), subscriptionArrayList, 0, 0);
                lv_headlines.setSelection(0);
                Toast.makeText(getActivity(), "Searching for headlines...", Toast.LENGTH_SHORT).show();
            }
        });

        iv_header = (ImageView) viewHeader.findViewById(R.id.sourceIV);
        tv_header = (TextView) viewHeader.findViewById(R.id.sourceTV);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        AdapterView.OnItemClickListener headlineAdapterViewListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                position -= lv_headlines.getHeaderViewsCount();

                Headline headline = headlineList.get(position);
                //articleURL = headline.getLink();
                articleWebView.loadUrl(headline.getLink());

                new ShortUrlAsync(headline.getLink());

                articleURL = headline.getLink();

                //myWebView.loadUrl("http://www.jakebreen.co.uk/bitcoinheadlines/sites/article.php?id=" + articleID + "&html=" + articleURL);
                //myWebView.loadUrl("http://www.jakebreen.co.uk/bitcoinheadlines/sites/article.php?id=newsbtc&html=http://www.newsbtc.com/2017/03/05/enterprise-ethereum-alliance-good-bad/");
                vpPager.setCurrentItem(2);
                //Log.e("url", articleURL);
            }
        };
        //set the listener to the list view
        lv_headlines.setOnItemClickListener(headlineAdapterViewListener);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        if (source == null) {
            swipeRefreshLayout.setRefreshing(false);
            vpPager.setCurrentItem(0);
            Toast.makeText(getActivity(), "Select a news source", Toast.LENGTH_SHORT).show();
        } else if (source.getId().equals("subscriptions")) {
            toggleFooter(0);
            headlineList = new ArrayList<Headline>();
            if (SubscribedHeadlineCounter.Count() == 0) {
                xmlSubscriptionHeadlineParser = new XMLSubscriptionHeadlineParser(getActivity(), subscriptionArrayList, 0, 0);
            } else if (SubscribedHeadlineCounter.Count() >= 1) {
                xmlSubscriptionHeadlineParser = new XMLSubscriptionHeadlineParser(getActivity(), subscriptionArrayList, 1, 1);
            }
        } else {
            headlineList = new ArrayList<Headline>();
            new XMLHeadlineParser(getActivity(), source.getRssURL());
        }
    }

    public static void toggleFooter(int i) {
        if (i == 1) {
            lv_headlines.addFooterView(viewFooter, null, false);
        } else if (i == 0) {
            lv_headlines.removeFooterView(viewFooter);
        }
    }
}

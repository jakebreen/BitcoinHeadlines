package uk.co.breen.jake.bitcoinheadlines;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.R.attr.fragment;
import static android.content.ContentValues.TAG;
import static uk.co.breen.jake.bitcoinheadlines.FragmentHeadlines.iv_header;
import static uk.co.breen.jake.bitcoinheadlines.FragmentHeadlines.tv_header;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.adapter;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.articleID;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.badge;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.lastDate;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.sourceList;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.subscriptionList;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.vpPager;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.rssUrl;

/**
 * Created by Jake on 04/03/2017.
 */

public class FragmentSource extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static ListView lv_source;
    protected static String articleIMG;
    private ParseHeadlines parseHeadlines;
    private ParseSource parseSource;
    private XMLHeadlineParser xmlHeadlineParser;
    private XMLSubscriptionHeadlineParser xmlSubscriptionHeadlineParser;
    protected static SwipeRefreshLayout swipeRefreshLayoutSOURCE;

    // Store instance variables
    private String title;
    private int page;
    private Source source;

    // newInstance constructor for creating fragment with arguments
    public static FragmentSource newInstance(int page, String title) {
        FragmentSource fragmentFirst = new FragmentSource();
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
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_source, container, false);
        lv_source = (ListView) view.findViewById(R.id.ListView_Source);

        swipeRefreshLayoutSOURCE = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_source);
        swipeRefreshLayoutSOURCE.setOnRefreshListener(this);
        swipeRefreshLayoutSOURCE.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        LayoutInflater myinflater = getLayoutInflater(getArguments());
        ViewGroup myHeader = (ViewGroup)myinflater.inflate(R.layout.listview_header_source, lv_source, false);
        lv_source.addHeaderView(myHeader, null, false);

        AdapterView.OnItemClickListener sourceAdapterViewListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= lv_source.getHeaderViewsCount();

                if (!sourceList.isEmpty()) {
                    source = sourceList.get(position);

                    FragmentHeadlines.getSource(source, getActivity());

                    //mSource = source;

                    //articleIMG = source.getImage();
                    //articleID = source.getId();
                    //rssUrl = source.getRssURL();
                    //Log.e("id ", " " + articleID);
                    adapter.clear();

                    //parseHeadlines = new ParseHeadlines(getActivity());
                    if (xmlHeadlineParser != null)
                        xmlHeadlineParser.cancel(true);

                    if (xmlSubscriptionHeadlineParser != null)
                        xmlSubscriptionHeadlineParser.cancel(true);

                    xmlHeadlineParser = new XMLHeadlineParser(getActivity(), source.getRssURL());

                    //String bitmapLoc = "ic_"+source.getId()+"_60";
                    //int resId = getActivity().getResources().getIdentifier(bitmapLoc, "drawable", getActivity().getPackageName());
                    //iv_header.setImageResource(resId);

                    //tv_header.setText(source.getTitle());
                    vpPager.setCurrentItem(1);
                }
            }
        };

            ;
            //set the listener to the list view
        lv_source.setOnItemClickListener(sourceAdapterViewListener);

        return view;
    }

    @Override
    public void onRefresh() {
        sourceList = new ArrayList<Source>();
        new ParseSource(getActivity());
        new ParseBitcoinPrice(getActivity());
    }

}

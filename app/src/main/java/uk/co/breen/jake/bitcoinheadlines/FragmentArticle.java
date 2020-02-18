package uk.co.breen.jake.bitcoinheadlines;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import static uk.co.breen.jake.bitcoinheadlines.MainActivity.articleURL;
import static uk.co.breen.jake.bitcoinheadlines.R.id.webView;

/**
 * Created by Jake on 04/03/2017.
 */

public class FragmentArticle extends Fragment {

    protected static WebView articleWebView;
    protected static TextView tv_article;

    private String title;
    private int page;
    private ProgressDialog mProgress;

    // newInstance constructor for creating fragment with arguments
    public static FragmentArticle newInstance(int page, String title) {
        FragmentArticle fragmentFirst = new FragmentArticle();
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

        mProgress = new ProgressDialog(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        articleWebView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_article, container, false);

        articleWebView = (WebView) view.findViewById(webView);
        WebSettings webSettings = articleWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        articleWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO show you progress image
                super.onPageStarted(view, url, favicon);
                mProgress = new ProgressDialog(getActivity());
                if (mProgress != null) {
                    mProgress.setMessage("Loading article...");
                    mProgress.show();
                }

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (mProgress != null && mProgress.isShowing()) {
                            mProgress.dismiss();
                        }
                    }
                }, 5000);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO hide your progress image
                super.onPageFinished(view, url);
                if (mProgress != null) {
                    mProgress.hide();
                }

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mProgress.dismiss();
                    }
                }, 5000);
            }
        });

        if (savedInstanceState != null) {
            mProgress.dismiss();
            articleWebView.restoreState(savedInstanceState);
        } else {
            articleWebView.loadUrl(articleURL);
        }

        return view;
    }
}

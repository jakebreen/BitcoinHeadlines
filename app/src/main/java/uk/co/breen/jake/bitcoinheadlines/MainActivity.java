package uk.co.breen.jake.bitcoinheadlines;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kobakei.ratethisapp.RateThisApp;
import com.readystatesoftware.viewbadger.BadgeView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.cketti.library.changelog.ChangeLog;
import uk.co.breen.jake.bitcoinheadlines.FirebaseServices.FirebaseConfig;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static uk.co.breen.jake.bitcoinheadlines.R.id.fab;
import static uk.co.breen.jake.bitcoinheadlines.R.id.viewPager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected static String articleURL = "";
    protected static String articleID = "";
    protected static String rssUrl = "";
    protected static String articleURLshort = "";
    protected static ViewPager vpPager;
    protected static ArrayList<Headline> headlineList;
    protected static ArrayList<Source> sourceList;
    protected static ArrayAdapter<Headline> adapter;
    protected static ArrayAdapter<Source> sAdapter;
    protected static ArrayList<Source> subscriptionArrayList;
    protected static FloatingActionButton FAB, shareFAB;
    protected static String lastDate, rateType;
    protected static List<String> subscriptionList = new ArrayList<String>();
    public static int notificationsEnabled, shortUrlEnabled;
    public static boolean playSound;
    protected static TextView tvPrice;
    protected static BitcoinPrice bitcoinPrice;
    protected static BadgeView badge;

    private FragmentPagerAdapter adapterViewPager;
    private ParseSource parseSource;
    private ParseHeadlines parseHeadlines;
    private SQLiteOpenHelper dataHelper;
    private SQLiteDatabase dataDB;
    private XMLSubscriptionHeadlineParser xmlSubscriptionHeadlineParser;
    private XMLHeadlineParser xmlHeadlineParser;

    protected static double verCode;
    protected static String verName;

    public MainActivity() throws PackageManager.NameNotFoundException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // RELEASE TO-DO LIST
        // 1) update gradle:app
        // 2) add patch notes to changelog_master.xml

        // Monitor launch times and interval from installation
        RateThisApp.onCreate(this);
        // If the condition is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(this);
        // Custom condition: 3 days and 5 launches
        RateThisApp.Config config = new RateThisApp.Config(3,5);
        RateThisApp.init(config);
        config.setTitle(R.string.rate_app_title);
        config.setMessage(R.string.rate_app_message);

        // Change log
        ChangeLog cl = new ChangeLog(this);
        if (cl.isFirstRun()) {
            cl.getLogDialog().show();
        }

        tvPrice = (TextView) toolbar.findViewById(R.id.custom_toolbar_title);
        rateType = getDefaultSharedPreferences(this).getString("rateTypeString", "usd");

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        verName = pInfo.versionName;
        verCode = pInfo.versionCode;

        //Set lastDate
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        String presentDate = df.format(c.getTime());
        lastDate = getDefaultSharedPreferences(this).getString("lastDate", presentDate);
        //lastDate = "Tue, 19 Jun 2017 13:00:47 +0000";
        notificationsEnabled = Integer.parseInt(getDefaultSharedPreferences(this).getString("notification", String.valueOf(1)));
        shortUrlEnabled = Integer.parseInt(getDefaultSharedPreferences(this).getString("shortURL", String.valueOf(0)));
        playSound = Boolean.parseBoolean(getDefaultSharedPreferences(this).getString("playSound", String.valueOf(true)));

        //Get subscription list
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        if (!subscriptionList.equals(null) || !subscriptionList.equals("")) {
            Set<String> defaultString  = new HashSet<String>();
            Set<String> set = prefs.getStringSet("subList", defaultString);
            subscriptionList = new ArrayList<String>(set);
        } else {
        }

        //Toast.makeText(getApplicationContext(), String.valueOf(subscriptionList), Toast.LENGTH_SHORT).show();

        headlineList = new ArrayList<Headline>();
        sourceList = new ArrayList<Source>();

        //ARRAY ADAPTER
        adapter = new ArrayAdapter<Headline>(
                this, android.R.layout.simple_list_item_1, headlineList);

        sAdapter = new ArrayAdapter<Source>(
                this, android.R.layout.simple_list_item_1, sourceList);

        View target = findViewById(R.id.fab);
        badge = new BadgeView(this, target);
        ViewCompat.setElevation(badge, 25);
        badge.setBadgePosition(0);

        FAB = (FloatingActionButton) findViewById(fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (xmlHeadlineParser != null)
                    xmlHeadlineParser.cancel(true);

                if (xmlSubscriptionHeadlineParser != null)
                    xmlSubscriptionHeadlineParser.cancel(true);

                if (adapter != null) adapter.clear();

                subscriptionArrayList = new ArrayList<Source>();

                for (Source s : sourceList) {
                    if (subscriptionList.contains(s.getId())) {
                        articleID = "subscriptions";
                        subscriptionArrayList.add(s);
                    }
                }

                if (SubscribedHeadlineCounter.Count() == 0) {
                    xmlSubscriptionHeadlineParser = new XMLSubscriptionHeadlineParser(MainActivity.this, subscriptionArrayList, 0, 0);
                } else if (SubscribedHeadlineCounter.Count() >= 1) {
                    xmlSubscriptionHeadlineParser = new XMLSubscriptionHeadlineParser(MainActivity.this, subscriptionArrayList, 1, 1);
                }

                //tv_header.setText("Your Subscriptions");
                //iv_header.setImageResource(android.R.drawable.btn_star_big_on);
                vpPager.setCurrentItem(1, true);

                Source source = new Source();
                source.setId("subscriptions");
                source.setTitle("Your Subscriptions");

                FragmentHeadlines.getSource(source, MainActivity.this);

            }
        });

        shareFAB = (FloatingActionButton) findViewById(R.id.shareFab);
        shareFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!articleURL.isEmpty() || !articleURL.equals("")) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    if (shortUrlEnabled == 1) {
                        new ShortUrlAsync(articleURL);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, articleURLshort);
                    } else {
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, articleURL);
                    }
                    startActivity(Intent.createChooser(sharingIntent, "Share URL"));
                } else {
                    Toast.makeText(getApplicationContext(), "No article selected to share. " + articleURL, Toast.LENGTH_SHORT).show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toolbar.setNavigationIcon(null);

        //DRAWER FINISH

        vpPager = (ViewPager) findViewById(viewPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setOffscreenPageLimit(2);

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(getApplicationContext(), "Selected page position: " + position, Toast.LENGTH_SHORT).show();
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == 0) {
                    FAB.setVisibility(View.VISIBLE);
                    shareFAB.setVisibility(View.GONE);
                    getSubCount();
                } else if (position == 1) {
                    FAB.setVisibility(View.GONE);
                    shareFAB.setVisibility(View.GONE);
                    badge.hide();
                } else if (position == 2) {
                    FAB.setVisibility(View.GONE);
                    shareFAB.setVisibility(View.VISIBLE);
                    badge.hide();
                }
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseConfig.TOPIC_GLOBAL);

        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            Log.e("token: ",token);
        }

        parseSource = new ParseSource(this);

        Log.e("articleID ", articleID);
        Log.e("articleURL ", articleURL);
        Log.e("lastDate ", lastDate);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_tab);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColorResource(R.color.colorPrimary);

        new ParseBitcoinPrice(this);

        //TEST CHANNEL
        //FirebaseMessaging.getInstance().subscribeToTopic("testFBM");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("testFBM");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_donate) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //ADAPTER PAGER

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return FragmentSource.newInstance(0, "Source");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return FragmentHeadlines.newInstance(1, "Headline");
                case 2: // Fragment # 1 - This will show SecondFragment
                    return FragmentArticle.newInstance(2, "Article");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return "Source";
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return "Headlines";
                case 2: // Fragment # 1 - This will show SecondFragment
                    return "Article";
                default:
                    return null;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (vpPager.getCurrentItem() == 2) {
                vpPager.setCurrentItem(1, true);
            } else if (vpPager.getCurrentItem() == 1) {
                vpPager.setCurrentItem(0, true);
            } else {
                return super.onKeyDown(keyCode, event);
            }

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        String presentDate = df.format(c.getTime());
        getDefaultSharedPreferences(this).edit().putString("lastDate", presentDate).commit();

        Log.e("presentDate ", presentDate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentSource.newInstance(0, "Source");
    }

    public static void getSubCount() {

        if (SubscribedHeadlineCounter.Count() > 0) {
            badge.setText(String.valueOf(SubscribedHeadlineCounter.Count()));
            badge.show();
        } else {
            badge.hide();
        }

    }
}

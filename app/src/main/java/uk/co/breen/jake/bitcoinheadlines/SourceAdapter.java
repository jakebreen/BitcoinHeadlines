package uk.co.breen.jake.bitcoinheadlines;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import cz.msebera.android.httpclient.client.cache.Resource;

import static uk.co.breen.jake.bitcoinheadlines.FragmentSource.lv_source;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.badge;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.lastDate;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.sAdapter;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.sourceList;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.subscriptionList;

/**
 * Created by Jake on 05/03/2017.
 */

public class SourceAdapter extends ArrayAdapter<Source> {

    protected static Bitmap mIcon11;

    private Context context;
    private List<Source> mSourceList;
    int mSplat;

    public SourceAdapter(Context context, int resource, ArrayList<Source> objects) {
        super(context, resource, objects);

        this.context = context;
        this.mSourceList = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Source source = this.mSourceList.get(position);

        String sourceDateArray = source .getDateArray();
        String sourceDateArraySub = sourceDateArray.substring(2, sourceDateArray.length() - 2);

        String[] dateArraySplit = sourceDateArraySub.split("\",\"");

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat readDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        readDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat writeDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        writeDate.setTimeZone(tz);

        int sum = 0;
        int len = dateArraySplit.length;
        for (int i = 0; i < len; ++i) {
                try {
                    //local datetime
                    Date setDate = readDate.parse(lastDate);
                    //split array datetime
                    Date strDate = writeDate.parse(dateArraySplit[i]);
                    if (strDate.after(setDate)) {
                        sum ++;
                    }
                } catch (ParseException p) {
                    Log.e("ParseException: ", String.valueOf(p));
                }
        }

        View view = null;
        View viewSub = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.source_custom, null);

        ImageView iv_image = (ImageView) view.findViewById(R.id.image);
        TextView tv_title = (TextView) view.findViewById(R.id.title);
        TextView tv_description = (TextView) view.findViewById(R.id.description);
        TextView tv_dateArray = (TextView) view.findViewById(R.id.dateArray);
        final CheckBox cb_subscribe = (CheckBox) view.findViewById(R.id.checkboxSub);
                ;
        //Check subscribed items
        if (subscriptionList.contains(source.getId())) {
            cb_subscribe.setChecked(true);
        } else {
            cb_subscribe.setChecked(false);
        }

        cb_subscribe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    //SUBSCRIBE
                    FirebaseMessaging.getInstance().subscribeToTopic(source.getId());

                    subscriptionList.add(source.getId());

                    Toast.makeText(context, "Subscribed to " + source.getTitle(),
                            Toast.LENGTH_SHORT).show();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();

                    Set<String> set = new HashSet<String>();
                    set.addAll(subscriptionList);
                    editor.putStringSet("subList", set);
                    editor.commit();
                    //MainActivity.toggleFab();
                    MainActivity.getSubCount();
                } else {
                    //UN-SUBSCRIBE
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(source.getId());

                    subscriptionList.remove(source.getId());

                    Toast.makeText(context, "Unsubscribed from " + source.getTitle(),
                            Toast.LENGTH_SHORT).show();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();

                    Set<String> set = new HashSet<String>();
                    set.addAll(subscriptionList);
                    editor.putStringSet("subList", set);
                    editor.commit();
                    //MainActivity.toggleFab();
                    MainActivity.getSubCount();
                }
            }
        });

        tv_title.setText(String.valueOf(source.getTitle()));
        tv_description.setText(String.valueOf(source.getDescription()));

        if (sum == 0) {
            tv_dateArray.setText("");
            ViewGroup.LayoutParams params = tv_dateArray.getLayoutParams();
            params.height = context.getResources().getDimensionPixelSize(R.dimen.text_view_height);
        } else if (sum == 1) {
            tv_dateArray.setText(String.valueOf(sum) + " new article.");
            tv_dateArray.setTypeface(null, Typeface.BOLD);
        } else {
            tv_dateArray.setText(String.valueOf(sum) + " new articles.");
            tv_dateArray.setTypeface(null, Typeface.BOLD);
        }

        String bitmapLoc = "ic_"+source.getId()+"_60";
        int resId = context.getResources().getIdentifier(bitmapLoc, "drawable", context.getPackageName());
        iv_image.setImageResource(resId);

        return view;
    }

    public static int getStringIdentifier(Context pContext, String pString){
        return pContext.getResources().getIdentifier(pString, "string", pContext.getPackageName());
    }
}

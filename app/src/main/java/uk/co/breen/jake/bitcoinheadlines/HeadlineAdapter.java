package uk.co.breen.jake.bitcoinheadlines;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static uk.co.breen.jake.bitcoinheadlines.MainActivity.lastDate;

/**
 * Created by Jake on 04/03/2017.
 */

public class HeadlineAdapter extends ArrayAdapter<Headline> {

    private Context context;
    private List<Headline> headlineList;

    public HeadlineAdapter(Context context, int resource, ArrayList<Headline> objects) {
        super(context, resource, objects);
        this.context = context;
        this.headlineList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Headline headline = this.headlineList.get(position);

        //View view = null;
        View view = convertView;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //view = inflater.inflate(R.layout.headline_custom, null);
        if (view == null) view = inflater.inflate(R.layout.headline_custom, parent, false);

        TextView tv_new = (TextView) view.findViewById(R.id.newArticle);
        TextView tv_title = (TextView) view.findViewById(R.id.title);
        TextView tv_dateTime = (TextView) view.findViewById(R.id.dateTime);
        TextView tv_Author = (TextView) view.findViewById(R.id.author);
        TextView tv_description = (TextView) view.findViewById(R.id.description);

        ViewGroup.LayoutParams params = tv_new.getLayoutParams();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

        String formattedDate = df.format(c.getTime());
        //Log.e("datetimeSOURCEADAPTER: ", formattedDate);

        if (headline != null || !headline.getDateTime().equals(null) || !headline.getDateTime().equals("")) {
            try {
                Date headlineDate = df.parse(headline.getDateTime());
                //Log.e(TAG, "DateArray:" + headlineDate);
                Date savedDate = df.parse(lastDate);
                if (headlineDate.after(savedDate)) {
                    tv_new.setText("New");
                } else {
                    tv_new.setText("");
                    params.height = context.getResources().getDimensionPixelSize(R.dimen.text_view_height);
                }
            } catch (ParseException p) {
                Log.e("ParseException: ", String.valueOf(p));
            }
        } else {
            tv_new.setText("");
            params.height = context.getResources().getDimensionPixelSize(R.dimen.text_view_height);
        }

        tv_title.setText(String.valueOf(headline.getTitle()));

        String dateSub = headline.getDateTime().substring(0, headline.getDateTime().length() - 20);
        tv_dateTime.setText(dateSub);
        tv_Author.setText(String.valueOf(headline.getAuthor()));
        tv_description.setText(Html.fromHtml(String.valueOf(headline.getDescription())));

        return view;
    }


}

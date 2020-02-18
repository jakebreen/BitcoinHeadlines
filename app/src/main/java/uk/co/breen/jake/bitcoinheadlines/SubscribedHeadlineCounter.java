package uk.co.breen.jake.bitcoinheadlines;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static uk.co.breen.jake.bitcoinheadlines.MainActivity.lastDate;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.sourceList;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.subscriptionList;

/**
 * Created by Jacob on 20/06/2017.
 */

public class SubscribedHeadlineCounter {

    static int headlineCount;

    public static int Count() {

        headlineCount = 0;

        for (Source s : sourceList) {
            if (subscriptionList.contains(s.getId())) {
                String sourceDateArray = s.getDateArray();
                String sourceDateArraySub = sourceDateArray.substring(2, sourceDateArray.length() - 2);

                String[] dateArraySplit = sourceDateArraySub.split("\",\"");

                Calendar cal = Calendar.getInstance();
                TimeZone tz = cal.getTimeZone();
                SimpleDateFormat readDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                readDate.setTimeZone(TimeZone.getTimeZone("UTC"));
                SimpleDateFormat writeDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                writeDate.setTimeZone(tz);

                int len = dateArraySplit.length;
                for (int i = 0; i < len; ++i) {
                    try {
                        //local datetime
                        Date setDate = readDate.parse(lastDate);
                        //split array datetime
                        Date strDate = writeDate.parse(dateArraySplit[i]);
                        if (strDate.after(setDate)) {
                            headlineCount ++;
                        }
                    } catch (ParseException p) {
                        Log.e("ParseException: ", String.valueOf(p));
                    }
                }


            }
        }
        return headlineCount;
    }

}

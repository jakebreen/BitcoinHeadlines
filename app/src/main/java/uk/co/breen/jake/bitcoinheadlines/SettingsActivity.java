package uk.co.breen.jake.bitcoinheadlines;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.bitcoinPrice;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.notificationsEnabled;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.shortUrlEnabled;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.playSound;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.tvPrice;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.verCode;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.rateType;
import static uk.co.breen.jake.bitcoinheadlines.MainActivity.verName;

/**
 * Created by Jake on 31/03/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    private PopupWindow popupWindow;
    private TextView tvCharacters;
    private EditText etFeedback, etEmail;
    private Button btnCancel, btnSubmit;
    private RadioGroup rgFeedback;
    private String feedbackType, email, emailPattern, feedback;
    private String strFeedbackDateTime;
    private SimpleDateFormat df;
    private Spinner spRate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch cb_notif = (Switch) findViewById(R.id.notificationsCB);
        Switch cb_shortUrl = (Switch) findViewById(R.id.shortUrlCB);
        TextView tv_version = (TextView) findViewById(R.id.version);
        Switch swPlaySound = (Switch) findViewById(R.id.soundSW);
        spRate = (Spinner) findViewById(R.id.rateSP);

        feedbackType = "none";

        tv_version.setText("Version: " + verName);

        if (notificationsEnabled == 1) {
            cb_notif.setChecked(true);
        } else {
            cb_notif.setChecked(false);
        }

        cb_notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notificationsEnabled = 1;
                    FirebaseMessaging.getInstance().subscribeToTopic("global");
                    getDefaultSharedPreferences(SettingsActivity.this).edit().putString("notification", String.valueOf(notificationsEnabled)).commit();
                } else {
                    notificationsEnabled = 0;
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("global");
                    getDefaultSharedPreferences(SettingsActivity.this).edit().putString("notification", String.valueOf(notificationsEnabled)).commit();
                }
            }
        });

        if (shortUrlEnabled == 1) {
            cb_shortUrl.setChecked(true);
        } else {
            cb_shortUrl.setChecked(false);
        }

        cb_shortUrl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    shortUrlEnabled = 1;
                    getDefaultSharedPreferences(SettingsActivity.this).edit().putString("shortURL", String.valueOf(shortUrlEnabled)).commit();
                } else {
                    shortUrlEnabled = 0;
                    getDefaultSharedPreferences(SettingsActivity.this).edit().putString("shortURL", String.valueOf(shortUrlEnabled)).commit();
                }
            }
        });

        if (playSound) {
            swPlaySound.setChecked(true);
        } else {
            swPlaySound.setChecked(false);
        }

        swPlaySound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    playSound = true;
                    Toast.makeText(SettingsActivity.this, String.valueOf(playSound), Toast.LENGTH_SHORT).show();
                    getDefaultSharedPreferences(SettingsActivity.this).edit().putString("playSound", String.valueOf(playSound)).commit();
                } else {
                    playSound = false;
                    Toast.makeText(SettingsActivity.this, String.valueOf(playSound), Toast.LENGTH_SHORT).show();
                    getDefaultSharedPreferences(SettingsActivity.this).edit().putString("playSound", String.valueOf(playSound)).commit();
                }
            }
        });

        spRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        rateType = "usd";
                        getDefaultSharedPreferences(SettingsActivity.this).edit().putString("rateTypeString", rateType).commit();
                        if (!rateType.isEmpty() && bitcoinPrice != null) tvPrice.setText("$" + bitcoinPrice.getUsd());
                        break;
                    case 1:
                        rateType = "gbp";
                        getDefaultSharedPreferences(SettingsActivity.this).edit().putString("rateTypeString", rateType).commit();
                        if (!rateType.isEmpty() && bitcoinPrice != null) tvPrice.setText("£" + bitcoinPrice.getGbp());
                        break;
                    case 2:
                        rateType = "eur";
                        getDefaultSharedPreferences(SettingsActivity.this).edit().putString("rateTypeString", rateType).commit();
                        if (!rateType.isEmpty() && bitcoinPrice != null) tvPrice.setText("€" + bitcoinPrice.getEur());
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (rateType.equals("usd")) spRate.setSelection(0);
        if (rateType.equals("gbp")) spRate.setSelection(1);
        if (rateType.equals("eur")) spRate.setSelection(2);

    }

        //CREATE FEEDBACK POPUP WINDOW

    public void feedbackActivity(final View view) {

        //GET LAST SUBMITTED FEEDBACK DATETIME
        strFeedbackDateTime = getDefaultSharedPreferences(this).getString("feedbackDateTime", "none");

        final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        final View popupView = layoutInflater.inflate(R.layout.popup_feedback, null);

        popupWindow = new PopupWindow(popupView,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                true);

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        tvCharacters = (TextView) popupView.findViewById(R.id.characters);
        etFeedback = (EditText) popupView.findViewById(R.id.feedback);
        etEmail = (EditText) popupView.findViewById(R.id.email);
        btnCancel = (Button) popupView.findViewById(R.id.cancel);
        btnSubmit = (Button) popupView.findViewById(R.id.submit);
        rgFeedback = (RadioGroup) popupView.findViewById(R.id.feedbackGroup);

        //COMPARE DATE OF LAST SUBMITTED FEEDBACK
        Calendar c = Calendar.getInstance();
        //UNCOMMENT TO TEST FEEDBACK DATE
        //c.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        String presentDate = df.format(c.getTime());
        if (strFeedbackDateTime.equals("none")) {
        } else {
            try {
                if (df.parse(presentDate).after(df.parse(strFeedbackDateTime))) {
                    btnSubmit.setEnabled(true);
                } else {
                    btnSubmit.setEnabled(false);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        etFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCharacters.setText(200 - s.toString().length() + " characters left.");
            }
        });

        rgFeedback.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.bug) {
                    feedbackType = "bug";
                } else if (checkedId == R.id.suggestions) {
                    feedbackType = "suggestion";
                } else if (checkedId == R.id.question) {
                    feedbackType = "question";
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                feedback = etFeedback.getText().toString();
                email = etEmail.getText().toString().trim();
                if (!isEmailValid(email)) {
                    Toast.makeText(getApplicationContext(), "Invalid email address.", Toast.LENGTH_SHORT).show();
                } else if (feedback.equals("") || feedback.equals(null)) {
                    Toast.makeText(getApplicationContext(), "Please complete the feedback field.", Toast.LENGTH_SHORT).show();
                } else if (feedbackType.equals("none")) {
                    Toast.makeText(getApplicationContext(), "Please select a topic.", Toast.LENGTH_SHORT).show();
                } else {
                    if (feedbackType.equals("bug")) {
                        Toast.makeText(getApplicationContext(), "Thank you for submitting a bug.", Toast.LENGTH_SHORT).show();
                    } else if (feedbackType.equals("suggestion")) {
                        Toast.makeText(getApplicationContext(), "Thank you for submitting a suggestion.", Toast.LENGTH_SHORT).show();
                    } else if (feedbackType.equals("question")) {
                        Toast.makeText(getApplicationContext(), "Thank you for submitting a question.", Toast.LENGTH_SHORT).show();
                    }

                    Calendar c2 = Calendar.getInstance();
                    c2.add(Calendar.DAY_OF_MONTH, 1);
                    SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                    String feedbackDateTime = df.format(c2.getTime());

                    getDefaultSharedPreferences(SettingsActivity.this).edit().putString("feedbackDateTime", feedbackDateTime).commit();

                    HttpHandler.makePost(SettingsActivity.this, email, feedbackType, feedback);

                    popupWindow.dismiss();
                    finish();
                }
            }
        });
    }

    //CHECK EMAIL VALIDITY
    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}


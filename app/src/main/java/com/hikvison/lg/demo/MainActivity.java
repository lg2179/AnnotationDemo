package com.hikvison.lg.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hikvision.lg.annotation.BindView;
import com.hikvision.lg.api.BindViewTools;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String yyyy_MM_dd_T_HH_mm_ss_SSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String yyyy_MM_dd_T_HH_mm_ss = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    @BindView(R.id.find_view_by_id_button)
    Button mButton;
    @BindView(R.id.id_textview)
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindViewTools.bind(this);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText(MessageFormat.format("{0}", mButton.getId()));
            }
        });

        String time ="2018-11-15T10:56:10.170+05:00";
        Calendar calendar =  yyyy_MM_dd_T_HH_mm_SSSZToCalendar(time);
    }

    /**
     * 将"2018-05-07T14:41:57.819+03:00"型转换为Calendar,注意将转换为手机设备所在时区的Calendar
     *
     * @param formatTime "2018-05-07T14:41:57.819+03:00"型time
     * @return Calendar
     */
    public static Calendar yyyy_MM_dd_T_HH_mm_SSSZToCalendar(String formatTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (TextUtils.isEmpty(formatTime)) {
            Log.e(TAG, "convertToCalendar : The formatting time is null");
            return calendar;
        }

        //截取为：2018-05-07T14:41:57.819
        //formatTime = formatTime.substring(0, formatTime.indexOf("+"));
        SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_dd_T_HH_mm_ss_SSSZ, Locale.getDefault());
        try {
            Date date = sdf.parse(formatTime);
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            Log.e(TAG, "convertToCalendar : The time format not 2018-05-07T14:41:57.819+03:00");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(yyyy_MM_dd_T_HH_mm_ss, Locale.getDefault());
            try {
                Date date = simpleDateFormat.parse(formatTime);
                calendar.setTime(date);
                return calendar;
            } catch (ParseException pw) {
                Log.e(TAG, "convertToCalendar : The time format not 2018-05-07T14:41:57+03:00");
                return calendar;
            }
        }
    }
}

package com.wz.timeview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TimeView timeView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeView = findViewById(R.id.timeView);

        /* 时间同步 */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true)
                    {
                        Calendar calendar = Calendar.getInstance();
                        /* 时 */
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        /* 分 */
                        int minute = calendar.get(Calendar.MINUTE);
                        /* 秒 */
                        int second = calendar.get(Calendar.SECOND);
                        /* 更新时间 */
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    timeView.setTime(hour,minute,second);
                                } catch (TimeView.InvalidTimeException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        Thread.sleep(500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
# TimeView
android时钟控件

参考这位博主的实现，做了一些删减和新增：[https://github.com/Lloyd0577/CustomClockForAndroid](https://github.com/Lloyd0577/CustomClockForAndroid)

## 使用说明
* 复制TimeView.java和attrs.xml到工程中对应的目录下。
* 在目标页面xml头，添加[xmlns:custom="http://schemas.android.com/apk/res-auto"]()，如：
    ```xml
    <androidx.constraintlayout.widget.ConstraintLayout 
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        xmlns:custom="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MainActivity">
        ...
    </androidx.constraintlayout.widget.ConstraintLayout>
    ```
* 在目标页面中添加```<TimeView>```标签对，如：
    ```xml
    <com.wz.timeview.TimeView
            android:id="@+id/timeView"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:layout_centerInParent="true"
            android:layout_gravity="center_horizontal"
            android:layout_marginStart="32dp"
            android:layout_marginTop="32dp"
            android:layout_marginEnd="32dp"
            android:layout_marginBottom="32dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            custom:borderColor="#cccccc"
            custom:borderWidth="3dp"
            custom:circleBackground="#FFF5EE"
            custom:minScaleColor="#000000"
            custom:minScaleLength="6dp"
            custom:midScaleColor="#000000"
            custom:midScaleLength="10dp"
            custom:maxScaleColor="#000000"
            custom:maxScaleLength="14dp"
            custom:isDrawText="true"
            custom:textDistanceToBorder="40dp"
            custom:textColor="#000000"
            custom:textSize="24sp"
            custom:secondPointerColor="#ff0000"
            custom:secondPointerLength="80dp"
            custom:minPointerColor="#00ff00"
            custom:minPointerLength="50dp"
            custom:hourPointerColor="#0000ff"
            custom:hourPointerLength="30dp"
            custom:centerPointColor="#000000"
            custom:centerPointRadius="2dp" />
    ```
* 以上```<TimeView>```中对应的custom:xxx属性，可在TimeView.java中找到对应注释。
* 在合适的位置添加**时间同步代码**，如：
    ```java
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
    ```
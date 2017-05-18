package cn.edu.djtu.car_park;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.djtu.car_park.util.LotInfo;


public class AllDetailActivity extends Activity {

    private CircleRefreshLayout mRefreshLayout;
    private ListView mList;
    private String empty;
    private Button navigationButton;
    private LotInfo mLotInfo;
    private String name, address;
    private int distance;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mRefreshLayout = (CircleRefreshLayout) findViewById(R.id.refresh_layout);
        mList = (ListView) findViewById(R.id.list);
        navigationButton = (Button) findViewById(R.id.navigation);

        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mLotInfo = new LotInfo();
        mRefreshLayout.setOnRefreshListener(
                new CircleRefreshLayout.OnCircleRefreshListener() {
                    @Override
                    public void refreshing() {
                        long stop = System.currentTimeMillis() + 2000;
                        Date stopDate = new Date();
                        stopDate.setTime(stop);
                        Timer timer = new Timer();
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                mRefreshLayout.finishRefreshing();
                            }
                        };
                        timer.schedule(timerTask, stopDate);
                    }

                    @Override
                    public void completeRefresh() {

                    }

                    @Override
                    public void setItems() {
                        empty = String.valueOf(mLotInfo.getEmptyNumber());
                        String[] strs = {
                                name,
                                address,
                                "距离：" + distance + "米",
                                "总泊位数：" + mLotInfo.getTotalNumber(),
                                "空泊位数：" + empty,
                                "收费标准：15分钟内免费,2.5元/30分钟,35元/天,300元/月"
                        };
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllDetailActivity.this, android.R.layout.simple_list_item_1, strs);
                        mList.setAdapter(adapter);
                    }
                });


        getIntentData();
        empty = String.valueOf(mLotInfo.getEmptyNumber());
        String[] strs = {
                name,
                address,
                "距离：" + distance + "米",
                "总泊位数：" + mLotInfo.getTotalNumber(),
                "空泊位数：" + empty,
                "收费标准：15分钟内免费,2.5元/30分钟,35元/天,300元/月"
        };
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strs);
        mList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getIntentData() {
        intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        name = bundle.getString("name");
        address = bundle.getString("address");
        distance = bundle.getInt("distance");
        mLotInfo = (LotInfo) (bundle.getSerializable("infoObject"));
    }
}

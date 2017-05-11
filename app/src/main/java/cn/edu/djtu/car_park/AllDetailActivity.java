package cn.edu.djtu.car_park;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;

/**
 * Created by Frank on 11/05/2017.
 */

public class AllDetailActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("看一看1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }
}


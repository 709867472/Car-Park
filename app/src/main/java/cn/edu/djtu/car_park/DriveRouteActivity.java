package cn.edu.djtu.car_park;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import cn.edu.djtu.car_park.overlay.DrivingRouteOverlay;
import cn.edu.djtu.car_park.util.AMapUtil;
import cn.edu.djtu.car_park.util.ToastUtil;


/**
 * Created by Frank on 22/05/2017.
 */

public class DriveRouteActivity extends Activity implements AMap.OnMapClickListener,
        AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener {

    private AMap aMap;
    private MapView mapView;
    private Context mContext;
    private Intent intent;
    private RelativeLayout mBottomLayout;
    private LatLonPoint mStartPoint;//导航时的起点，即当前位置
    private LatLonPoint mEndPoint;//导航时的终点，即目的地停车场
    private RouteSearch mRouteSearch;//导航对象
    private DriveRouteResult mDriveRouteResult;//导航结果对象
    private TextView mRotueTimeDes, mRouteDetailDes;
    private final int ROUTE_TYPE_DRIVE = 2;

    public AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
    public AMapLocationClientOption mLocationOption = null;//声明AMapLocationClientOption对象

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_route);

        mContext = this.getApplicationContext();
        mapView = (MapView) findViewById(R.id.route_map);
        mapView.onCreate(bundle);// 此方法必须重写

        init();
        setfromandtoMarker();
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
    }

    private void setfromandtoMarker() {
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mStartPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        registerListener();
        getIntentData();
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mRotueTimeDes = (TextView) findViewById(R.id.firstline);
        mRouteDetailDes = (TextView) findViewById(R.id.secondline);
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setLogoBottomMargin(-26);//隐藏logo

        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mStartPoint.getLatitude(), mStartPoint.getLongitude()), 14));
    }

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener(DriveRouteActivity.this);
        aMap.setOnMarkerClickListener(DriveRouteActivity.this);
        aMap.setOnInfoWindowClickListener(DriveRouteActivity.this);
        aMap.setInfoWindowAdapter(DriveRouteActivity.this);

    }

    private void getIntentData() {
        intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mStartPoint = (LatLonPoint) (bundle.getParcelable("mStartPoint"));
        mEndPoint = (LatLonPoint) (bundle.getParcelable("mEndPoint"));
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }


    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }


    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
//        System.out.println("现在的经纬度是："+mStartPoint.getLatitude()+","+mStartPoint.getLongitude());
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
        // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            if (mRouteSearch == null) {
                mRouteSearch = new RouteSearch(this);
            }
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null, null, "");
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }


    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths().get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            this, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(false);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.VISIBLE);
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                    mRouteDetailDes.setText("打车约" + taxiCost + "元");
                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DriveRouteActivity.this, DriveRouteDetailActivity.class);
                            intent.putExtra("drive_path", drivePath);
                            intent.putExtra("drive_result", mDriveRouteResult);
                            startActivity(intent);
                        }
                    });

                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(this, R.string.no_result);
                }

            } else {
                ToastUtil.show(this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}

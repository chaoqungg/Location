package carlocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.qht.location.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.telephony.TelephonyManager;
import android.provider.Settings.Secure;
import android.content.Context;

import application.StartApplication;
import model.Point;
import model.Result;
import util.ConstantUtil;
import util.SharePreferenceUtil;
import util.uploadParser;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {
	private MapView                  mMapView;//一个显示地图（数据源自mapbar地图服务）的视图，当被焦点选中时，它能捕获按键事件和触摸手势去平移和缩放地图
	private BaiduMap                 mBaiduMap;
	private LocationClient           locationClient;//用来发起定位，添加取消监听
	private LatLng                   p1;// 上一个点  //地理坐标基本数据结构
	private LocationMode             mCurrentMode;// 定位点图标的样式，普通箭头。
	private OverlayOptions           option;//红色mark //地图覆盖物选型基类
	private Marker                   marker; //定义地图 Marker 覆盖物基类，继承于Overlay类
	private ScheduledExecutorService scheduledExecutorService; //基于线程池设计的定时任务类,每个调度任务都会分配到线程池中的一个线程去执行,也就是说,任务是并发执行,互不影响。
	private uploadParser parse;
	private List<Point>              list;
	private double                   Latitude,Longitude;
	private String id,deviceID,operateTime;
	private boolean firstloc=true;
	private BitmapDescriptor bitmap;


	//获取当前时间
	SimpleDateFormat format;
	String time;

	//获取设备id
	TelephonyManager TelephonyMgr ;
	String szImei ;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initview();  //cq初始化参数
		initMap();	//cq初始化地图
		initPermission();
	}

	//申请权限
	private void initPermission() {
		AndPermission.with(this)
				.requestCode(200)
				.permission(Permission.LOCATION,
						Permission.STORAGE)
				.rationale(rationlistener)
		.callback(listener)
		.start();
	}

	private RationaleListener rationlistener=new RationaleListener(){

		@Override
		public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
			AndPermission.rationaleDialog(MainActivity.this, rationale).show();
		}
	};
	private PermissionListener listener = new PermissionListener() {
		@Override
		public void onSucceed(int requestCode, @NonNull List<String> grantedPermissions) {
			// 权限申请成功回调。
			// 这里的requestCode就是申请时设置的requestCode。
			// 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
			if(requestCode == 200) {
				// TODO ...
			}
		}

		@Override
		public void onFailed(int requestCode, List<String> deniedPermissions) {
			// 权限申请失败回调。
			if(requestCode == 200) {
				// 第二种：用自定义的提示语。
				AndPermission.defaultSettingDialog(MainActivity.this, 200)
						.setTitle("权限申请失败")
						.setMessage("您拒绝了我们必要的一些权限，****,请在设置中授权！")
						.setPositiveButton("好，去设置")
						.show();
			}
		}
	};

	private void initview() {
		// TODO Auto-generated method stub
		TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);

        bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.car);
		mMapView = (MapView) findViewById(R.id.mapView);
		parse=new uploadParser();
		list=new ArrayList<Point>();
		scheduledExecutorService = Executors.newScheduledThreadPool(5);//用来创建一个定长线程池，并且支持定时和周期性的执行任务。 长度20
		scheduledExecutorService.scheduleWithFixedDelay(myThread, 2, 10, TimeUnit.SECONDS);//延迟2s执行，每个5s执行一次
		requestReadPhonePermission();
	}

	private void requestReadPhonePermission() {
		if (shouldShowRequestPermissionRationale( Manifest.permission.READ_PHONE_STATE)) {
			//在这里面处理需要权限的代码
		} else {
			requestPermissions( new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case 2:
				if ((grantResults.length > 0) && (grantResults[0] == PERMISSION_GRANTED)) {
					//TODO
					szImei = TelephonyMgr.getDeviceId();
				}
				break;
			default:
				break;
		}
	}

	private  Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
				case ConstantUtil.SendOK:

					DrawPoint();
					break;
				case ConstantUtil.SendError:

					break;
			}
			super.handleMessage(msg);
		}

		private void DrawPoint() {
			// TODO Auto-generated method stub
			mBaiduMap.clear();
			for (Point point : list) {
				if(!point.getId().equals(id)){
					LatLng p=new LatLng(Double.parseDouble(point.getLatitude()), Double.parseDouble(point.getLongitude()));
					if(bitmap!=null){
						option = new MarkerOptions().position(p).icon(
								bitmap);
						mBaiduMap.addOverlay(option);
					}
				}
			}
		}
	};

	private String getXmL(){
		StringBuffer buffer=new StringBuffer();
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
		time=format.format(new Date());
		buffer.append("<root>")
				.append("<PointItem>")
				.append("<OperateID>").append(SharePreferenceUtil.getStringSP("username","")).append("</OperateID>")
				.append("<Lat>").append(Latitude).append("</Lat>")
				.append("<Lng>").append(Longitude).append("</Lng>")
				.append("<DeviceID>").append(szImei).append("</DeviceID>")
				.append("<OperateTime>").append(time).append("</OperateTime>")
				.append("<Remark>").append("备注").append("</Remark>")
				.append("</PointItem>")
				.append("</root>");
		return buffer.toString();
	}
// 找着这里写
	Runnable myThread=new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(null==p1 || p1.latitude==0){
				return;
			}
			String[] key = new String[1];
			key[0] = "XmlContent";
			Map<String, Object> value=new HashMap<String, Object>();
			value.put(key[0], getXmL());
			boolean Result=parse.summit( key, value, "UpLoadPosition");
			if(!Result){
				handler.sendEmptyMessage(ConstantUtil.SendError);
			}else{
				handler.sendEmptyMessage(ConstantUtil.SendOK);
			}
		}
	};
	/**
	 * 初始化地图数据
	 * */
	private void initMap() {
		locationClient = StartApplication.mLocationClient;
		locationClient.registerLocationListener(new MyLocationListener());
		mBaiduMap = mMapView.getMap();
		// 普通地图
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setMyLocationEnabled(true);
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 3000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于3000ms才是有效的
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIgnoreKillProcess(true);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(19));// 地图缩放级别为14
		locationClient.setLocOption(option);
		locationClient.start();
	}
	/**
	 * 定位监听*/
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation loc) {
			if (loc != null) {
				// 此处设置开发者获取到的方向信息，顺时针0-360
				MyLocationData locData = new MyLocationData.Builder()
						.accuracy(loc.getRadius()/2)
						.latitude(loc.getLatitude())
						.longitude(loc.getLongitude()).build();
				mBaiduMap.setMyLocationData(locData);
				location(loc);
			} else {
				Toast.makeText(getApplicationContext(), "定位失败，请检查手机网络或GPS设置！",
						Toast.LENGTH_LONG).show();
			}
		}
	}
	public void location(BDLocation location) {
		// TODO Auto-generated method stub
		p1 = new LatLng(location.getLatitude(),location.getLongitude());
//		LatLng p1 = new LatLng(34.27320460395923,
//		108.79243283534517);
		Latitude=location.getLatitude();
		Longitude=location.getLongitude();
//		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(p1);
//		mBaiduMap.animateMapStatus(u);
		if(firstloc){
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(p1);
			mBaiduMap.animateMapStatus(u);
			firstloc=false;
		}
//		BitmapDescriptor bitmap = BitmapDescriptorFactory
//				.fromResource(R.mipmap.fragment_trace_baidumap_mark);
//		option = new MarkerOptions().position(p1).icon(bitmap);
//		marker = (Marker)mBaiduMap.addOverlay(option);
	}
}

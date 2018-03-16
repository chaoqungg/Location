package application;

import android.app.Application;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import util.SharePreferenceUtil;

/**
 * For developer startup other SDK
 * 
 */
public class StartApplication extends Application {

	public static LocationClient mLocationClient = null;

    protected static StartApplication instance;

    @Override
    public void onCreate() {    	     
         super.onCreate();
         mLocationClient = new LocationClient(this);
         SDKInitializer.initialize(getApplicationContext());// init baidu map sdk
            SharePreferenceUtil.initSharePreferenceUtil(this);
         OkHttpClient okHttpClient = new OkHttpClient.Builder()//init OkHttp3.0
         .connectTimeout(10000L, TimeUnit.MILLISECONDS)
         .readTimeout(10000L, TimeUnit.MILLISECONDS)
        .build();
         OkHttpUtils.initClient(okHttpClient);
    }
    public static StartApplication getInstance() {
        return instance;
    }
}

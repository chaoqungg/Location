package carlocation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qht.location.R;

import android.Manifest;

public class EditActivity extends Activity {
	private SharedPreferences sp;
	private String id;
	private EditText et;
	private Button bt;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);


		//@判断是否为android6.0以上系统版本，如果是，需要动态添加权限
		if(Build.VERSION.SDK_INT>=23){
			showContacts();
		}else {
			initview();
		}
	}

	//@判断权限
	public void showContacts()
	{
		if(ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
			!=PackageManager.PERMISSION_GRANTED
			||ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
			!=PackageManager.PERMISSION_GRANTED
		    /*||ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.READ_PHONE_STATE)
            !=PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(EditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED*/){
			Toast.makeText(getApplicationContext(),"没有权限,请手动开启定位权限",Toast.LENGTH_SHORT).show();
			// 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
			ActivityCompat.requestPermissions(EditActivity.this,
					new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.ACCESS_FINE_LOCATION},1);
		}else {
			initview();
		}
	}

	//@
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			// requestCode即所声明的权限获取码，在checkSelfPermission时传入
			case 1:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
					initview();
				} else {
					// 没有获取到权限，做特殊处理
					Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动开启", Toast.LENGTH_LONG).show();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
				break;
			default:
				break;
		}
	}

	private void initview() {
		// TODO Auto-generated method stub
		et = (EditText) findViewById(R.id.et); //
		bt= (Button) findViewById(R.id.bt); //
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String id ;
				if(et.getText()==null || et.getText().equals("")){
					Toast.makeText(getApplicationContext(), "请输入球车号", Toast.LENGTH_SHORT).show();
					return;
				}
				try {
					id=et.getText().toString();
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(getApplicationContext(), "请输入正确的球车编号", Toast.LENGTH_SHORT).show();
					return;
				}
				sp = getSharedPreferences("com.location", 0);
				sp .edit().putString("id", id).commit();
				startActivity(new Intent(EditActivity.this,MainActivity.class));
				finish();
			}
		});
	}
}

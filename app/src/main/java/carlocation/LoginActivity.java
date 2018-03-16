package carlocation;

/**
 * Created by Administrator on 2018/3/14.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.qht.location.R;

import java.util.HashMap;
import java.util.Map;

import model.Point;
import util.ConstantUtil;
import util.SharePreferenceUtil;
import util.findListParser;
import util.uploadParser;

public class LoginActivity extends Activity {

    private Button btn_login;
    private EditText username,password;
    private uploadParser parse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }
    private void initView(){
        btn_login=(Button)findViewById(R.id.btn_login);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        parse=new uploadParser();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(username.getText().toString())){
                   Toast.makeText(LoginActivity.this,"用户名为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(LoginActivity.this,"密码为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                MyThread myThread =new MyThread();
                new Thread(myThread).start();
            }
        });
    }


    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case ConstantUtil.SendOK:
                    SharePreferenceUtil.setStringSP("username",username.getText().toString());
                    SharePreferenceUtil.setStringSP("password",password.getText().toString());
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    break;
                case ConstantUtil.SendError:

                    break;
            }
            super.handleMessage(msg);
        }


    };
    class MyThread implements Runnable{
        @Override
        public void run() {
            // TODO Auto-generated method stub

            String[] key = new String[2];
            key[0] = "TelePhone";
            key[1] = "PassWord";
            Map<String, Object> value=new HashMap<String, Object>();
            value.put(key[0], username.getText().toString());
            value.put(key[1], password.getText().toString());
            boolean Result=parse.summit(key,value,"Login");
            if(Result){
                handler.sendEmptyMessage(ConstantUtil.SendOK);
            }else{
                handler.sendEmptyMessage(ConstantUtil.SendError);
            }
        }
    }
}

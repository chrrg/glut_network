package cn.edu.glut.yiban.glut_network;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import cn.edu.glut.yiban.glut_network.service.GLUT_Network_Binder;

public class main extends AppCompatActivity {
    service myService;
    private static int type=0;
    private static int xiaoqu=0;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GLUT_Network_Binder glut_network_binder = (GLUT_Network_Binder) service;
            myService = glut_network_binder.getService();
            EditText usernameText =findViewById(R.id.zhanghao);
            EditText pwdText =  findViewById(R.id.password);
            Button bt =findViewById(R.id.button);
            if(myService.zh!=null)usernameText.setText(myService.zh);
            if(myService.mm!=null)pwdText.setText(myService.mm);
            if(myService.status){
                bt.setText("断开");
            }else{
                bt.setText("连接");
            }
        }
    };
//    public String getWifiIp() {
//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//        assert wifiManager != null;
//        if(!wifiManager.isWifiEnabled())return null;
//        WifiInfo wi = wifiManager.getConnectionInfo();
//        if (wi == null)return null;
//        //获取32位整型IP地址
//        int ipAdd=wi.getIpAddress();
//        if (ipAdd == 0)return null;
//        //把整型地址转换成“*.*.*.*”地址
//        String ip=intToIp(ipAdd);
//        if (ip.startsWith("0"))return null;
//        return ip;
//    }
//    private static String intToIp(int i) {
//        return (i & 0xFF ) + "." +
//                ((i >> 8 ) & 0xFF) + "." +
//                ((i >> 16 ) & 0xFF) + "." +
//                ( i >> 24 & 0xFF) ;
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.layout);
        final glut_network_tool glut_network_tool=new glut_network_tool();
        final Button bt = findViewById(R.id.button);
        final EditText usernameText = findViewById(R.id.zhanghao);
        final EditText pwdText = findViewById(R.id.password);
        final RadioButton radioButton = findViewById(R.id.radioButton);
        final RadioButton radioButton2 = findViewById(R.id.radioButton2);
        final RadioButton radioButton3 = findViewById(R.id.radioButton3);
        final RadioButton radioButton4 = findViewById(R.id.radioButton4);
        final RadioButton radioButton5 = findViewById(R.id.radioButton5);
        final RadioButton radioButton6 = findViewById(R.id.radioButton6);

        SharedPreferences preferences=getSharedPreferences("data",MODE_PRIVATE);
        //然后通过键的方式取出，后边是如果找不到的默认内容
        usernameText.setText(preferences.getString("username",""));
        pwdText.setText(preferences.getString("password",""));
        String login_Type=preferences.getString("type","");
        String login_xiaoqu=preferences.getString("xiaoqu","");
        if("".equals(login_xiaoqu)||"0".equals(login_xiaoqu)){
            main.xiaoqu=0;
            radioButton5.setChecked(true);
        }else if("1".equals(login_xiaoqu)){
            main.xiaoqu=1;
            radioButton6.setChecked(true);
        }
        switch (login_Type) {
            case "":
            case "0":
                main.type = 0;
                radioButton.setChecked(true);
                break;
            case "1":
                main.type = 1;
                radioButton2.setChecked(true);
                break;
            case "2":
                main.type = 2;
                radioButton3.setChecked(true);
                break;
            case "3":
                main.type = 3;
                radioButton4.setChecked(true);
                break;
        }
        startService(new Intent(this, service.class));
        bindService(new Intent(getBaseContext(), service.class), connection, BIND_AUTO_CREATE);


        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.type=0;
            }
        });
        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.type=1;
            }
        });
        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.type=2;
            }
        });
        radioButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.type=3;
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("断开".contentEquals(bt.getText())) {
                    myService.status = false;

                    bt.setText("连接");//断开连接
                } else if ("连接".contentEquals(bt.getText())) {



                    final String zh=usernameText.getText().toString();
                    final String mm=pwdText.getText().toString();
                    if("".equals(zh)){
                        Toast.makeText(getApplicationContext() ,"请输入校园网账号！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if("".equals(mm)){
                        Toast.makeText(getApplicationContext() ,"请输入校园网密码！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    /*String ip=getWifiIp();
                    if(ip==null){
                        Toast.makeText(getApplicationContext() ,"请连接SSID：glut_web的wifi再试！",Toast.LENGTH_LONG).show();
                        return;
                    }*/
                    /*if(!ip.startsWith("172.")&&!ip.startsWith("10.")){
                        Toast.makeText(getApplicationContext() ,"ip:"+ip+"，不在校园网ip网段内，请确认是否已经连上校园网！",Toast.LENGTH_LONG).show();
                        return;
                    }*/
                    bt.setText("连接中");//断开连接
                    glut_network_tool.Login(zh,mm, String.valueOf(main.type),String.valueOf(main.xiaoqu),new glut_network_tool.ResultCallback() {
                        @Override
                        public void getResult(boolean result, String sec, String kb) {
                            if (!result) {//未连上校园网
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bt.setText("连接");
                                    }
                                });
                                if(sec==null){
                                    toast("信号弱或网络不稳定！");
                                    return;
                                }
                                toast(sec);
                            }else{
                                myService.zh = zh;
                                myService.mm = mm;
                                myService.type = String.valueOf(main.type);
                                myService.xiaoqu= String.valueOf(main.xiaoqu);
                                myService.glut_network_tool=glut_network_tool;
                                myService.status = true;

                                SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                                editor.putString("username",zh);
                                editor.putString("password",mm);
                                editor.putString("type", String.valueOf(main.type));
                                editor.putString("xiaoqu", String.valueOf(main.xiaoqu));
                                editor.apply();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bt.setText("断开");//断开连接
                                        Toast.makeText(getApplicationContext() ,"已连接校园网上网！",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
    public void toast(final String text){
        Looper.prepare();
        Toast.makeText(getApplicationContext() ,text,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
//        stopService(new Intent(this, service.class));
    }
}
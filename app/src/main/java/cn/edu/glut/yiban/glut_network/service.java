package cn.edu.glut.yiban.glut_network;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class service extends Service {
    private GLUT_Network_Binder glut_network_binder=new GLUT_Network_Binder();
    public String zh=null;
    public String mm=null;
    public String type="0";
    public String xiaoqu="0";

    //public String domain="";
    public boolean status=false;
    public glut_network_tool glut_network_tool=null;
    Timer timer;
    @Override
    public IBinder onBind(Intent intent) {
        return glut_network_binder;
    }
    class GLUT_Network_Binder extends Binder{
        service getService() {
            return service.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        final service context = this;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!context.status)return;

                glut_network_tool.is_link(new glut_network_tool.ResultCallback(){
                    @Override
                    public void getResult(boolean result,String sec,String kb){
                        if(!result){//未连上校园网
                            glut_network_tool.Login(zh,mm,type,xiaoqu,new glut_network_tool.ResultCallback(){
                                @Override
                                public void getResult(boolean result,String sec,String kb){
                                    if(!result) {//未连上校园网
                                        if(sec==null){
                                            if(!glut_network_tool.isWifiConnect(context)){
                                                toast("Wifi未连接");
                                                return;
                                            }
                                            toast("网络不稳定");
                                            return;
                                        }
                                        toast(sec);
                                    }else{
                                        toast("已连接校园网上网！");
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }, 3000,3000);//3秒
    }
    public void toast(final String text){
        Log.i("toast",text);
        Handler handler=new Handler(Looper.getMainLooper());
        handler.post(new Runnable(){
            public void run(){
                Toast.makeText(getApplicationContext() ,text,Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroy() {
        timer.cancel();
        glut_network_tool.Logout(new glut_network_tool.ResultCallback(){
            @Override
            public void getResult(boolean result,String sec,String kb){

            }
        });
        Toast.makeText(getApplicationContext() ,"已注销校园网账号！",Toast.LENGTH_SHORT).show();
        //toast("已注销校园网账号！");
        super.onDestroy();
    }
}

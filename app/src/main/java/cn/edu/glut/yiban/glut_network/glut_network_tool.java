package cn.edu.glut.yiban.glut_network;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
class glut_network_tool {
    private ResultCallback resultcallback;
    private String domain="";
    // 状态变化监听
    interface ResultCallback {
        // 回调方法
        void getResult(boolean result,String sec,String kb);
    }
    void is_link(ResultCallback resultCallback){
        this.resultcallback = resultCallback;
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url("http://"+domain).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                resultcallback.getResult(false,null,null);
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                assert response.body() != null;
                String res=response.body().string();
                if(res.contains("NID='")) {
                    String sec = (res.split("time='")[1]).split("'")[0];
                    String kb = (res.split("flow='")[1]).split("'")[0];
                    resultcallback.getResult(true, sec,kb);
                }else{
                    resultcallback.getResult(false,null,null);
                }
            }
        });
    }
//    private static String encodeURIComponent(String s) {
//        String result;
//        try {
//            result = URLEncoder.encode(s, "UTF-8")
//                    .replaceAll("\\+", "%20")
//                    .replaceAll("%21", "!")
//                    .replaceAll("%27", "'")
//                    .replaceAll("%28", "(")
//                    .replaceAll("%29", ")")
//                    .replaceAll("%7E", "~");
//        }
//        catch (UnsupportedEncodingException e) {
//            result = s;
//        }
//        return result;
//    }
    void Login(String username, String password,String loginType,String xiaoqu, final ResultCallback resultCallback){
        if("0".equals(xiaoqu)){
            domain="172.16.2.2";
        }else if("1".equals(xiaoqu)) {
            domain="202.193.80.124";
        }
        this.resultcallback = resultCallback;
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3,TimeUnit.SECONDS).readTimeout(3, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url("http://"+domain+"/drcom/login?callback=dr1004&DDDDD=" + username + "&upass=" + password + "&0MKKey=123456&R1=0&R3=" + loginType + "&R6=0&para=00&v6ip=&v=8239").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                resultcallback.getResult(false,null,null);
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                assert response.body() != null;
                String res=response.body().string();
                if("".equals(res)){
                    resultcallback.getResult(false,"1",null);//未处理 或 登录过程中不常见的错误。再试一下？
                    return;
                }
                if(!res.contains("dr1004(")){
                    resultcallback.getResult(false,"1",null);//未处理 或 登录过程中不常见的错误。再试一下？
                    return;
                }
                String json="{"+res.split("dr1004\\(\\{")[1].split("\\}\\)")[0]+"}";
                try {
                    JSONObject obj=new JSONObject(json);
                    if(obj.has("result")&&"1".equals(String.valueOf(obj.get("result")))){//链接成功
                        is_link(resultCallback);
//                        return;
                    }else{
                        resultcallback.getResult(false, errParse(String.valueOf(obj.get("msga"))),null);//未处理 或 登录过程中不常见的错误。再试一下？
//                        return;
                    }
                } catch (JSONException e) {
                    resultcallback.getResult(false,"JSON解析异常！"+json,null);//未处理 或 登录过程中不常见的错误。再试一下？
                    e.printStackTrace();
//                    return;
                }


//                if(res.contains("LoginID_3")){
//                    is_link(resultCallback);
//                }else if(res.contains("Msg=01")&&res.contains("msga='userid error1'")){
//                    resultcallback.getResult(false,"-2",null);//账号不存在
//                }else if(res.contains("Msg=01")&&res.contains("msga='userid error2'")){
//                    resultcallback.getResult(false,"0",null);//账号或密码不正确，请重新输入！
//                }else if(res.contains("msga='[02],")){
//                    resultcallback.getResult(false,"-1",null);//本帐号只能在指定 IP 段使用!可能因为您的账号是学生账号，请换个账号试试。
//                }else{//未处理 或 登录过程中不常见的错误。再试一下？
//                    resultcallback.getResult(false,"1",null);
//                }
            }
        });
    }
    boolean isWifiConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo;
        if (connManager != null) {
            mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifiInfo != null) {
                return mWifiInfo.isConnected();
            }
        }
        return false;
    }
    private String errParse(String str){
        if(str==null)return "网络异常！";
        String[] errText="|SESSION已过期,请重新登录|no errcode|AC认证失败|Authentication Fail ErrCode=04|上网时长/流量已到上限|Authentication Fail ErrCode=05|您的账号已停机，造成停机的可能原因： 1、用户欠费停机 2、用户报停 需要了解具体原因，请访问自助服务系统。|Authentication Fail ErrCode=09|本账号费用超支，禁止使用|Authentication Fail ErrCode=11|不允许Radius登录|Authentication Fail ErrCode=80|接入服务器不存在|Authentication Fail ErrCode=81|LDAP认证失败|Authentication Fail ErrCode=85|账号正在使用|Authentication Fail ErrCode=86|绑定IP或MAC失败|Authentication Fail ErrCode=88|IP地址冲突|Authentication Fail ErrCode=94|接入服务器并发超限|err(2)|请在指定的登录源地址范围内登录|err(3)|请在指定的IP登录|err(7)|请在指定的登录源VLAN范围登录|err(10)|请在指定的Vlan登录|err(11)|请在指定的MAC登录|err(17)|请在指定的设备端口登录|userid error1|账号不存在|userid error2|密码错误|userid error3|密码错误|auth error4|用户使用量超出限制|auth error5|账号已停机|auth error9|时长流量超支|auth error80|本时段禁止上网|auth error99|用户名或密码错误|auth error198|用户名或密码错误|auth error199|用户名或密码错误|auth error258|账号只能在指定区域使用|auth error|用户验证失败|set_onlinet error|用户数超过限制|In use|登录超过人数限制|port err|上课时间不允许上网|can not use static ip|不允许使用静态IP|[01], 本帐号只能在指定VLANID使用(0.4095)|本帐号只能在指定VLANID使用|Mac, IP, NASip, PORT err(6)!|本帐号只能在指定VLANID使用|wuxian OLno|VLAN范围控制账号的接入数量超出限制|Oppp error: 1|运营商账号密码错误，错误码为：1|Oppp error: 5|运营商账号在线，错误码为：5|Oppp error: 18|运营商账号密码错误，错误码为：18|Oppp error: 21|运营商账号在线，错误码为：21|Oppp error: 26|运营商账号被绑定，错误码为：26|Oppp error: 29|运营商账号锁定的用户端口NAS-Port-Id错误，错误码为：29|Oppp error: userid inuse|运营商账号已被使用|Oppp error: can't find user|运营商账号无法获取或不存在|bind userid error|绑定运营商账号失败|Oppp error: TOO MANY CONNECTIONS|运营商账号在线|Oppp error: Timeout|运营商账号状态异常(欠费等)|Oppp error: User dial-in so soon|运营商账号刚下线|Oppp error: SERVICE SUSPENDED|欠费暂停服务|Oppp error: open vpn session fail!|运营商账号已欠费,请充值|Oppp error: INVALID LOCATION.|运营商锁定的用户端口错误|Oppp error: 99|帐号绑定域名错误，请联系运营商检查或解绑。|error5 waitsec <3|登录过于频繁，请等候重新登录。".split("\\|");
        int l=errText.length/2;
        for(int i=0;i<l;i++)
            if(errText[i*2].equals(str))return errText[i*2+1];
        return str;
    }
    void Logout(ResultCallback resultCallback){
        this.resultcallback = resultCallback;
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3,TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url("http://"+domain+"/drcom/logout?callback=dr1003&v=5023").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                resultcallback.getResult(false,null,null);
            }
            @Override
            public void onResponse(Call call, final Response response){
                resultcallback.getResult(true,null,null);
            }
        });
    }

}

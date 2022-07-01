package com.example.shiyan4;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FindPassActivity extends AppCompatActivity implements View.OnClickListener {
    //一些控件
    private Button btn_submit;
    private Button btn_backtohome;
    private Button btn_check;
    private EditText et_phonenum;
    private EditText et_verifycode;
    private EditText et_username;
    //客户端
    private OkHttpClient client = new OkHttpClient();
    private TimerTask tt;
    private Timer tm;
    private int TIME = 60;//倒计时60s这里应该多设置些因为mob后台需要60s,我们前端会有差异的建议设置90，100或者120
    public String country="86";//这是中国区号，如果需要其他国家列表，可以使用getSupportedCountries();获得国家区号

    private static final int CODE_REPEAT = 1; //重新发送
    private static final int NOPERSON = 61;//用户不存在
    private static final int NOMATCH = 62;//用户与电话号码不匹配
    private static final int MATCH = 63;
    private String username;
    private String phone;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpass);
        MobSDK.init(this,"3515f57257374","a581bb5035db6a062fbe625b24847c94");//注册自己的
        SMSSDK.registerEventHandler(eh); //注册短信回调（记得销毁，避免泄露内存）
        MobSDK.submitPolicyGrantResult(true, null);
        //控件初始化
        btn_submit = findViewById(R.id.submit);
        btn_backtohome = findViewById(R.id.backmain);
        btn_check = findViewById(R.id.get_code);
        et_phonenum = findViewById(R.id.et_findbyphone);
        et_verifycode = findViewById(R.id.et_code);
        et_username = findViewById(R.id.et_findbyusername);
        //设置监听器
        btn_check.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_backtohome.setOnClickListener(this);

    }

    /**
     * 弹框
     * @param str
     */
    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FindPassActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 验证成功，跳转到设置密码的页面
     * @param username
     * @param telenum
     */
    public void ForgetPasswordRequest(final String username, final String telenum){
        System.out.println("i'm flag");
        Intent intent = new Intent(FindPassActivity.this,SetPassActivity.class);
        intent.putExtra("username",username);
        startActivity(intent);
    }

    /**
     * 修改UI线程
     */
    Handler hd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CODE_REPEAT) {
                btn_check.setEnabled(true);
                btn_submit.setEnabled(true);
                tm.cancel();//取消任务
                tt.cancel();//取消任务
                TIME = 60;//时间重置
                btn_check.setText("Get again");
            }else if(msg.what == NOPERSON){
                toast("用户名不存在");
            }else if(msg.what == NOMATCH){
                toast("电话号码与用户名不匹配");
            }else if(msg.what == MATCH){
                //接下来要判断电话号码格式是否正确
                if (!TextUtils.isEmpty(phone)) {
                    //定义需要匹配的正则表达式规则
                    String mobile_sample = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
                    //正则表达式编译成模板
                    Pattern pattern = Pattern.compile(mobile_sample);
                    //把需要匹配的字符给模板匹配，获得匹配器
                    Matcher matcher = pattern.matcher(phone);
                    //通过匹配器查找是否有该字符，不可重复调用matcher.find()
                    if (matcher.find()) {//匹配手机号是否存在
                        alterWarning();
                    } else {
                        toast("电话号码格式错误");
                    }
                } else{
                    toast("请输入电话号码");
                }
            }
            else {
                btn_check.setText(TIME + "s again");
            }
        }
    };
    /**
     * 短信验证码回调
     */
    EventHandler eh=new EventHandler(){
        @Override
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    toast("验证成功");
                    ForgetPasswordRequest(username,phone);
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){       //获取验证码成功
                    toast("获取验证码成功");
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//如果你调用了获取国家区号类表会在这里回调
                    //返回支持发送验证码的国家列表
                }
            }else{//错误等在这里（包括验证失败）
                //错误码请参照http://wiki.mob.com/android-api-错误码参考/这里我就不再继续写了
                ((Throwable)data).printStackTrace();
                String str = data.toString();
                //toast(str);
                toast("验证码错误");
            }
        }
    };

    /**
     * 弹框确定下发
     */
    private void alterWarning() {
        //构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("通知"); //设置标题
        builder.setMessage(phone + " 将会获取到验证码"); //设置内容
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                //通过sdk发送短信验证（请求获取短信验证码，在监听（eh）中返回）
                SMSSDK.getVerificationCode(country, phone);
                //做倒计时操作
                Toast.makeText(FindPassActivity.this, "已发送", Toast.LENGTH_SHORT).show();
                btn_check.setEnabled(false);
                btn_submit.setEnabled(true);
                tm = new Timer();
                tt = new TimerTask() {
                    @Override
                    public void run() {
                        hd.sendEmptyMessage(TIME--);
                    }
                };
                tm.schedule(tt,0,1000);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(FindPassActivity.this, "已取消" , Toast.LENGTH_SHORT).show();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.get_code:
                username = et_username.getText().toString();
                phone = et_phonenum.getText().toString().trim().replace("/s","");

                new Thread(){
                    @Override
                    public void run() {
                        try {
                            super.run();
                            Message message = Message.obtain();

                            String url = "http://47.98.101.147:8081/users/find?username=" + username;
                            JSONObject jsonresult = get(url);
                            if (jsonresult == null) {//用户不存在
                                message.what = NOPERSON;
                            } else {
                                //再判断用户和手机号是否匹配
                                JSONObject jsonperson = get("http://47.98.101.147:8081/person/find?username=" + username);
                                String telephone = jsonperson.getString("teleno");
                                if (!phone.equals(telephone)){
                                    message.what = NOMATCH;
                                }else{
                                    message.what = MATCH;
                                }
                            }
                            hd.sendMessage(message);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.submit:
                //获取用户输入的验证码
                String code = et_verifycode.getText().toString().replace("/s","");
                if (!TextUtils.isEmpty(code)) {//判断验证码是否为空
                    //验证
                    SMSSDK.submitVerificationCode( country,phone,code);
                }else{//如果用户输入的内容为空，提醒用户
                    toast("请输入验证码");
                }
                break;
            case R.id.backmain:
                startActivity(new Intent(FindPassActivity.this,MainActivity.class));
                break;
        }
    }

    /**
     * 销毁信息注册
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销回调接口registerEventHandler必须和unregisterEventHandler配套使用，否则可能造成内存泄漏。
        SMSSDK.unregisterEventHandler(eh);
    }

    /**
     * 处理get请求
     * @param url
     * @return
     */
    public JSONObject get(String url){
        try {
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
            System.out.println("jsonObject:"+jsonObject);
            return jsonObject;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}

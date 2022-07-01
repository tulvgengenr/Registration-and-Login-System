package com.example.shiyan4;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ComponentActivity;

import org.json.JSONObject;

import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    //编码
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //请求
    private static final int REGISTER = 1;
    //message
    private static final int REGISTERSUCCESS = 1;
    private static final int PERSONHAD = 2;
    //控件
    EditText edt_username;
    EditText edt_name;
    EditText edt_age;
    EditText edt_teleno;
    EditText edt_pass1;
    EditText edt_pass2;
    //按钮
    Button btn_quit;
    Button btn_register;
    //客户端
    OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //控件
        edt_username = (EditText) findViewById(R.id.et_username);
        edt_name = (EditText) findViewById(R.id.et_name);
        edt_age = (EditText) findViewById(R.id.et_age);
        edt_teleno = (EditText) findViewById(R.id.et_phone);
        edt_pass1 = (EditText) findViewById(R.id.et_pass1);
        edt_pass2 = (EditText) findViewById(R.id.et_pass2);
        btn_register = (Button) findViewById(R.id.button1);
        btn_quit = (Button) findViewById(R.id.button2);
        //监听器
        btn_register.setOnClickListener(this);
        btn_quit.setOnClickListener(this);
    }

    /**
     * 点击事件处理
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1://注册
                //获取组件内容
                String username = String.valueOf(edt_username.getText());
                String name = String.valueOf(edt_name.getText());
                Integer age;
                if(edt_age.getText().toString().isEmpty()){
                    age = null;
                }
                else{
                    age = Integer.parseInt(String.valueOf(edt_age.getText()));
                }
                String teleno = edt_teleno.getText().toString();
                String pass1 = edt_pass1.getText().toString();
                String pass2 = edt_pass2.getText().toString();
                register(username,name,age,teleno,pass1,pass2);
                break;
            case R.id.button2://取消
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                break;
        }
    }

    /**
     * 登录操作
     * @param username
     * @param name
     * @param age
     * @param teleno
     * @param pass1
     * @param pass2
     */
    public void register(String username, String name, Integer age, String teleno, String pass1, String pass2){
        if(check()) {//格式上的check
            new Thread() {
                @Override
                public void run() {
                    try {
                        super.run();
                        Message message = Message.obtain();
                        message.what = REGISTER;
                        //先判断username有没有已经注册过
                        String url0 = "http://47.98.101.147:8081/users/find?username="+username;
                        JSONObject jsonresult = get(url0);
                        if(jsonresult!=null){
                            message.obj = PERSONHAD;
                        }
                        else {
                            String url1;
                            if(age==null && teleno.isEmpty()) {
                                url1 = "http://47.98.101.147:8081/person/add2?username=" + username + "&name=" + name;
                            }
                            else if(age == null){
                                url1 = "http://47.98.101.147:8081/person/add3_teleno?username=" + username + "&name=" + name + "&teleno=" + teleno;
                            }
                            else if(teleno.isEmpty()){
                                url1 = "http://47.98.101.147:8081/person/add3_age?username=" + username + "&name=" + name + "&age=" + age;
                            }
                            else{
                                url1 = "http://47.98.101.147:8081/person/add4?username=" + username + "&name=" + name + "&age=" + age + "&teleno=" + teleno;
                            }
                            post(url1, "");//person和users表中创建新用户
                            if(!pass1.isEmpty()) {
                                System.out.println("pass1"+pass1);
                                String url2 = "http://47.98.101.147:8081/users/update?username=" + username + "&pass=" + pass1;
                                post(url2, "");
                            }
                            message.obj = REGISTERSUCCESS;
                        }
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
    /**
     * post请求
     * @param url
     * @return
     */
    private void post(String url,String json){
        try {
            RequestBody body = RequestBody.create(JSON,json);
            Request request = new Request.Builder().url(url).post(body).build();
            Response response = client.newCall(request).execute();
            System.out.println("post请求返回: "+response.body().string());
        }catch (Exception e){
            e.printStackTrace();
        }
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
            System.out.println("response:"+response.body().toString());
            JSONObject jsonObject = new JSONObject(response.body().string());
            System.out.println("jsonObject:"+jsonObject);
            return jsonObject;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 处理message
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REGISTER:
                    int flag = (int) msg.obj;
                    if (flag == REGISTERSUCCESS){
                        System.out.println("注册成功");
                        Intent intent = new Intent(RegisterActivity.this,SuccessActivity.class);
                        String message = "注册成功";
                        intent.putExtra("message",message);
                        startActivity(intent);
                    }
                    else if (flag == PERSONHAD){
                        toast("用户名已存在");
                    }
                    break;
            }
        }
    };

    /**
     * 检验是否符合格式
     * @return 布尔值
     */
    public boolean check(){
        boolean isok = false;
        String username = edt_username.getText().toString();
        if(username.isEmpty()){
            toast("用户名为空");
            return isok;
        }
        else {
            //判断username是否符合规范
            String mess1 = "";//弹框内容
            String pattern1 = "^[a-zA-Z][a-zA-Z\\d_]*";
            String pattern2 = ".*[A-Z].*";
            boolean match1 = Pattern.matches(pattern1, username);
            boolean match2 = Pattern.matches(pattern2, username);
            if (username.length() < 5 || username.length() > 10) {
                mess1 += "用户长度要求5-10位";
            }
            if (!match1) {
                mess1 += "用户名必须以英文字母开头，并且只允许包含英文字母、数字和下划线_";
            }
            if (!match2) {
                mess1 += "用户名至少包含一个大写英文字母";
            }
            if (!mess1.isEmpty()) {
                toast(mess1);
                return isok;
            }
        }
            //判断name是否符合规范
            String name = edt_name.getText().toString();
            if (name.isEmpty()){
                toast("姓名为空");
                return isok;
            }

            //如果填写了年龄
            if(!edt_age.getText().toString().isEmpty()){
                int age = Integer.valueOf(edt_age.getText().toString());
                String pattern4 = "^([1-9]\\d|\\d)$";
                boolean match4 = Pattern.matches(pattern4, String.valueOf(age));
                if (!match4) {
                    toast("年龄只能处于0-99范围");
                    return isok;
                }
            }

            //如果填写了电话号码
            String teleno = edt_teleno.getText().toString();
            if (!teleno.isEmpty()){
                String pattern5 = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
                boolean match5 = Pattern.matches(pattern5,teleno);
                if (!match5){
                    toast("电话号码格式不正确");
                    return isok;
                }
            }

            //判断密码是否符合规范
            String pass1 = edt_pass1.getText().toString();
            if(!pass1.isEmpty()){
                String mess = "";
                if (pass1.length()<6 || pass1.length()>12){
                    mess += "密码要求长度为6-12位";
                }
                String pattern6 = "[a-zA-Z\\d_]*";
                boolean match6 = Pattern.matches(pattern6,pass1);
                if(!match6){
                    mess += "密码要求只能包含英文字母、数字和下划线_";
                }
                if(!mess.isEmpty()){
                    toast(mess);
                    return isok;
                }
            }

            //判断两次输入密码是否一致
            String pass2 = edt_pass2.getText().toString();
            if(!pass1.equals(pass2)){
                toast("两次密码输入不一致");
                return isok;
            }
            else{
                isok = true;
                return isok;
            }
        }

    /**
     * 显示弹框
     * @param str
     */
    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

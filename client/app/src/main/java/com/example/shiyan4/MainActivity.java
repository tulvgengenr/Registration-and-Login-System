package com.example.shiyan4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //请求
    private static final int VAL = 1;
    //message
    private static final int NOPERSON = 1;
    private static final int WRONGPASS = 2;
    private static final int LOGINSUCCESS = 3;
    //控件
    private EditText et_username;
    private EditText et_pass;
    private Button btn_login;
    private Button btn_register;
    private ImageButton btn_clear_username;
    private ImageButton btn_see_pass;
    private Button btn_find_pass;
    //客户端
    private OkHttpClient client = new OkHttpClient();
    //设置密码可见/不可见
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //控件
        et_username = (EditText) findViewById(R.id.editTextTextPersonName);
        et_pass = (EditText) findViewById(R.id.editTextTextPassword);
        btn_login = (Button) findViewById(R.id.button1);
        btn_register = (Button) findViewById(R.id.button2);
        btn_clear_username = (ImageButton) findViewById(R.id.imageButton3);
        btn_see_pass = (ImageButton) findViewById(R.id.imageButton4);
        btn_find_pass = (Button) findViewById(R.id.button_findpass);
        //监听器
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_clear_username.setOnClickListener(this);
        btn_see_pass.setOnClickListener(this);
        btn_find_pass.setOnClickListener(this);
        //设置密码可见/不可见
        et_pass.setHorizontallyScrolling(true);//设置et_pass不换行
    }

    /**
     * 点击事件处理
     * @param v
     */
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()){
                case R.id.button1://登录按钮
                    verify(String.valueOf(et_username.getText()),String.valueOf(et_pass.getText()));
                    break;
                case R.id.button2://注册按钮
                    startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                    break;
                case R.id.imageButton3://清空username
                    et_username.setText("");
                    break;
                case R.id.imageButton4://密码视为（可见/不见）
                    if(flag == true){
                        et_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//密码不可见
                        flag = false;
                        //更换小眼睛图片
                        Resources resources = this.getApplicationContext().getResources();
                        Drawable drawable = resources.getDrawable(R.drawable.unseeable);
                        btn_see_pass.setBackground(drawable);
                    }else {
                        et_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());//密码可见
                        flag = true;
                        Resources resources = this.getApplicationContext().getResources();
                        Drawable drawable = resources.getDrawable(R.drawable.seeable);
                        btn_see_pass.setBackground(drawable);
                    }
                    break;
                case R.id.button_findpass://忘记密码短信验证码找回
                    startActivity(new Intent(MainActivity.this,FindPassActivity.class));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 判断账号与密码是否匹配
     * @param username
     * @param pass
     * @return NOPERSON,WRONGPASS,LOGINSUCCESS
     */
    public void verify(String username,String pass){
        new Thread(){
            @Override
            public void run() {
                try {
                    super.run();
                    //get请求
                    String url = "http://47.98.101.147:8081/users/find?username="+username;
                    JSONObject jsonresult = get(url);
                    //向主线程传递信息true or false
                    Message message = Message.obtain();
                    message.what =VAL;
                    if(jsonresult == null){
                        message.obj = NOPERSON;
                    } else if (!pass.equals(jsonresult.getString("pass"))){
                        message.obj = WRONGPASS;
                    } else{
                        message.obj = LOGINSUCCESS;
                    }
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
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


    /**
     * 处理message
     * 对UI线程的操作均在Handler里面
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case VAL:
                    int flag = (int) msg.obj;
                    if(flag == NOPERSON){
                        Toast toast = Toast.makeText(MainActivity.this,"用户未注册",Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP,0,150);
                        toast.show();
                    } else if(flag == WRONGPASS){
                        Toast toast = Toast.makeText(MainActivity.this,"密码错误",Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP,0,150);
                        toast.show();
                        et_pass.setText("");
                    } else if(flag == LOGINSUCCESS){//登录成功，获取name，并跳转页面
                        new Thread(){
                            @Override
                            public void run() {
                                try {
                                    super.run();
                                    System.out.println("登录成功");
                                    JSONObject jsonperson = get("http://47.98.101.147:8081/person/find?username="+String.valueOf(et_username.getText()));
                                    String name = jsonperson.getString("name");
                                    Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
                                    intent.putExtra("name",name);
                                    startActivity(intent);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }.start();

                    }
                    break;
            }
        }
    };
}
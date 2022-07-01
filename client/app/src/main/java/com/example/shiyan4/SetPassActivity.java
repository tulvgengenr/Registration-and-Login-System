package com.example.shiyan4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SetPassActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_submit;
    Button btn_cancel;
    EditText edt_pass1;
    EditText edt_pass2;
    private String username;
    private OkHttpClient client = new OkHttpClient();
    private static final int SUCCESS = 1;
    //编码
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpass);
        //控件
        btn_submit = (Button) findViewById(R.id.button_submit_reset);
        btn_cancel = (Button) findViewById(R.id.button_cancel_reset);
        edt_pass1 = (EditText) findViewById(R.id.et_resetpass1);
        edt_pass2 = (EditText) findViewById(R.id.et_resetpass2);
        //获取传过来的username和client
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        //监听器
        btn_submit.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_cancel_reset:
                startActivity(new Intent(SetPassActivity.this,MainActivity.class));
                break;
            case R.id.button_submit_reset:
                if(check()){
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                super.run();
                                String pass1 = edt_pass1.getText().toString();
                                String url = "http://47.98.101.147:8081/users/update?username=" + username + "&pass=" + pass1;
                                post(url,"");
                                Intent intent = new Intent(SetPassActivity.this,SuccessActivity.class);
                                intent.putExtra("message","修改成功");
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
    /**
     * 显示弹框
     * @param str
     */
    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SetPassActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
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
     * 检测密码格式
     */
    public boolean check(){
        boolean isok = false;
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
}

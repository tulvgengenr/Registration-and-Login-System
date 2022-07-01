package com.example.shiyan4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SuccessActivity extends AppCompatActivity {
    Button btn_backlogin;
    TextView text_success;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        //控件
        btn_backlogin = (Button) findViewById(R.id.button_back);
        text_success = (TextView) findViewById(R.id.success_message);
        //获取Activity传过来的message,并显示出来
        Intent intent = getIntent();
        text_success.setText(intent.getStringExtra("message"));
        //监听器
        btn_backlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuccessActivity.this,MainActivity.class));
            }
        });
    }
}

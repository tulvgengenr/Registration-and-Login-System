package com.example.shiyan4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    //显示name
    TextView nameview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        //控件
        nameview = findViewById(R.id.textView_name);
        //获取MainActivity传过来的name
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        nameview.setText(name);
    }
}

package com.hbck.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private static final int CODE_LOCATION = 1;
    private TextView tv_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_location = findViewById(R.id.tv_location);
        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                startActivityForResult(intent, CODE_LOCATION);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_LOCATION && resultCode == RESULT_OK) {
            if (data != null) {
                String address = data.getStringExtra("address");
                if (TextUtils.isEmpty(address)) {
                    tv_location.setText("添加地点");
                    tv_location.setTextColor(getResources().getColor(R.color.gray));
                }else {
                    tv_location.setText(address);
                    tv_location.setTextColor(getResources().getColor(R.color.black));
                }

            }
        } else {
            tv_location.setText("添加地点");
            tv_location.setTextColor(getResources().getColor(R.color.gray));
        }
    }
}

package com.jsj.txh;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class About extends AppCompatActivity {
    private Button btnAbout;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private Context mContext;
    private LinearLayout taBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mContext = About.this;

        this.btnAbout = this.findViewById(R.id.btn_about_about);
        this.taBack = this.findViewById(R.id.ta_about_back);

        btnAbout.setOnClickListener(view -> {
            alert = null;
            builder = new AlertDialog.Builder(mContext);
            alert = builder.setIcon(R.drawable.icon)
                    .setTitle("作者")
                    .setMessage("滕玺贺 19计算机2")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(mContext, "你点击了取消按钮~", Toast.LENGTH_SHORT).show();
                        }
                    }).create();             //创建AlertDialog对象
            alert.show();
        });
        taBack.setOnClickListener(view -> {
            finish();
        });
    }
}
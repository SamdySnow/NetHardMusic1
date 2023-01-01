package com.jsj.txh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.TextView;

public class Index extends AppCompatActivity {
    PlayerService.PlayerBinder playerBinder;
    private Thread thread;
    private PlayerConnection playerConnection;

    private TextView tvNowPlaying;

    private final Handler handler = new Handler(msg -> {
        if (msg.what == 110) {
            tvNowPlaying.setText((CharSequence) msg.obj);
        }
        //super.handleMessage(msg);
        return false;
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        this.playerConnection = new PlayerConnection();
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent,playerConnection,BIND_AUTO_CREATE);

        this.tvNowPlaying = this.findViewById(R.id.index_now_playing);

        //updateNowPlaying();

    }

    private void updateNowPlaying(){
        this.thread = new Thread(){
            @Override
            public void run() {
                while(!interrupted()){
                    String Hello = playerBinder.getHello();
                    Message message = Message.obtain();
                    message.obj = Hello;
                    message.what = 110;
                    handler.sendMessage(message);
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                super.run();
                }
            }
        };
        this.thread.start();
    }

    private class PlayerConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            playerBinder = (PlayerService.PlayerBinder) iBinder;
            updateNowPlaying();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    }
}
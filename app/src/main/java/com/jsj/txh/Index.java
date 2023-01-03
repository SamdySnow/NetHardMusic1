package com.jsj.txh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Index extends AppCompatActivity {
    PlayerService.PlayerBinder playerBinder;
    private Thread thread;
    private PlayerConnection playerConnection;

    private TextView tvNowPlaying;
    private RelativeLayout taNowPlaying;
    private RelativeLayout taAllSongs;
    private RelativeLayout taFav;
    private RelativeLayout taAlbums;
    private RelativeLayout taAbout;
    private RelativeLayout taAdd;

    private final Handler handler = new Handler(msg -> {
        if (msg.what == 110) {
            if (msg.obj == null){
                taNowPlaying.setVisibility(View.GONE);
                taNowPlaying.setClickable(false);
            }else{
                taNowPlaying.setVisibility(View.VISIBLE);
                taNowPlaying.setClickable(true);
                tvNowPlaying.setText((CharSequence) msg.obj);
            }
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
        this.taNowPlaying = this.findViewById(R.id.ta_Now_Playing);
        this.taAllSongs = this.findViewById(R.id.ta_all_music);
        this.taFav = this.findViewById(R.id.ta_Favorite);
        this.taAbout = this.findViewById(R.id.ta_about);
        this.taAlbums = this.findViewById(R.id.ta_albums);
        this.taAdd = this.findViewById(R.id.ta_add_songs);

        taAdd.setOnClickListener(view -> {
            Intent intent1 = new Intent(Index.this,Add_Song.class);
            startActivity(intent1);
        });


        taNowPlaying.setOnClickListener(view -> {
            Intent intent1 = new Intent(Index.this,NowPlaying_Activity.class);
            startActivity(intent1);
        });

        taAllSongs.setOnClickListener(view -> {
            Intent intent1 = new Intent(Index.this,AllSongs.class);
            Bundle bundle = new Bundle();
            bundle.putInt("TAG",0);
            bundle.putString("CONTENT","ALl Songs");
            startActivity(intent1);
        });
        //updateNowPlaying();
        taFav.setOnClickListener(view -> {
            Intent intent1 = new Intent(Index.this,FavoriteSong.class);
            startActivity(intent1);
        });
        taAbout.setOnClickListener(view -> {
            Intent intent1 = new Intent(Index.this,About.class);
            startActivity(intent1);
        });
        taAlbums.setOnClickListener(view -> {
            Intent intent1 = new Intent(Index.this,AllAlbums.class);
            startActivity(intent1);
        });
    }

    private void updateNowPlaying(){
        this.thread = new Thread(){
            @Override
            public void run() {
                while(!interrupted()){
                    String text = "";
                    if (playerBinder.getNowPlaying() == null){
                        text = null;
                    }else {
                        taNowPlaying.setClickable(true);
                        String name = playerBinder.getNowPlaying().getSong_name();
                        String singer = playerBinder.getNowPlaying().getSinger_name();
                        text = name + " - " + singer;
                    }
                    Message message = Message.obtain();
                    message.obj = text;
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
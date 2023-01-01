package com.jsj.txh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class NowPlaying_Activity<now_playing> extends AppCompatActivity {
    private ImageView tvPP;
    private TextView tvAlbum_Name;
    private TextView tvSong_Name;
    private TextView tvTimeCD;
    private TextView tvTime_Full;
    private SeekBar seekBar;
    private com.jsj.txh.FocusTextView tvLyrics;
    private ImageView tvShuffle;
    private ImageView tvPrevious;
    private ImageView tvNext;
    private ImageView TvShow_Lyrics;
    PlayerService.PlayerBinder playerBinder;
    private Thread thread;
    private PlayerConnection playerConnection;
    private String lrc_path;
    private ImageView ivAlbum_cover;
    //private List<Song> play_list;
    private Song now_playing;
    private ImageView add2fav;
    DatabaseOperator operator = new DatabaseOperator();

    List<Song> playList = operator.getAllSong();

    int player_index = 0;
    boolean shuffleOn = false;

    private final Handler handler = new Handler(msg -> {
        if (msg.what == 100) {
            seekBar.setProgress(playerBinder.playerGetCurrentPosition());
            int rem = playerBinder.playerGetMusicLength() - playerBinder.playerGetCurrentPosition();
            //Log.i("T-","T-" + String.valueOf(rem));
            if(rem <= 0){
                playNext();
                //Log.i("T-","T-" + rem + " ms  Play Next");
            }
            String cd = '-' + formatTimeStamp(rem);
            String full = formatTimeStamp(playerBinder.playerGetMusicLength());

            //TODO Test getCurrentLyrics Method, Complete usage after testing!

            //String lrc = getCurrentLyrics(getLyrics(this.lrc_path),playerBinder.playerGetCurrentPosition());
            String lrc = now_playing.getCurrentLyrics(playerBinder.playerGetCurrentPosition());
            if (lrc.length() == 0){
                lrc = "- Music -";
            }
            tvLyrics.setText(lrc);

            //Log.i("Player", cd + ':' + full);
            tvTimeCD.setText(cd);
            tvTime_Full.setText(full);
        }
        //super.handleMessage(msg);
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.now_playing);
        this.tvSong_Name = this.findViewById(R.id.tvSongName_NowPlaying);
        this.tvAlbum_Name = this.findViewById(R.id.tvAlbumName_NowPlaying);
        this.tvTimeCD = this.findViewById(R.id.tvTimeCD);
        this.tvTime_Full = this.findViewById(R.id.tvTimeFull);
        this.seekBar = this.findViewById(R.id.seekBar);
        this.tvLyrics = this.findViewById(R.id.tvLyrics);
        this.ivAlbum_cover = this.findViewById(R.id.ivCover);
        this.tvPP = this.findViewById(R.id.tvPlay_Pause);
        final boolean[] isFromUser = {false};

        this.playerConnection = new PlayerConnection();
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent,playerConnection,BIND_AUTO_CREATE);

        this.tvNext = this.findViewById(R.id.tvNext);
        this.tvPrevious = this.findViewById(R.id.tvPrevious);
        this.add2fav = this.findViewById(R.id.imAdd2Fav);
        this.tvShuffle = this.findViewById(R.id.tvShuffle);
        this.tvLyrics.setFocusable(true);

        this.now_playing = playList.get(player_index);

        tvPP.setOnClickListener(view -> {
            if (playerBinder.playerIsPlayerNull()) {
                //第一次读取歌曲文件信息
                playSong(now_playing);

            }else {
                if (playerBinder.playerIsPlaying()){
                    //pause stream
                    playerBinder.playerPause();
                    tvPP.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.pp));
                    Toast.makeText(getApplicationContext(),"Paused",Toast.LENGTH_SHORT).show();
                }
                else {
                    playerBinder.playerResume();
                    tvPP.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.pause));
                    Toast.makeText(getApplicationContext(),"Play",Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvNext.setOnClickListener(view -> {
            playNext();
        });

        tvPrevious.setOnClickListener(view -> {
            playPrevious();
        });

        tvShuffle.setOnClickListener(view -> {

            if (shuffleOn){
                tvShuffle.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.shuffle));
                Toast.makeText(getApplicationContext(),"Shuffle OFF",Toast.LENGTH_SHORT).show();
            }else {
                tvShuffle.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.shuffle_on));
                Toast.makeText(getApplicationContext(),"Shuffle ON",Toast.LENGTH_SHORT).show();
            }
            shuffleOn = !shuffleOn;
        });

        add2fav.setOnClickListener(view -> {
            if (now_playing.isFavorite()){
                //TODO Complete DatabaseOperator.removeFromFavorite Method
                operator.removeFromFavorite(now_playing);
                add2fav.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.add2fav));
                Toast.makeText(getApplicationContext(),"Remove from Favorite",Toast.LENGTH_SHORT).show();
                now_playing.setFavorite(false);
            }else {
                //TODO Complete DatabaseOperator.addToFavorite Method
                operator.addToFavorite(now_playing);
                add2fav.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.fav));
                Toast.makeText(getApplicationContext(),"Add to Favorite",Toast.LENGTH_SHORT).show();
                now_playing.setFavorite(true);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override

            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (isFromUser[0]){
                    playerBinder.playerSeekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isFromUser[0] = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isFromUser[0] = false;
            }
        });

    }///onCreate()



    private void initSeekbar(){
        int musicWidth = playerBinder.playerGetMusicLength();
        this.seekBar.setMax(musicWidth);
    }

    private void updateProgress(){
        this.thread = new Thread(){
            @Override
            public void run() {
                while(!interrupted()){
                    int currentPosition = playerBinder.playerGetCurrentPosition();
                    Message message = Message.obtain();
                    message.obj = currentPosition;
                    message.what = 100;
                    handler.sendMessage(message);
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                super.run();
            }
        };
        this.thread.start();
    }

    @NonNull
    private String formatTimeStamp(int s){
        String min;
        String sec;
        int sec_int = s/1000;
        int min_int = sec_int / 60;
        min = String.valueOf(min_int);
        sec = String.format("%02d",sec_int - min_int * 60);
        String res;
        res = min + ':' + sec;
        return res;
    }

    private class PlayerConnection implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            playerBinder = (PlayerService.PlayerBinder) iBinder;

            if (playerBinder.playerIsPlayerNull()){
                playSong(now_playing);
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    }

    private void playNext(){
        if (++player_index >= playList.size()){
            player_index = 0;
        }
        if (shuffleOn){
            Random random = new Random();
            player_index = random.nextInt(playList.size());
        }
        now_playing = playList.get(player_index);
        playSong(now_playing);
        Toast.makeText(getApplicationContext(),"Play Next",Toast.LENGTH_SHORT).show();
    }

    private void playPrevious(){
        if(playerBinder.playerGetCurrentPosition() >= 5000){
            playerBinder.playerSeekTo(0);
            Toast.makeText(getApplicationContext(),"Back to Start",Toast.LENGTH_SHORT).show();
        }else {
            if(--player_index < 0){
                player_index = playList.size()-1;
            }
            now_playing = playList.get(player_index);
            playSong(now_playing);
            Toast.makeText(getApplicationContext(),"Play Previous",Toast.LENGTH_SHORT).show();
        }
    }

    private void playSong(Song song){
        if(song.isFavorite()){
            add2fav.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.fav));
        }else {
            add2fav.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.add2fav));
        }
        if (playerBinder.playerIsPlayerNull()) {
            ivAlbum_cover.setImageBitmap(now_playing.getAlbum_cover());
            tvAlbum_Name.setText(now_playing.getAlbum_name()  + " - " + now_playing.getSinger_name());
            tvSong_Name.setText(now_playing.getSong_name());
            playerBinder.playerPlay(now_playing);
            initSeekbar();
            updateProgress();
        }
        else {
            playerBinder.playerStop();
            ivAlbum_cover.setImageBitmap(now_playing.getAlbum_cover());
            tvAlbum_Name.setText(now_playing.getAlbum_name()  + " - " + now_playing.getSinger_name());
            tvSong_Name.setText(now_playing.getSong_name());
            playerBinder.playerPlay(now_playing);
            initSeekbar();
            updateProgress();
        }
    }

    @Override
    protected void onDestroy() {
        if(this.thread != null){
            if(!this.thread.isInterrupted()){
                this.thread.interrupt();
            }
        }
        unbindService(playerConnection);
        super.onDestroy();
    }
}
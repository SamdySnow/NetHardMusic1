package com.jsj.txh;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class PlayerService extends Service {
    private static final String TAG = "PlayingService";
    private Song now_playing;
    MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //TODO Overwrite onBind Method;
        return new PlayerBinder();
        //return null;
    }

    public int getMusicLength(){
        if(this.mediaPlayer != null){
            return this.mediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentPosition(){
        try{
            if (mediaPlayer != null) {
                return this.mediaPlayer.getCurrentPosition();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public void play(Song song){
        String path = song.getFile_path();
        this.now_playing = song;
        try{
            if (mediaPlayer == null) {
                Log.i(TAG,"Start Play");
                this.mediaPlayer = new MediaPlayer();
                this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                this.mediaPlayer.setDataSource(path);
                this.mediaPlayer.prepare();
                this.mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            }else {
                this.mediaPlayer.seekTo(getCurrentPosition());
                this.mediaPlayer.prepare();
                this.mediaPlayer.start();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pause(){
        if(this.mediaPlayer != null){
            if(this.mediaPlayer.isPlaying()){
                Log.i(TAG,"Playback Paused");
                this.mediaPlayer.pause();
            }else if(!this.mediaPlayer.isPlaying()){
                try{
//                    int position = getCurrentProgress();
//                    this.mediaPlayer.seekTo(position);
//                    this.mediaPlayer.prepare();
                    this.mediaPlayer.start();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isPlaying(){
        return this.mediaPlayer.isPlaying();
    }

    public void stop(){
        if(this.mediaPlayer != null){
            Log.i(TAG,"Play Stopped");
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }else {
            Toast.makeText(getApplicationContext(),"Player Stopped!", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isPlayerNull(){
        return this.mediaPlayer == null;
    }

    public void resume(){
        this.mediaPlayer.start();
    }

    public void seekTo(int s){
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(s);
        }
    }



    @Override
    public void onDestroy() {
        if (this.mediaPlayer != null){
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        super.onDestroy();
    }

     class PlayerBinder extends Binder{
        public void playerPlay(Song song){
            play(song);
        }
        public int playerGetCurrentPosition(){
            return getCurrentPosition();
        }
        public void playerPause(){
            pause();
        }
        public int playerGetMusicLength(){
            return getMusicLength();
        }
        public boolean playerIsPlaying(){
            return isPlaying();
        }
        public boolean playerIsPlayerNull(){
            return isPlayerNull();
        }
        public void playerResume(){
            resume();
        }
        public void playerSeekTo(int s){
            seekTo(s);
        }
        public void playerStop(){
            stop();
        }
        public String getHello(){
            return "Hello Service!";
        }
        public Song getNowPlaying(){
            return now_playing;
         }
    }
}

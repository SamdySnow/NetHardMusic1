package com.jsj.txh;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerService extends Service {
    private static final String TAG = "PlayingService";
    private Song now_playing;
    private List<Song> playlist = new ArrayList<>();
    private int player_index = 0;
    private List<Integer> id_list = new ArrayList<>();
    private DatabaseOperator operator = new DatabaseOperator();
    private boolean shuffleOn;

    public void setShuffleOn(boolean shuffleOn) {
        this.shuffleOn = shuffleOn;
        Log.i("Playback CTRL","Shuffle ON Set: " + this.shuffleOn);
    }

    private Thread thread;

    private final Handler handler = new Handler(msg ->{
        if(msg.what == 101){
            playNext();
            Log.i("Playback CTRL","Handle msg 101, Play NEXT");
        }
        return false;
    });

    MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //TODO Overwrite onBind Method;
        return new PlayerBinder();
        //return null;
    }

    public int getMusicLength(){
        if(this.mediaPlayer != null && mediaPlayer.isPlaying()){
            //Log.i("PlayerServices","Call getDuration :" + mediaPlayer.getDuration());
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
        if (this.thread != null){
            if(!this.thread.isInterrupted()){
                thread.interrupt();
                Log.i("THREAD","AutoNext Thread destroyed!");
            }
        }
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
        autoNext();
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

    public void playNext(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Log.i("Playback CTRL","Shuffle Switch: " + shuffleOn);
        if (shuffleOn){
            do{
                int nextInt = new Random().nextInt(playlist.size());
                Log.i("RANDOM","Get Random " + nextInt + " / " + playlist.size());
                if (nextInt != player_index){
                    player_index = nextInt;
                    break;
                }
            }while (true);

            Log.i("Playback CTRL","Shuffle ON INDEX: " + player_index);
        }else {
            if (++player_index >= playlist.size()){
                player_index = 0;
            }
        }
        Log.i("Playback CTRL","PlayNext at INDEX:  " + player_index);
        play(playlist.get(player_index));
    }
    public void playPrevious(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (--player_index < 0){
            player_index = playlist.size() - 1;
        }
        play(playlist.get(player_index));
    }

    public void initService(List<Integer> playlist_id, int player_index){

        this.player_index = player_index;
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        for (int i : playlist_id){
            this.playlist.add(operator.getSongByID(i));
        }
        play(playlist.get(player_index));

        //autoNext();
    }

    public boolean isShuffleOn() {
        return shuffleOn;
    }

    private void autoNext(){
        this.thread = new Thread(){
            @Override
            public void run() {
                int duration = 0xFFFFFF;
                Log.i("THREAD","auto next thread created");
                while (!interrupted()){
                    if (mediaPlayer != null){
                        int rem = 0xFFFFFF;
                        try{
                            rem = getMusicLength() - getCurrentPosition();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Message message = Message.obtain();
                        //message.obj = rem;
                        if (rem <= 250 && getMusicLength() > 0){
                            Log.i("Playback CTRL","-T < 0" + " SEND MESSAGE 101" + "Current Len: " + getMusicLength());
                            message.what = 101;
                            handler.sendMessage(message);
                        }
                        try{
                            sleep(250);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
                super.run();
            }
        };
        this.thread.start();
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
        public void playerInitService(List<Integer> id_list, int index){
            initService(id_list,index);
        }
        public void playerSetShuffle(boolean shuffle){
            setShuffleOn(shuffle);
        }
        public boolean playerIsShuffleOn(){
            return isShuffleOn();
        }
        public void playerPlayNext(){
            playNext();
        }
        public void playerPlayPrevious(){
            playPrevious();
        }
    }
}

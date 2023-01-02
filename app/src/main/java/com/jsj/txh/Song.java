package com.jsj.txh;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Song {
    private int id;
    private String song_name;
    private String file_path;
    private String album_name;
    private String singer_name;
    private String album_cover_path;
    //private Bitmap album_cover;
    private String lyrics_path;
    //private String lyrics;
    private List<String> lyrics_list;
    private boolean favorite;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Song(String song_name, String file_path, String album_name, String singer_name, String album_cover_path, String lyrics_path,boolean favorite) {
        this.id = -1;
        this.song_name = song_name;
        this.file_path = file_path;
        this.album_name = album_name;
        this.singer_name = singer_name;
        this.album_cover_path = album_cover_path;
        this.lyrics_path = lyrics_path;
        this.favorite = favorite;
        List<String> list = new ArrayList<>();
        //Get Full Lyrics at Construct;
        list.add("[00:00.000]Start");
        try{

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(lyrics_path))));
            String line = null;
            int count = 0;
            //读取歌词文件
            while ((line = reader.readLine()) != null){
                list.add(line);
                count++;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        list.add("[99:99.999]End");
        this.lyrics_list = list;
    }

    public void setLyrics_path(String lyrics_path) {
        this.lyrics_path = lyrics_path;
    }

    public String getLyrics_path() {
        return lyrics_path;
    }

    public String getCurrentLyrics(int timestamp){
        //TODO Complete Method
        String res = null;
        //TODO Complete getCurrentLyrics() Method;
        for(int i = 1 ; i < this.lyrics_list.size(); i++){
            //TODO!!! Logic Fault, Debug it!
            int lr_timestamp = timeStampOf(this.lyrics_list.get(i));
            if (lr_timestamp >= timestamp){
                int pos_start = this.lyrics_list.get(i-1).indexOf(']');

                //TODO Log Statement for debugging only, delete it after debugging
                //Log.i("Lyrics",String.valueOf(timestamp) + " / " + String.valueOf(lr_timestamp) + this.lyrics_list.get(i-1).substring(pos_start + 1));

                return this.lyrics_list.get(i-1).substring(pos_start + 1);
            }
        }
        return null;
    }

    private int timeStampOf(@NonNull String s){
        int left = s.indexOf('[');
        int right = s.indexOf(']');
        String timecode = s.substring(left + 1,right);
        int min_right = timecode.indexOf(':');
        String min_code = timecode.substring(0,min_right);
        String sec_code = timecode.substring(min_right + 1);
        int min = Integer.parseInt(min_code);
        int res = 0;
        res += min * 1000 * 60;

        int pos_dot = sec_code.indexOf('.');
        int sec = Integer.parseInt(sec_code.substring(0,pos_dot));
        int mills = Integer.parseInt(sec_code.substring(pos_dot+1));
        res += sec * 1000 + mills;

        return res;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public String getSinger_name() {
        return singer_name;
    }

    public void setSinger_name(String singer_name) {
        this.singer_name = singer_name;
    }

    public String getAlbum_cover_path() {
        return album_cover_path;
    }

    public void setAlbum_cover_path(String album_cover_path) {
        this.album_cover_path = album_cover_path;
    }

    public Bitmap getAlbum_cover() {
        return BitmapFactory.decodeFile(album_cover_path);
    }
}

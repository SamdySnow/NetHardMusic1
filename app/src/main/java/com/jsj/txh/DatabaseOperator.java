package com.jsj.txh;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DatabaseOperator {

    private final String databasePath = "data/data/com.jsj.txh/databases/music_db.db";

    private final SQLiteDatabase database =SQLiteDatabase.openOrCreateDatabase(databasePath,null);

    private final String TAG = "DatabaseOperation";

    private final String TABLE_NAME = "MusicInfo";

    public void createDB(){
        String sql = "CREATE TABLE MusicInfo" +
                "(id Integer primary key autoincrement," +
                "song_name text," +
                "file_path text," +
                "album_name text," +
                "singer_name text," +
                "album_cover_path text," +
                "lyrics_path text," +
                "favorite Integer)";
        database.execSQL(sql);
    }

    //TODO Complete Method
    public int addSong(@NonNull Song song){
        String song_name = song.getSong_name();
        String file_path = song.getFile_path();
        String album_name = song.getAlbum_name();
        String singer_name = song.getSinger_name();
        String album_cover_path = song.getAlbum_cover_path();
        String lyrics_path = song.getLyrics_path();
        int favorite = (song.isFavorite())? 1 : 0;

        Cursor cursor = database.rawQuery("SELECT * FROM MusicInfo WHERE file_path = ?",new String[]{file_path});
        if (cursor.getCount() != 0){
            cursor.close();
            return 1;
        }
        String sql = "INSERT INTO MusicInfo VALUES(?,?,?,?,?,?,?,?)";

        //TODO Add Try/Catch Statement
        database.execSQL(sql,new Object[]{null,song_name,file_path,album_name,singer_name,album_cover_path,lyrics_path,favorite});
        Log.i(TAG,"Insert Success!");
        return 0;
    }
    public void addToFavorite(Song song){
        String file_path = song.getFile_path();
        String sql = "UPDATE MusicInfo SET favorite = 1 WHERE file_path = ?";
        database.execSQL(sql,new String[]{file_path});
    }
    public void removeFromFavorite(Song song){
        String file_path = song.getFile_path();
        String sql = "UPDATE MusicInfo SET favorite = 0 WHERE file_path = ?";
        database.execSQL(sql,new String[]{file_path});
    }
    public int deleteFromDatabase(Song song){
        return 0;
    }
    public List<Song> getAllSong(){
        List<Song> res = new ArrayList<>();
        String sql = "SELECT * FROM MusicInfo";
        Cursor cursor = database.rawQuery(sql,null);

        if(cursor.getCount() != 0){
            cursor.moveToFirst();

            do {
                int id = cursor.getInt(0);
                String song_name = cursor.getString(1);
                String file_path = cursor.getString(2);
                String album_name = cursor.getString(3);
                String singer_name = cursor.getString(4);
                String album_cover_path = cursor.getString(5);
                String lyrics_path = cursor.getString(6);
                int isFavorite = cursor.getInt(7);

                Song s = new Song(song_name, file_path, album_name, singer_name, album_cover_path, lyrics_path, isFavorite == 1);
                s.setId(id);
                res.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }
    public Song getSongByID(int id){
        String sql = "SELECT * FROM MusicInfo WHERE id = ?";
        Cursor cursor = database.rawQuery(sql,new String[]{String.valueOf(id)});
        //int id = cursor.getInt(0);
        cursor.moveToFirst();
        String song_name = cursor.getString(1);
        String file_path = cursor.getString(2);
        String album_name = cursor.getString(3);
        String singer_name = cursor.getString(4);
        String album_cover_path = cursor.getString(5);
        String lyrics_path = cursor.getString(6);
        int isFavorite = cursor.getInt(7);

        Song s = new Song(song_name, file_path, album_name, singer_name, album_cover_path, lyrics_path, isFavorite == 1);
        s.setId(id);
        cursor.close();
        return s;
    }
}

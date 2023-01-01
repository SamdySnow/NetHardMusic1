package com.jsj.txh;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Add_Song extends AppCompatActivity {

    private DatabaseOperator operator = new DatabaseOperator();

    private EditText etSongName;
    private EditText etAlbumName;
    private EditText etImagePath;
    private EditText etSingerName;
    private EditText etSongFilePath;
    private EditText etLyricsFilePath;

    private Button btnADD;
    private Button btnINIT;

    String path = "/data/data/com.jsj.txh/MusicFiles/M04.mp3";
    String lrc_path = "/data/data/com.jsj.txh/Lyrics/L04.txt";
    String song_name = "星球上的追溯诗";
    String album_name = "Girls POP Vol.2";
    String album_cover_path = "/data/data/com.jsj.txh/AlbumCover/C04.jpg";
    String singer_name = "熊子/味素";

    //TODO This Class ONLY FOR TESTING!
    Song TEST_CLASS_SONG = new Song(song_name,path,album_name,singer_name,album_cover_path,lrc_path,true);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);
        this.btnINIT = this.findViewById(R.id.btn_init_database);
        this.btnADD = this.findViewById(R.id.btn_add_to_database);

        btnINIT.setOnClickListener(view -> {
            operator.createDB();
            Toast.makeText(getApplicationContext(),"Database CREATED!",Toast.LENGTH_LONG).show();
        });

        btnADD.setOnClickListener(view -> {
            if (operator.addSong(TEST_CLASS_SONG) == 1) Toast.makeText(getApplicationContext(),"Already Exists!",Toast.LENGTH_LONG).show();
            else Toast.makeText(getApplicationContext(),"Song ( " + song_name + " ) Successfully Added to Database!",Toast.LENGTH_LONG).show();
        });
    }

    protected int addToDatabase(Song song){
        return 0;
    }
}
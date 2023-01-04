package com.jsj.txh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllSongs extends AppCompatActivity {
    DatabaseOperator operator = new DatabaseOperator();
    private PlayerConnection playerConnection;
    PlayerService.PlayerBinder playerBinder;

    private LinearLayout taBack;

    private class PlayerConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            playerBinder = (PlayerService.PlayerBinder) iBinder;

        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_songs);

        this.playerConnection = new PlayerConnection();
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent,playerConnection,BIND_AUTO_CREATE);

        this.taBack = this.findViewById(R.id.ta_all_songs_back);
        taBack.setOnClickListener(view -> {
            finish();
        });

        List<Song> playlist = new ArrayList<>();

        //TODO Switch intent and reuse this class
        Intent intent1 = getIntent();
        Bundle bundle = intent1.getExtras();
        int tag = bundle.getInt("TAG");

        switch (tag)
        {
            case 0:
                playlist = operator.getAllSong();
                break;
            case 1:
                String album_name = bundle.getString("CONTENT");
                playlist = operator.getAllSongByAlbum(album_name);
                break;
            case 2:
                String singer_name = bundle.getString("CONTENT");
                playlist = operator.getAllMusicBySinger(singer_name);
        }

        List<Integer> playlist_id = new ArrayList<>();

        List<Map<String,Object>> list = new ArrayList<>();

        for (Song s : playlist){
            Map<String, Object> item = new HashMap<>();
            item.put("cover",s.getAlbum_cover());
            item.put("name",s.getSong_name());
            item.put("about",s.getAlbum_name() + " - " + s.getSinger_name());
            list.add(item);
            playlist_id.add(s.getId());
        }

        SimpleAdapter adapter = new SimpleAdapter(this,
                list,R.layout.all_song_text_elm,
                new String[]{"cover","name","about"},
                new int[]{R.id.all_songs_album,R.id.all_songs_name,R.id.all_songs_about});


        adapter.setViewBinder((view, data, textRepresentation) -> {
            if (view instanceof ImageView && data instanceof Bitmap) {
                ImageView image = (ImageView) view;
                image.setImageBitmap((Bitmap) data);
                return true;
            }
            return false;
        });

        ListView listView = this.findViewById(R.id.all_songs_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("ListView Click","Clicked");
                playerBinder.playerInitService(playlist_id,i);
                Intent intent1 = new Intent(AllSongs.this,NowPlaying_Activity.class);
                startActivity(intent1);
            }
        });
    }
}
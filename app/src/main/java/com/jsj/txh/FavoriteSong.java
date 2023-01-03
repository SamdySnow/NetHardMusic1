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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteSong extends AppCompatActivity {
    DatabaseOperator operator = new DatabaseOperator();
    private PlayerConnection playerConnection;
    PlayerService.PlayerBinder playerBinder;

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
        setContentView(R.layout.activity_favorite_song);


        this.playerConnection = new PlayerConnection();
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent,playerConnection,BIND_AUTO_CREATE);

        List<Song> playlist = operator.getAllFavoriteSong();
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

        ListView listView = this.findViewById(R.id.all_fav_songs_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("ListView Click","Clicked");
                playerBinder.playerInitService(playlist_id,i);
                Intent intent1 = new Intent(FavoriteSong.this,NowPlaying_Activity.class);
                startActivity(intent1);
            }
        });
    }
}

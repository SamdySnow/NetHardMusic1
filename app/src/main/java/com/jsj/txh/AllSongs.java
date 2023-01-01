package com.jsj.txh;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllSongs extends AppCompatActivity {
    DatabaseOperator operator = new DatabaseOperator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_songs);

        List<Song> playlist = operator.getAllSong();

        List<Map<String,Object>> list = new ArrayList<>();

        for (Song s : playlist){
            Map<String, Object> item = new HashMap<>();
            item.put("cover",s.getAlbum_cover());
            item.put("name",s.getSong_name());
            item.put("about",s.getAlbum_name() + " - " + s.getSinger_name());
            list.add(item);
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
    }
}
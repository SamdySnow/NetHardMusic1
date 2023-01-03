package com.jsj.txh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AllAlbums extends AppCompatActivity {

    DatabaseOperator operator = new DatabaseOperator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_albums);

        LinearLayout taBack = this.findViewById(R.id.ta_all_albums_back);
        taBack.setOnClickListener(view -> {
            finish();
        });

        List<Map<String,String>> album_list = operator.getALlAlbums();
        ListView listView = this.findViewById(R.id.all_albums_list);

        List<Map<String, Object>> display_list = new ArrayList<>();

        for(Map<String,String> i : album_list){
            String album_name = i.get("album_name");
            Bitmap cover = BitmapFactory.decodeFile(i.get("album_cover_path"));
            Map<String,Object> item = new HashMap<>();
            item.put("album_name",album_name);
            item.put("album_cover",cover);
            display_list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this,
                display_list,R.layout.group_by_albums_elm,
                new String[]{"album_name","album_cover"},
                new int[]{R.id.all_album_name,R.id.all_album_cover});

        adapter.setViewBinder((view, data, textRepresentation) -> {
            if (view instanceof ImageView && data instanceof Bitmap) {
                ImageView image = (ImageView) view;
                image.setImageBitmap((Bitmap) data);
                return true;
            }
            return false;
        });

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putInt("TAG",1);
                bundle.putString("CONTENT", Objects.requireNonNull(display_list.get(i).get("album_name")).toString());
                Intent intent = new Intent(AllAlbums.this,AllSongs.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
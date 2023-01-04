package com.jsj.txh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllSinger extends AppCompatActivity {

    DatabaseOperator operator = new DatabaseOperator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_singer);

        LinearLayout taBack = this.findViewById(R.id.ta_all_singers_back);

        taBack.setOnClickListener(view -> {
            finish();
        });

        List<String> singer_name_list = operator.getAllSingers();
        List<Map<String,String>> display_list = new ArrayList<>();

        for (String i : singer_name_list){
            Map<String , String> item = new HashMap<>();
            item.put("name",i);
            display_list.add(item);

        }
        SimpleAdapter adapter = new SimpleAdapter(this,
                display_list,R.layout.group_by_singers_elm,
                new String[]{"name"}, new int[]{R.id.all_singer_name});

        ListView listView = this.findViewById(R.id.all_singers_songs_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putInt("TAG",2);
                bundle.putString("CONTENT",singer_name_list.get(i));
                Intent intent = new Intent(AllSinger.this,AllSongs.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }
}
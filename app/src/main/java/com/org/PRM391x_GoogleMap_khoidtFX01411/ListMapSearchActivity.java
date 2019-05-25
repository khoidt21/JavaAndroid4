package com.org.PRM391x_GoogleMap_khoidtFX01411;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import java.util.ArrayList;

import adapter.MapAdapter;
import dbhelper.MapDbhelper;
import model.ModelMap;

public class ListMapSearchActivity extends AppCompatActivity {

    ListView listView;
    public ArrayList<ModelMap> listMap = new ArrayList<>();
    MapAdapter mapAdapter;
    RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;

    MapDbhelper mapDbhelperDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_map);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        mapDbhelperDB = new MapDbhelper(getApplicationContext());;
        listMap = mapDbhelperDB.getAlarms();
        setAlarmToAdapter();
    }
    public void setAlarmToAdapter(){

        mapAdapter = new MapAdapter(this, listMap);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mapAdapter);
    }

}

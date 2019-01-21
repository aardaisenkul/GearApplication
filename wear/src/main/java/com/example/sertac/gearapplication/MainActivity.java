package com.example.sertac.gearapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends WearableActivity{

    private TextView mTextView;
    private WearableListView wearableListView;
    private String[] habitNames;
    private int habitCounts[];
    private int lvScrollPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub =(WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                mTextView = stub.findViewById(R.id.text);
                String message = getIntent().getStringExtra("message");
                if(message==null || message.equalsIgnoreCase("") || message.equalsIgnoreCase("Bilgiler Telefondan Çekilemedi...")){
                    message = "Bilgiler Telefondan Çekilemedi...";
                    mTextView.setText(message);
                    mTextView.setVisibility(View.VISIBLE);
                }
                else{
                    habitNames = message.split(",");
                    habitCounts = new int[habitNames.length];
                    wearableListView = findViewById(R.id.listView);

                    wearableListView.setAdapter(adapt());
                    WearableListView.ClickListener clickListener = new WearableListView.ClickListener() {
                        @Override
                        public void onClick(WearableListView.ViewHolder view) {
                            int position = view.getLayoutPosition();
                            habitCounts[position]++;
                            if (habitNames[position].indexOf(":")>0)
                                habitNames[position]=habitNames[position].substring(0,habitNames[position].indexOf(":"))+":\t\t("+habitCounts[position]+")";
                            else
                                habitNames[position]+=":\t\t("+habitCounts[position]+")";
                            wearableListView.setAdapter(adapt());
                        }

                        @Override
                        public void onTopEmptyRegionClick() {

                        }
                    };
                    wearableListView.setClickListener(clickListener);

                }

            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

    private WearableListView.Adapter adapt(){
        WearableListView.Adapter adapter = new WearableListView.Adapter() {
            private LayoutInflater inflater=LayoutInflater.from(MainActivity.this);

            @Override
            public WearableListView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new WearableListView.ViewHolder(inflater.inflate(R.layout.row_simple_item,null));
            }

            @Override
            public void onBindViewHolder(@NonNull WearableListView.ViewHolder holder, int position) {
                TextView view = holder.itemView.findViewById(R.id.textView);
                view.setText(habitNames[position]);
                holder.itemView.setTag(position);
            }

            @Override
            public int getItemCount() {
                return habitNames.length;
            }
        };
        return adapter;
    }


}

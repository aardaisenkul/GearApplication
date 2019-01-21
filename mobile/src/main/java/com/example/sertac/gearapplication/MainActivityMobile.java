package com.example.sertac.gearapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class MainActivityMobile extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    Database db;
    GoogleApiClient googleApiClient = null;
    public static final String TAG = "MyDataMap....";
    public static final String WEARABLE_DATA_PATH = "/wearable/data/path";
    private int receivedMessageNumber;

    TextView textView;
    protected Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        db = new Database(getApplicationContext());


        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stuff = msg.getData();
                messageText(stuff.getString("messageText"));
                return true;
            }
        });

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addApi(Wearable.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        googleApiClient = builder.build();
        sendMessage();

    }

    public class Receiver extends BroadcastReceiver {
        @Override

        public void onReceive(Context context, Intent intent) {

//Upon receiving each message from the wearable, display the following text//

            String message = "I just received a message from the wearable " + receivedMessageNumber++;;

            textView.setText(message);

        }
    }

    public void messageText(String newinfo) {
        if (newinfo.compareTo("") != 0) {
            textView.append("\n" + newinfo);
        }
    }

    public void onClickAddButton(View view) {

        EditText habitName = findViewById(R.id.habitName);

        String a = habitName.getText().toString();
        db.addScore(a);
        habitName.setText("");
        sendMessage();
    }


    public void onClickResetTable(View view) {
        db.resetTables();
        Toast.makeText(this, "Table reset successfully", Toast.LENGTH_SHORT).show();
        sendMessage();

    }



    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(googleApiClient!=null && googleApiClient.isConnected())
        {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        sendMessage();
    }

    public void sendMessage(){
        if(googleApiClient.isConnected()){
            ArrayList<habitFeatures> scores = db.scores();
            StringBuilder sb = new StringBuilder();
            for (habitFeatures a : scores){
                sb.append(a.getHabitName()+",");
            }
            String message = sb.toString();

            if(message ==null || message.equals(""))
            {
                message="Hiçbir Alışkanlık Bulunamadı...";
            }
            new SendMessageToDataLayer(WEARABLE_DATA_PATH,message).start();

        }else{

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class SendMessageToDataLayer extends Thread{
        String path;
        String message;
        public SendMessageToDataLayer(String path, String message){
            this.path = path;
            this.message = message;
        }

        @Override
        public void run() {
            NodeApi.GetConnectedNodesResult nodesList = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
            for (Node node: nodesList.getNodes()){
                MessageApi.SendMessageResult messageResult = Wearable.MessageApi.sendMessage(googleApiClient,node.getId(),path,message.getBytes()).await();
                if(messageResult.getStatus().isSuccess()){
                    Log.v(TAG,"Message: successfully sent to"+node.getDisplayName());
                    Log.v(TAG,"Message: Node Id is"+node.getId());
                    Log.v(TAG,"Message: Node size is"+nodesList.getNodes().size());
                }else{
                    Log.v(TAG,"Error while sending Message!!");
                }
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


}

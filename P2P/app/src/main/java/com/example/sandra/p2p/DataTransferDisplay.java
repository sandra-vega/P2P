package com.example.sandra.p2p;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sandra on 08/04/2018.
 */

public class DataTransferDisplay  extends AppCompatActivity {
    TextView p1textView;
    TextView p2textView;

    ClientThread clientThread;
    ServerThread serverThread;

    InetAddress hostAddress;
    String stringHostAddress;

    Timer mTimer;
    TimerTask mTask;

    Intent intent;

    Boolean host;

    int port =8888;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer_display);

        p1textView = (TextView) findViewById(R.id.player1TextView);
        p2textView = (TextView) findViewById(R.id.player2TextView);

        intent = getIntent();

        if(intent.getBooleanExtra("Connected",false)){
            stringHostAddress= intent.getStringExtra("Hostaddress");

            try{
                hostAddress = InetAddress.getByName(stringHostAddress);
             }catch (UnknownHostException Exc){

            }

            host = intent.getBooleanExtra("IsHost",false);

             if(host){
                 serverThread = new ServerThread(port);
                 new Thread(serverThread).start();
             }else {
                 clientThread = new ClientThread(hostAddress, port);
                 new Thread(clientThread).start();
             }
        }
    }

    @Override
    protected void onResume() {

        mTimer = new Timer();
        mTask =new TimerTask() {
            @Override
            public void run() {
                if(host){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            p1textView.setText("Player1: "+serverThread.getPlayer1String());
                            p2textView.setText("Player2: "+serverThread.getPlayer2String());
                        }
                    });
                }else if(!host){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            p1textView.setText("Player1: "+clientThread.getPlayer1String());
                            p2textView.setText("Player2: "+clientThread.getPlayer2String());
                        }
                    });
                }
            }
        };
        mTimer.schedule(mTask, 10, 10);
        super.onResume();
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

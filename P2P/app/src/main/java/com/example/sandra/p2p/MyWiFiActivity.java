package com.example.sandra.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;

public class MyWiFiActivity extends AppCompatActivity {

    public TextView mTextView;
    public ListView mListView;

    private Button btnBuscar, btnPlay;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WiFiDirectBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private ArrayAdapter<String> wifip2pArrayAdapter;
    int posicion;

    Intent dataDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIntentFilter = new IntentFilter();
        // Indica un cambio en el estado de Wi-Fi P2P.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indica un cambio en la lista de pares disponibles.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indica que el estado de la conectividad Wi-Fi P2P ha cambiado.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indica que los detalles de este dispositivo han cambiado.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mTextView =(TextView)findViewById(R.id.textView);
        mListView = (ListView)findViewById(R.id.lsvLista);

        wifip2pArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mListView.setAdapter(wifip2pArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                posicion= pos;
                mReceiver.connect(posicion);

            }
        });

        btnBuscar =(Button)findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                buscar(view);
            }
        });

        btnPlay =(Button)findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                play(view);
            }
        });


        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        dataDisplay = new Intent(MyWiFiActivity.this, DataTransferDisplay.class);
    }

    public void buscar (View view){
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // El código para cuando la iniciación de descubrimiento es exitosa va aquí.
                // Aún no se han descubierto servicios, por lo que este método
                // a menudo se puede dejar en blanco.  El código para el descubrimiento de pares va en el
                // método onReceive, detallado a continuación.
                mTextView.setText("wifi-Direct: Buscando...");
            }

            @Override
            public void onFailure(int reasonCode) {
                // El código para cuando falla la iniciación del descubrimiento va aquí.
                // Alertar al usuario de que algo salió mal.
                mTextView.setText("Error: "+reasonCode);
            }
        });
    }

    public void play(View view){
        Toast.makeText(getApplicationContext(),
                "Esto empezara una vez conectado",
                Toast.LENGTH_LONG).show();
    }
    public void play(InetAddress hostAddress, Boolean host) {
        dataDisplay.putExtra("HostAddress", hostAddress.getHostAddress());
        dataDisplay.putExtra("IsHost", host);
        dataDisplay.putExtra("Connected",true);

        startActivity(dataDisplay);
    }

    public void displayPeers(WifiP2pDeviceList peerList){
        wifip2pArrayAdapter.clear();
        for(WifiP2pDevice peer : peerList.getDeviceList()){
            wifip2pArrayAdapter.add(peer.deviceName +"\n" + peer.deviceAddress);
        }
    }
    public void setmTextView(String text){
        mTextView.setText("Wifi-Direct: "+text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }



}

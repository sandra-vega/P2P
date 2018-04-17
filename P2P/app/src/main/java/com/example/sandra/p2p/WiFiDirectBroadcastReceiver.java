package com.example.sandra.p2p;

/**
 * Un BroadcastReceiver que notifica importantes eventos Wi-Fi p2p.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MyWiFiActivity mActivity;
    private List<WifiP2pDevice> mPeers;
    private List<WifiP2pConfig> mConfigs;

    private  WifiP2pDevice mDivice;


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MyWiFiActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // Verifique si Wi-Fi está habilitado y notifique la actividad apropiada
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
              int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi P2P is enabled
                    mActivity.mTextView.setText("wifi-Direct: Encendido");
                    //infoListener.onConnectionInfoAvailable();
                } else {
                    // Wi-Fi P2P is not enabled
                    mActivity.mTextView.setText("wifi_Derect: Apagado");
                }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Llamar a WifiP2pManager.requestPeers () para obtener una lista de pares actuales
            mPeers = new ArrayList<WifiP2pDevice>();
            mConfigs = new ArrayList<WifiP2pConfig>();
            mDivice= new WifiP2pDevice();

            if (mManager != null) {
                final WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peerList) {
                        mPeers.clear();
                        mPeers.addAll(peerList.getDeviceList());

                        mActivity.displayPeers(peerList);

                        mPeers.addAll(peerList.getDeviceList());

                        for(int i=0; i< peerList.getDeviceList().size(); i++){
                            WifiP2pConfig config = new WifiP2pConfig();
                            config.deviceAddress = mPeers.get(i).deviceAddress;
                            mConfigs.add(config);
                        }
                    }
                };

               mManager.requestPeers(mChannel, peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Responder a una nueva conexión o desconexión
            if (mManager == null) {
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                // Estamos conectados con el otro dispositivo, solicitamos conexión
                //información para encontrar IP del propietario del grupo

                mManager.requestConnectionInfo(mChannel,infoListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Responde al cambio de estado del wifi de este dispositivo
        }
    }


    public void connect(int posicion) {

        WifiP2pConfig config = mConfigs.get(posicion);
        mDivice = mPeers.get(posicion);

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener(){

            @Override
            public void onSuccess() {
                //mActivity.makeToast("Conectado");
                Log.d("MyTag","Connect");
            }

            @Override
            public void onFailure(int reason) {
                //mActivity.makeToa st("Conectado");
                Log.d("MyTag","Connect: Error"+reason);

            }
        });
    }

    WifiP2pManager.ConnectionInfoListener infoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo Info) {
            InetAddress groupOwnerAddress= Info.groupOwnerAddress;

            if (Info.groupFormed){
                if(Info.isGroupOwner){
                    mActivity.setmTextView("Host");
                    mActivity.play(groupOwnerAddress, true);
                }else{
                    mActivity.setmTextView("Client");
                    mActivity.play(groupOwnerAddress, false);
                }
            }
        }
    };
    public void disconnect() {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener(){

            @Override
            public void onSuccess() {
                Log.d("MyTag","Removed Group");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("MyTag","Removed Group Failed: Error "+ reason);
            }
        });
    }
}
package com.example.sandra.p2p;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by sandra on 08/04/2018.
 */

class ClientThread implements Runnable {

    InetAddress mHostAddress;
    int mPort = 0;

    DatagramSocket socket;

    byte[] sendData = new byte [64];
    byte[] receiveData = new byte [64];

    String player1String = "Client";
    String player2String;
    int sendCount =1;
    int reciveCount=0;

    public ClientThread(InetAddress hostAddress, int port) {
        mHostAddress=hostAddress;
        mPort=port;
    }

    @Override
    public void run() {

        if(mHostAddress != null && mPort !=0){
            while (true){
                try{
                    if(socket == null){
                        socket = new DatagramSocket(mPort);
                        socket.setSoTimeout(1);
                    }
                }catch (IOException exc){
                    if(exc.getMessage() == null){
                        Log.e("set Socket","Unknown Message");
                    }else{
                        Log.e("Set Socket", exc.getMessage());
                    }
                }


                try{
                    sendData = (player1String + sendCount).getBytes();
                    sendCount++;

                    DatagramPacket packet = new DatagramPacket(sendData,sendData.length,
                            mHostAddress,mPort);
                    socket.send(packet);
                    Log.e("MyTag","Client: Packet Sent");
                }catch (IOException exc){
                    if(exc.getMessage() == null){
                        Log.e("set Socket","Unknown Message: Likely Timeout ");
                    }else{
                        Log.e("Set Socket", exc.getMessage());
                    }
                }

                try{
                    DatagramPacket receivePacket = new DatagramPacket(receiveData,
                            receiveData.length);
                    socket.receive(receivePacket);

                    receivePacket.getData();

                    player2String = new String(receivePacket.getData(),0,
                            receivePacket.getLength());
                    reciveCount++;

                }catch (IOException exc){
                    if(exc.getMessage() == null){
                        Log.e("set Socket","Unknown Message");
                    }else{
                        Log.e("Set Socket", exc.getMessage());
                    }
                    continue;
                }
            }
        }

    }

    public String getPlayer1String(){return (player1String+ reciveCount);}
    public String getPlayer2String(){return player2String;}
}

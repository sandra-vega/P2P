package com.example.sandra.p2p;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by sandra on 08/04/2018.
 */

class ServerThread implements Runnable {

    DatagramSocket socket;

    int mPort;

    String player1String = "Host";
    String player2String;
    int sendCount =1;
    int reciveCount=0;

    byte[] sendData = new byte [64];
    byte[] receiveData = new byte [64];

    InetAddress mclientAddress;

    boolean gotPacket = false;


    public ServerThread(int initport) {
        mPort = initport;
    }

    @Override
    public void run() {

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

                    DatagramPacket receivePacket = new DatagramPacket(receiveData,
                            receiveData.length);
                    Log.e("MyTag", "Waiting for Packet");

              try{

                  socket.receive(receivePacket);

                  receivePacket.getData();

                  player2String = new String(receivePacket.getData(),0,
                          receivePacket.getLength());
                  Log.e("MyTag", "Recived Packet, contaied: "+player2String);
                  reciveCount++;

                  if(mclientAddress == null){
                      mclientAddress = receivePacket.getAddress();
                  }

              }catch (IOException exc){
                  if(exc.getMessage() == null){
                      Log.e("Receive","NullExceptio: Likely timeout");
                      continue;
                  }else{
                      Log.e("Set Socket", exc.getMessage());
                      continue;
                  }

              }

              try{

                  if(mclientAddress != null){
                      sendData = (player1String + sendCount).getBytes();
                      sendCount++;

                      DatagramPacket packet = new DatagramPacket(sendData,sendData.length,
                              mclientAddress,mPort);
                      socket.send(packet);
                      Log.e("MyTag","Server: Packet Sent"+ player1String);
                  }

              }catch (IOException exc){
                  if(exc.getMessage() == null){
                      Log.e("Sender","Null Exception: Likely Timeout ");
                      continue;
                  }else{
                      Log.e("Sender", exc.getMessage());
                  }
              }
          }
    }

    public String getPlayer1String(){return (player1String+ reciveCount);}
    public String getPlayer2String(){return player2String;}
}

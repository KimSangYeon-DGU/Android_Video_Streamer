package com.cse.streamingclient;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by sy081 on 2018-04-08.
 */

public class UdpSocket {
    private DatagramSocket udpSocket;
    private SocketAddress socketAddress;
    private String address;
    private int port;
    private boolean connection;

    public UdpSocket(final String address, final int port){
        this.address = address;
        this.port = port;
        this.connection = false;
    }
    public boolean connect(){
        try {
            udpSocket = new DatagramSocket();
            socketAddress = new InetSocketAddress(address, port);
            this.connection = true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            return true;
        }
    }

    public boolean isConnected(){
        if(connection)
            return true;
        else
            return false;
    }

    public void sendUdpPacket(final byte[] data){
        Thread udpSendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(udpSocket != null){
                    try {
                        udpSocket.send(new DatagramPacket(data, data.length, socketAddress));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        udpSendThread.start();
    }
    public void close(){
        if(udpSocket != null){
            udpSocket.close();
        }
    }
}

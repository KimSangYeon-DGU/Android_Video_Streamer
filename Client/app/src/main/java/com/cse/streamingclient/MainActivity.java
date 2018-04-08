package com.cse.streamingclient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.FaceDetector;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements VideoExtractor.FrameCallback{
    private UdpSocket udpSocket;
    private int READ_EXTERNAL_STORAGE_PERMISSION = 1;
    private int INTERNET_PERMISSION = 2;
    private VideoExtractor videoExtractor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
    }

    public void init(){
        udpSocket = new UdpSocket(Constants.ADDR, Constants.PORT);
        if(udpSocket.connect()){
            showToast("Connected Udp Socket to server");
            videoExtractor = new VideoExtractor(getBaseContext(),"/Android_Studio/hd_00_00.mp4");
            videoExtractor.setFrameCallback(this);

            videoExtractor.extract();
        }
    }

    private boolean requestPermissions() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION);
            }else if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION);
            }else{
                showToast("Succeeded to get all permission");
                init();
            }
        }else{
            return true;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (READ_EXTERNAL_STORAGE_PERMISSION == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Succeeded to get read_external_storage permission");
            } else {
                showToast("Failed to get read_external_storage permission");
            }
        }else if(INTERNET_PERMISSION == requestCode){
            if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                showToast("Succeeded to get Internet permission");
            } else {
                showToast("Failed to get Internet permission");
            }
        }
    }

    public void showToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void getVideoFrame(final byte[] bytes) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(udpSocket.isConnected()) {
                    udpSocket.sendUdpPacket(bytes);
                    Log.d("DEBUG", ""+bytes);
                }
            }
        });
    }

}

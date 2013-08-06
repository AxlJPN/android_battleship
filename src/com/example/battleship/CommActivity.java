package com.example.battleship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * 共通クラス
 * 
 * @author T.Sasaki
 * 
 */
public class CommActivity extends Activity {
    Context context = null;
    int port = 8080;
    String serverIpAddress = null;
    String clientIpAddress = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }

    public class connectWait extends Wait {
        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                // Intent intent = new Intent(context, nextActivity.class);
                // startActivity(intent);
            }

        }
    }

    // ��M���̑҂���Ԃ�����
    public class Wait extends AsyncTask<String, Integer, String> {
        private ServerSocket mServer;
        private Socket mSocket;
        Handler mHandler = new Handler();

        @Override
        protected String doInBackground(String... args0) {
            StringBuilder messageBuilder = new StringBuilder();
            String message = null;

            // Toast.makeText(context, "host-ip:" + getWifiInfo(),
            // Toast.LENGTH_SHORT).show();
            serverIpAddress = getWifiInfo();
            Log.v("Socket", "host-ip:" + serverIpAddress);

            try {
                mServer = new ServerSocket(port);
                mServer.setSoTimeout(30000);
                mSocket = mServer.accept();

                clientIpAddress = mSocket.getInetAddress().toString();
                Log.v("Socket", "client-ip:" + clientIpAddress);

                // TODO ��M
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        mSocket.getInputStream()));

                while ((message = in.readLine()) != null) {
                    messageBuilder.append(message);
                }
            } catch (SocketTimeoutException e) {
                Log.v("Socket", "Socket Timeout");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (mSocket != null)
                    mSocket.close();
                if (mServer != null)
                    mServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Toast.makeText(context, "�X���b�h�X�^�[�g",
            // Toast.LENGTH_SHORT).show();

            return messageBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                // Intent intent = new Intent(context, nextActivity.class);
                // startActivity(intent);
            }

        }

    }

    public class Send extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... args0) {
            Socket socket = null;

            try {
                socket = new Socket(args0[0], port);
                clientIpAddress = getWifiInfo();

                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                pw.write("1");
                pw.flush();

            } catch (UnknownHostException e) {
                Log.v("Socket", "unknownHost");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (socket != null) {
                try {
                    socket.close();
                    socket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return true;
        }
    }

    public String getWifiInfo() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifIinfo = wifiManager.getConnectionInfo();
        int address = wifIinfo.getIpAddress();
        String ipAddressStr = ((address >> 0) & 0xFF) + "." + ((address >> 8) & 0xFF) + "."
                + ((address >> 16) & 0xFF) + "." + ((address >> 24) & 0xFF);

        return ipAddressStr;
    }
    
    public void Xxx(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setIcon(R.drawable.ic_launcher);
//        builder.setTitle("Connection destination");
//
//        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
//        WifiInfo wifIinfo = wifiManager.getConnectionInfo();
//        int address = wifIinfo.getIpAddress();
//        String ipAddressStr = ((address >> 0) & 0xFF) + "." + ((address >> 8) & 0xFF) + "."
//                + ((address >> 16) & 0xFF) + "." + ((address >> 24) & 0xFF);
//
//        builder.setMessage("Please specify the connection destination.\nYour IP is: " + ipAddressStr);
//        final EditText editView = new EditText(this);
//        builder.setView(editView);
//
//        builder.setPositiveButton("connect", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MainActivity.this, "CANCEL", Toast.LENGTH_LONG).show();
//            }
//
//        });
//
//        builder.show();
    }
}

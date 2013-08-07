package com.example.battleship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class CommModule {
    /**
     * ポートの指定
     */
    int port = 8080;
    Context context = null;
    String serverIpAddress = null;
    String clientIpAddress = null;

    public CommModule(Context con){
        context = con;
    }

    /**
     * ソケット通信の受信を行う
     * @author T.Sasaki
     *
     */
    public class Recieve extends AsyncTask<String, Integer, String> {
        private ServerSocket mServer;
        private Socket mSocket;

        public Recieve(Context con){
            context = con;
        }

        public Recieve(Context con, int p){
            context = con;
            port = p;
        }

        @Override
        protected String doInBackground(String... args0) {
            StringBuilder messageBuilder = new StringBuilder();
            String message = null;

            serverIpAddress = getWifiInfo();
            Log.v("Socket", "server-ip:" + serverIpAddress);

            try {
                // connect
                mServer = new ServerSocket(port);
                // タイムアウト時間を設定
                mServer.setSoTimeout(30000);
                mSocket = mServer.accept();

                clientIpAddress = mSocket.getInetAddress().toString();
                Log.v("Socket", "client-ip:" + clientIpAddress);

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        mSocket.getInputStream()));

                while ((message = in.readLine()) != null) {
                    messageBuilder.append(message);
                }
            } catch (SocketTimeoutException e) {
                Log.v("Socket", "Socket Timeout");
                Toast.makeText(context, "接続がタイムアウトしました", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Socket", "Error");
                e.printStackTrace();
            }

            // disconnect
            try {
                if (mSocket != null){
                    mSocket.close();
                    mSocket = null;
                }
                if (mServer != null){
                    mServer.close();
                    mServer = null;
                }
            } catch (IOException e) {
                Log.e("Socket", "Close Error");
                e.printStackTrace();
            }

            return messageBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // 値を判別して処理を加える
        }
    }

    /**
     * ソケット通信の送信を行う
     * @author T.Sasaki
     */
    public class Send extends AsyncTask<String, Integer, Boolean> {
        /**
         * コンストラクタ
         * @param con 使用するActivityのContext
         * @param serverIp 接続する先のIPアドレス
         */
        public Send(Context con, String serverIp){
            context = con;
            serverIpAddress = serverIp;
        }

        @Override
        /**
         * @param args0: [0]:送信したい文字列
         */
        protected Boolean doInBackground(String... args0) {
            Socket socket = null;

            try {
                socket = new Socket(serverIpAddress, port);
                clientIpAddress = getWifiInfo();

                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                pw.write(args0[0]);
                pw.flush();

            } catch (UnknownHostException e) {
                Log.e("Socket", "unknownHost");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Socket", "Error");
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

        @Override
        protected void onPostExecute(Boolean bol) {
            // Send終了後の処理
        }
    }

    /**
     * 自機のIPアドレスを取得する
     * @return IPアドレス
     */
    public String getWifiInfo() {
        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifIinfo = wifiManager.getConnectionInfo();
        int address = wifIinfo.getIpAddress();
        String ipAddressStr = ((address >> 0) & 0xFF) + "." + ((address >> 8) & 0xFF) + "."
                + ((address >> 16) & 0xFF) + "." + ((address >> 24) & 0xFF);

        return ipAddressStr;
    }

    /**
     * serverIpAddressを取得します。
     * @return serverIpAddress
     */
    public String getServerIpAddress() {
        return serverIpAddress;
    }

    /**
     * clientIpAddressを取得します。
     * @return clientIpAddress
     */
    public String getClientIpAddress() {
        return clientIpAddress;
    }
}

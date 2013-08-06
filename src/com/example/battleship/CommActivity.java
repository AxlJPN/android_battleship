package com.example.battleship;

import com.example.battleship.CommModule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }

    public class connectRecieve extends CommModule.Recieve {
        public connectRecieve(CommModule commModule, Context con) {
            commModule.super(con);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                // Intent intent = new Intent(context, nextActivity.class);
                // startActivity(intent);
            }
        }
    }

    public class connectSend extends CommModule.Send {
        public connectSend(CommModule commModule, Context con) {
            commModule.super(con);
        }
        @Override
        protected Boolean doInBackground(String... args0) {
            return super.doInBackground("1");
        }
        @Override
        protected void onPostExecute(Boolean bol) {

        }
    }

    /*
     * 接続設定画面表示
     */
    public void connectionSetting(){
        this.showSelectServerCliendDialog();
    }

    /*
     * サーバー，クライアント選択画面
     */
    private void showSelectServerCliendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Connection destination");

        builder.setMessage("Please select Server/Client");

        builder.setPositiveButton("server", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // サーバー側接続画面表示
                showRecieveDialog();
            }
        });

        builder.setNegativeButton("client", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // クライアント側接続画面表示
                showSendDialog();
            }
        });

        builder.show();
    }

    /*
     * サーバー側接続画面表示
     */
    private void showRecieveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Connection destination(Server)");

        CommModule comm = new CommModule(context);
        // IP取得
        builder.setMessage("Please wait.\nYour IP is: " +comm.getWifiInfo());

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // サーバー，クライアント選択画面にもどる
                showSelectServerCliendDialog();
            }

        });

        builder.show();

        // 待機＋ゲームスタート
        new connectRecieve(comm, this).execute();
    }

    /*
     * クライアント側接続画面表示
     */
    private void showSendDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Connection destination(Client)");

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifIinfo = wifiManager.getConnectionInfo();
        int address = wifIinfo.getIpAddress();
        String ipAddressStr = ((address >> 0) & 0xFF) + "." + ((address >> 8) & 0xFF) + "."
                + ((address >> 16) & 0xFF) + "." + ((address >> 24) & 0xFF);

        builder.setMessage("Please specify the connection destination.\nYour IP is: " + ipAddressStr);
        final EditText editView = new EditText(this);
        builder.setView(editView);

        builder.setPositiveButton("connect", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "OK", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // サーバー，クライアント選択画面にもどる
                showSelectServerCliendDialog();
            }

        });

        builder.show();
    }
}

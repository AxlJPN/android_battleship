package com.example.battleship;

import com.example.battleship.CommModule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 初期接続設定クラス
 *
 * @author T.Sasaki
 *
 */
public class CommActivity extends Activity {
    Context context = null;
    int port = 8080;
    String serverIpAddress = null;
    String clientIpAddress = null;
    
    CommModule comm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }

    /**
     * 初期接続の受信
     * @author T.Sasaki
     *
     */
    public class connectRecieve extends CommModule.Recieve {
        public connectRecieve(CommModule commModule, Context con) {
            commModule.super(con);
        }
        
        @Override
        protected String doInBackground(String... args0) {
            return super.doInBackground(args0);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result == null)
                Toast.makeText(context, "unconnected", Toast.LENGTH_SHORT).show();
            else if(result.equals("1")) {
                Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 初期接続の送信
     * @author T.Sasaki
     *
     */
    public class connectSend extends CommModule.Send {
        public connectSend(CommModule commModule, Context con, String serverIp) {
            commModule.super(con, serverIp);
        }
        @Override
        protected Boolean doInBackground(String... args0) {
            return super.doInBackground("1");
        }
        @Override
        protected void onPostExecute(Boolean bol) {
            super.onPostExecute(bol);
            if(bol)
                Toast.makeText(context, "送信しました", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "送信できませんでした", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 接続設定画面表示
     */
    public void connectionSetting(){

        comm = new CommModule(context);
        
        // 接続確認
        if(comm.getClientIpAddress() == null || comm.getServerIpAddress() == null)
            this.showSelectServerCliendDialog();
    }

    /**
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

    /**
     * サーバー側接続画面表示
     */
    private void showRecieveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Connection destination(Server)");

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
        new connectRecieve(comm, context).execute();
        
        this.clientIpAddress = comm.getClientIpAddress();
        this.serverIpAddress = comm.getServerIpAddress();
        
    }

    /**
     * クライアント側接続画面表示
     */
    private void showSendDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Connection destination(Client)");

        builder.setMessage("Please specify the connection destination.\nYour IP is: " + comm.getWifiInfo());
        final EditText editView = new EditText(this);
        builder.setView(editView);

        builder.setPositiveButton("connect", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // サーバー側に接続する
                new connectSend(comm, context, editView.getText().toString()).execute();
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

package com.example.battleship;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.battleship.code.ShipType;

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

    protected String shortName = null;
    protected ArrayAdapter<Ship> _adapter;
    protected AlertDialog _alertDialog;
    private AlertDialog _recieveDialog;
    private AlertDialog _selectServerCliendDialog;
    private AlertDialog _sendDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }

    /**
     * 初期接続の受信
     *
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
                Toast.makeText(context, "disconnected", Toast.LENGTH_SHORT).show();
            else if(result.equals("1")) {
                Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
                _recieveDialog.dismiss();

                // 船を配置するダイアログを表示
                _alertDialog = createSelectShipDialog(CommActivity.this);
                _alertDialog.show();

            }
        }
    }

    /**
     * 初期接続の送信
     *
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
    public void connectionSetting() {

        comm = new CommModule(context);

        // 接続確認
        if (comm.getClientIpAddress() == null || comm.getServerIpAddress() == null)
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

        _selectServerCliendDialog = builder.create();
        _selectServerCliendDialog.show();
    }

    class onSelectShipDialogClickListener implements OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            shortName = _adapter.getItem(which).getShortShipName();
            dialog.dismiss();
        }
    }

    /**
     * サーバー側接続画面表示
     */
    private void showRecieveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Connection destination(Server)");

        // IP取得
        builder.setMessage("Please wait.\nYour IP is: " + comm.getWifiInfo());

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // サーバー，クライアント選択画面にもどる
                showSelectServerCliendDialog();
            }
        });

        _recieveDialog = builder.create();
        _recieveDialog.show();

        // 待機＋ゲームスタート
        connectRecieve conRec = new connectRecieve(comm, context);
        conRec.execute();

        this.clientIpAddress = comm.getClientIpAddress();
        this.serverIpAddress = comm.getServerIpAddress();

        // doInBackgroundを終了させる
        conRec.isCancelled();


    }

    /**
     * クライアント側接続画面表示
     */
    private void showSendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Connection destination(Client)");

        builder.setMessage("Please specify the connection destination.\nYour IP is: "
                + comm.getWifiInfo());
        final EditText editView = new EditText(this);
        builder.setView(editView);

        builder.setPositiveButton("connect", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // サーバー側に接続する
                new connectSend(comm, context, editView.getText().toString()).execute();

//                // Pause Test
//                // ボタンは押せないようにする
//                for(int i = 0; i < 25; i++){
//                    Button but = (Button)findViewById(i);
//                    but.setEnabled(false);
//                }


                // 船を配置するダイアログを表示
                _alertDialog = createSelectShipDialog(CommActivity.this);
                _alertDialog.show();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // サーバー，クライアント選択画面にもどる
                showSelectServerCliendDialog();
            }

        });

        _sendDialog = builder.create();
        _sendDialog.show();
    }

    protected AlertDialog createSelectShipDialog(Context context) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("配置する船の選択");

        _adapter = new ArrayAdapter<Ship>(context,
                android.R.layout.simple_list_item_single_choice);

        _adapter.add(new Ship(ShipType.BATTLESHIP, "戦艦", "B"));
        _adapter.add(new Ship(ShipType.DESTROYER, "駆逐艦", "D"));
        _adapter.add(new Ship(ShipType.SUBMARINE, "潜水艦", "S"));

        builder.setSingleChoiceItems(_adapter, 0, new onSelectShipDialogClickListener());
        dialog = builder.create();
        return dialog;
    }
}

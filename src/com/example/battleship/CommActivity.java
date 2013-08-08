package com.example.battleship;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.battleship.code.ShipType;

/**
 * 初期接続設定クラス
 * 
 * @author T.Sasaki
 * 
 */
public class CommActivity extends Activity implements Common {

    /**
     * デバッグフラグ
     */
    static final boolean DEBUG = false;

    Context _context = null;
    int port = 8080;

    String _myIpAddress = null;
    String _otherIpAddress = null;

    static final String FIRST_TURN = "先攻";
    static final String SECOND_TURN = "後攻";
    String _playerFirstTurn = FIRST_TURN;

    CommModule comm = null;

    protected String shortName = null;
    protected ArrayAdapter<Ship> _adapter;
    protected AlertDialog _alertDialog;
    private AlertDialog _recieveDialog;
    private AlertDialog _selectServerCliendDialog;
    private AlertDialog _sendDialog;
    private AlertDialog _recieveRetryDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = getApplicationContext();
    }

    /**
     * 初期接続の受信
     * 
     * @author T.Sasaki
     * 
     */
    public class connectRecieve extends CommModule.Recieve {
        static final int timeout = DEBUG ? 1 : 20000;

        public connectRecieve(CommModule commModule, Context con) {
            commModule.super(con, 8080, timeout);
        }

        @Override
        protected String doInBackground(String... args0) {
            return super.doInBackground(args0);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // ターン終了時にアクセスするためにIPアドレスを取得する
            _myIpAddress = comm.getServerIpAddress();
            _otherIpAddress = comm.getClientIpAddress();

            if (DEBUG) {
                Toast.makeText(_context, "通信できないから船選んでー", Toast.LENGTH_SHORT).show();
                _recieveDialog.dismiss();

                // 船を配置するダイアログを表示
                _alertDialog = createSelectShipDialog(CommActivity.this);
                _alertDialog.show();
            } else {
                if (result == null) {
                    Toast.makeText(_context, "接続できませんでした", Toast.LENGTH_SHORT).show();
                    // 受信リトライ
                    createRecieveRetryDialog(CommActivity.this);
                } else if (result.equals("1")) {
                    Toast.makeText(_context, "接続しました", Toast.LENGTH_SHORT).show();
                    _recieveDialog.dismiss();

                    // 船を配置するダイアログを表示
                    _alertDialog = createSelectShipDialog(CommActivity.this);
                    _alertDialog.show();

                }
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

            // ターン終了時にアクセスするためにIPアドレスを取得する
            _myIpAddress = comm.getClientIpAddress();
            _otherIpAddress = comm.getServerIpAddress();

            if (DEBUG) {
                Toast.makeText(_context, "通信してないけど船の配置しよう", Toast.LENGTH_SHORT).show();
                _sendDialog.dismiss();

                // 船を配置するダイアログを表示
                _alertDialog = createSelectShipDialog(CommActivity.this);
                _alertDialog.show();
            } else {
                if (bol) {
                    Toast.makeText(_context, "送信しました", Toast.LENGTH_SHORT).show();
                    _sendDialog.dismiss();

                    // 船を配置するダイアログを表示
                    _alertDialog = createSelectShipDialog(CommActivity.this);
                    _alertDialog.show();
                } else {
                    Toast.makeText(_context, "送信できませんでした", Toast.LENGTH_SHORT).show();
                    // リトライ
                    showSendDialog();
                }
            }
        }
    }

    /**
     * ターン終了時の受信
     * 
     * @author T.Sasaki
     * 
     */
    public class turnEndRecieve extends CommModule.Recieve {
        static final int timeout = DEBUG ? 1 : 20000;
        String[] param = { "1" };

        public turnEndRecieve(CommModule commModule, Context con) {
            commModule.super(con, 8080, timeout);
        }

        @Override
        protected String doInBackground(String... args0) {
            return super.doInBackground(param);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result == null) {
                Toast.makeText(_context, "通信出来ませんでした", Toast.LENGTH_SHORT).show();
                createTurnEndRecieveRetryDialog(_context);
            } else if (result.equals("1")) {
                Toast.makeText(_context, "自分の番です", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < WIDTH * HEIGHT; i++) {
                    ((Button) findViewById(i)).setEnabled(true);
                }

                if (result.equals("攻撃")) {
                    // 相手側が攻撃を行ったとき
                } else if (result.equals("移動")) {
                    // 相手側が移動を行ったとき
                }

            }
        }
    }

    /**
     * 移動終了後の送信
     * 
     * @author T.Sasaki
     * 
     */
    public class moveSend extends CommModule.Send {
        public moveSend(CommModule commModule, Context con) {
            commModule.super(con, _otherIpAddress);
            Log.v("send", "sendToIp:" + _otherIpAddress);
        }

        @Override
        protected Boolean doInBackground(String... args0) {
            // 移動したのでパラメータを送信する
            return super.doInBackground("1");
        }

        @Override
        protected void onPostExecute(Boolean bol) {
            super.onPostExecute(bol);

            // 自分の番が終了する
            Toast.makeText(_context, "自分の番を終了します", Toast.LENGTH_SHORT).show();

            if (DEBUG) {
                // デバッグ時
                Toast.makeText(_context, "[相手の移動が終わって自分の番]", Toast.LENGTH_SHORT).show();
            } else {
                if (bol) {
                    Toast.makeText(_context, "相手の番に変わります", Toast.LENGTH_SHORT).show();
                    // 相手の番が終わるまで待機
                    for (int i = 0; i < WIDTH * HEIGHT; i++) {
                        ((Button) findViewById(i)).setEnabled(false);
                    }
                    turnEndRecieve teRec = new turnEndRecieve(comm, _context);
                    teRec.execute();
                    teRec.isCancelled();
                } else {
                    Toast.makeText(_context, "通信出来ませんでした", Toast.LENGTH_SHORT).show();
                    // リトライ
                    createTurnEndSendRetryDialog(_context);
                }
            }
        }
    }

    /**
     * 攻撃終了後の送信
     * 
     * @author T.Sasaki
     * 
     */
    public class attackSend extends CommModule.Send {
        public attackSend(CommModule commModule, Context con) {
            commModule.super(con, _otherIpAddress);
        }

        @Override
        /**
         * @args0 [0]:座標，ダメージ (例) {1,5}1
         */
        protected Boolean doInBackground(String... args0) {
            // 攻撃したので座標，ダメージを送信する
            return super.doInBackground(args0);
        }

        @Override
        protected void onPostExecute(Boolean bol) {
            super.onPostExecute(bol);
            if (DEBUG) {
                // デバッグ時
                Toast.makeText(_context, "[相手の攻撃が終わって自分の番]", Toast.LENGTH_SHORT).show();
            } else {
                if (bol) {
                    Toast.makeText(_context, "相手の番に変わります", Toast.LENGTH_SHORT).show();
                    // 相手の番が終わるまで待機
                    turnEndRecieve rec = new turnEndRecieve(comm, _context);
                    rec.execute();
                    rec.isCancelled();
                } else {
                    Toast.makeText(_context, "通信出来ませんでした", Toast.LENGTH_SHORT).show();
                    // リトライ

                }
            }
        }
    }

    /**
     * 接続設定画面表示
     */
    public void connectionSetting() {

        comm = new CommModule(_context);

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
                // サーバー側は先行
                _playerFirstTurn = FIRST_TURN;
            }
        });

        builder.setNegativeButton("client", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // クライアント側接続画面表示
                showSendDialog();
                // クライアント側は後攻
                _playerFirstTurn = SECOND_TURN;
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
        connectRecieve conRec = new connectRecieve(comm, _context);
        conRec.execute();

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
                connectSend conSend = new connectSend(comm, _context, editView.getText().toString());
                conSend.execute();

                // doInBackgroundを終了させる
                conSend.isCancelled();

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

        _adapter = new ArrayAdapter<Ship>(context, android.R.layout.simple_list_item_single_choice);

        _adapter.add(new Ship(ShipType.BATTLESHIP, "戦艦", "B"));
        _adapter.add(new Ship(ShipType.DESTROYER, "駆逐艦", "D"));
        _adapter.add(new Ship(ShipType.SUBMARINE, "潜水艦", "S"));

        builder.setSingleChoiceItems(_adapter, 0, new onSelectShipDialogClickListener());
        dialog = builder.create();
        return dialog;
    }

    /**
     * 初期受信リトライダイアログの表示
     * 
     * @param context
     */
    protected void createRecieveRetryDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Connection destination");

        builder.setMessage("受信に失敗しました．\n リトライしますか？");

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // リトライ
                showRecieveDialog();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 終了
            }
        });

        // 初期設定受信ダイアログを閉じる
        _recieveDialog.dismiss();

        _recieveRetryDialog = builder.create();
        _recieveRetryDialog.show();
    }

    /**
     * 受信リトライダイアログの表示
     * 
     * @param context
     */
    protected void createTurnEndRecieveRetryDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Connection destination");

        builder.setMessage("受信に失敗しました．\n リトライしますか？");

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // リトライ
                turnEndRecieve teRec = new turnEndRecieve(comm, CommActivity.this);
                teRec.execute();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 終了
            }
        });

        _recieveRetryDialog = builder.create();
        _recieveRetryDialog.show();
    }

    /**
     * 送信リトライダイアログの表示
     * 
     * @param context
     */
    protected void createTurnEndSendRetryDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Connection destination");

        builder.setMessage("送信に失敗しました．\n リトライしますか？");

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // リトライ
                moveSend mvSend = new moveSend(comm, CommActivity.this);
                mvSend.execute();
                // doInBackgroundの終了
                mvSend.isCancelled();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 終了
            }
        });

        _recieveRetryDialog = builder.create();
        _recieveRetryDialog.show();
    }
}

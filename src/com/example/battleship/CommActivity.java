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

    static final String ATTACK_TURN = "1";
    static final String MOVE_TURN = "2";

    CommModule comm = null;

    protected String _shortName = null;
    protected ShipType _shipType;
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
    public class ConnectRecieve extends CommModule.Recieve {
        static final int timeout = DEBUG ? 1 : 20000;

        public ConnectRecieve(CommModule commModule, Context con) {
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
                    Toast.makeText(_context, MSG_CONNECT_FAIL, Toast.LENGTH_SHORT).show();
                    // 受信リトライ
                    createRecieveRetryDialog(CommActivity.this);
                } else if (result.equals("1")) {
                    Toast.makeText(_context, MSG_CONNECT_SUCCESS, Toast.LENGTH_SHORT).show();
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
    public class ConnectSend extends CommModule.Send {
        public ConnectSend(CommModule commModule, Context con, String serverIp) {
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
                _alertDialog.setCanceledOnTouchOutside(false);
                _alertDialog.show();
            } else {
                if (bol) {
                    Toast.makeText(_context, MSG_CONNECT_SUCCESS, Toast.LENGTH_SHORT).show();
                    _sendDialog.dismiss();

                    // 船を配置するダイアログを表示
                    _alertDialog = createSelectShipDialog(CommActivity.this);
                    _alertDialog.setCanceledOnTouchOutside(false);
                    _alertDialog.show();
                } else {
                    Toast.makeText(_context, MSG_CONNECT_FAIL, Toast.LENGTH_SHORT).show();
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
    public class TurnEndRecieve extends CommModule.Recieve {
        static final int timeout = DEBUG ? 1 : 20000;
        String[] param = { "1" };

        public TurnEndRecieve(CommModule commModule, Context con) {
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
            } else {
                // 分割
                String[] params = result.split(",");
                String turnType = params[0];

                LogMsg.AddLogMessage("自分の番です");
                for (int i = 0; i < WIDTH * HEIGHT; i++) {
                    ((Button) findViewById(i)).setEnabled(true);
                }

                if (turnType.equals(ATTACK_TURN)) {
                    int pointX = Integer.parseInt(params[1]);
                    int pointY = Integer.parseInt(params[2]);
                    int attackPower = Integer.parseInt(params[3]);
                    // 相手側が攻撃を行ったとき
                    LogMsg.AddLogMessage("相手が攻撃をしました");
                    // 攻撃判定

                } else if (turnType.equals(MOVE_TURN)) {
                    // 相手側が移動を行ったとき
                    LogMsg.AddLogMessage(params[1]);
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
    public class MoveSend extends CommModule.Send {
        String logText = null;

        public MoveSend(CommModule commModule, Context con, String log) {
            commModule.super(con, _otherIpAddress);
            Log.v("send", "sendToIp:" + _otherIpAddress);
            logText = log;
        }

        @Override
        protected Boolean doInBackground(String... args0) {
            // 移動したのでパラメータを送信する
            return super.doInBackground("2," + logText);
        }

        @Override
        protected void onPostExecute(Boolean bol) {
            super.onPostExecute(bol);

            // 自分の番が終了する
            LogMsg.AddLogMessage("自分の番を終了します");

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
                    TurnEndRecieve teRec = new TurnEndRecieve(comm, _context);
                    teRec.execute();
                    teRec.isCancelled();
                } else {
                    LogMsg.AddLogMessage("通信出来ませんでした");
                    // リトライ
                    createTurnEndSendRetryDialog(_context, logText);
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
    public class AttackSend extends CommModule.Send {
        int pointX = 0;
        int pointY = 0;
        int power = 0;

        public AttackSend(CommModule commModule, Context con, int pX, int pY, int p) {
            commModule.super(con, _otherIpAddress);
            pointX = pX;
            pointY = pY;
            power = p;
        }

        @Override
        /**
         * @args0 [0]:座標，ダメージ (例) 1,5,1
         */
        protected Boolean doInBackground(String... args0) {
            // 攻撃したので座標，ダメージを送信する
            return super.doInBackground("2," + pointX + "," + pointY + "," + power);
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
                    TurnEndRecieve rec = new TurnEndRecieve(comm, _context);
                    rec.execute();
                    rec.isCancelled();

                } else {
                    LogMsg.AddLogMessage("通信出来ませんでした");
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
        _selectServerCliendDialog.setCanceledOnTouchOutside(false);
        _selectServerCliendDialog.show();
    }

    class OnSelectShipDialogClickListener implements OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            _shortName = _adapter.getItem(which).getShortShipName();
            _shipType = _adapter.getItem(which).getShipType();
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
        _recieveDialog.setCanceledOnTouchOutside(false);
        _recieveDialog.show();

        // 待機＋ゲームスタート
        ConnectRecieve conRec = new ConnectRecieve(comm, _context);
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
        editView.setText("192.168.");
        builder.setView(editView);

        builder.setPositiveButton("connect", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // サーバー側に接続する
                ConnectSend conSend = new ConnectSend(comm, _context, editView.getText().toString());
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
        _sendDialog.setCanceledOnTouchOutside(false);
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

        builder.setSingleChoiceItems(_adapter, 0, new OnSelectShipDialogClickListener());
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
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
        _recieveRetryDialog.setCanceledOnTouchOutside(false);
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
                TurnEndRecieve teRec = new TurnEndRecieve(comm, CommActivity.this);
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
        _recieveRetryDialog.setCanceledOnTouchOutside(false);
        _recieveRetryDialog.show();
    }

    /**
     * 送信リトライダイアログの表示
     * 
     * @param context
     */
    protected void createTurnEndSendRetryDialog(Context context, String logText) {
        final String log = logText;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Connection destination");

        builder.setMessage("送信に失敗しました．\n リトライしますか？");

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // リトライ
                MoveSend mvSend = new MoveSend(comm, CommActivity.this, log);
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
        _recieveRetryDialog.setCanceledOnTouchOutside(false);
        _recieveRetryDialog.show();
    }

    protected int getPointX(int id) {
        return id % Common.WIDTH;
    }

    protected int getPointY(int id) {
        return id / Common.WIDTH;
    }
}

package com.example.battleship;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.Toast;

import com.example.battleship.code.AttackResult;
import com.example.battleship.code.ShipType;

public class BattleShip extends CommActivity {

    // 変数
    BattleshipClass _battleShip = null;
    private int _selectedIndex;
    private Button _selectedButton;
    private ArrayList<ArrayList<Integer>> _btnIDs;
    private int _selectButtonId;
    private Drawable _draw;
    public static ArrayAdapter<String> _logAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_ship);

        // 端末接続設定
        connectionSetting();

        _btnIDs = new ArrayList<ArrayList<Integer>>();

        // マス作成
        this.SetButtons(WIDTH, HEIGHT);
        this.SetListView(WIDTH);

        _battleShip = new BattleshipClass();
    }

    /**
     * ボタンを動的に配置
     * 
     * @param x
     * @param y
     */
    private void SetButtons(int x, int y) {
        int id = 0;
        TableLayout layout = (TableLayout) findViewById(R.id.TableLayout1);

        for (int i = 0; i < x; i++) {
            TableRow row = new TableRow(this);
            ArrayList<Integer> aryList = new ArrayList<Integer>();
            for (int j = 0; j < y; j++) {
                // ボタンを生成
                Button button = new Button(this);
                // IDを設定
                button.setId(id);
                button.setText("");

                row.addView(button);

                _draw = button.getBackground();

                // ボタンイベントを追加
                button.setOnClickListener(new OnClickButtonWhenFirst());

                // ボタンIDを取得
                aryList.add(id);

                // IDをインクリメント
                id++;
            }
            _btnIDs.add(aryList);
            layout.addView(row);
        }
    }

    /**
     * リストビューを追加する
     * 
     * @param x
     */
    private void SetListView(int x) {
        _logAdapter = new ArrayAdapter<String>(this, R.layout.log_row);
        TableLayout layout = (TableLayout) findViewById(R.id.TableLayout1);

        LayoutParams param = new LayoutParams();
        param.span = x;
        param.width = LayoutParams.MATCH_PARENT;
        param.height = LayoutParams.MATCH_PARENT;

        // ListView作成
        ListView list = new ListView(this);
        list.setId(LISTVIEWID);
        list.setAdapter(_logAdapter);

        layout.addView(list, param);
    }

    /**
     * 選択されたテキストと同じ物を見つけてクリアする
     * 
     * @param selectText
     */
    public void ClearButtonText(String selectText) {
        // ボタンを一個ずつ見て、同じ文字が存在するか確認
        for (ArrayList<Integer> btnId : _btnIDs) {
            for (int id : btnId) {
                Button button = (Button) findViewById(id);
                if (button.getText().toString().equals(selectText)) {
                    // 同じ文字が見つかったらクリアする
                    button.setText("");
                    break;
                }
            }
        }
    }

    private class OnClickButtonWhenFirst implements OnClickListener {

        @Override
        public void onClick(View v) {
            _selectedButton = (Button) findViewById(v.getId());
            int buttonId = _selectedButton.getId();
            int pointX = getPointX(buttonId);
            int pointY = getPointY(buttonId);

            ClearButtonText(_shortName);
            _selectedButton.setText(_shortName);
            _battleShip.SetPosition(pointX, pointY, _shipType);

            // 配置されている船の数を取得
            int shipCount = getShipCount();

            if (shipCount < 3) {
                _alertDialog = createSelectShipDialog(BattleShip.this);
                _alertDialog.show();
            } else {
                // ゲーム開始
                Toast.makeText(_context, "ゲームを開始します", Toast.LENGTH_SHORT).show();
                Toast.makeText(_context, "あなたが" + _playerFirstTurn + "です", Toast.LENGTH_SHORT)
                        .show();

                for (int i = 0; i < WIDTH * HEIGHT; i++) {
                    ((Button) findViewById(i)).setOnClickListener(new OnClickButtonGameStart());
                }

                if (_playerFirstTurn.equals(FIRST_TURN)) {
                    // 先行
                    _playerFirstTurn = SECOND_TURN;
                    for (int i = 0; i < WIDTH * HEIGHT; i++) {
                        ((Button) findViewById(i)).setEnabled(true);
                    }

                } else {
                    // 後攻
                    _playerFirstTurn = FIRST_TURN;

                    Toast.makeText(_context, "待機中", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < WIDTH * HEIGHT; i++) {
                        ((Button) findViewById(i)).setEnabled(false);
                    }
                    turnEndRecieve teRec = new turnEndRecieve(comm, _context);
                    teRec.execute();
                    teRec.isCancelled();
                }
            }
        }

    }

    /**
     * 配置されている船の数を返す
     * 
     * @return 配置されている船の数
     */
    public int getShipCount() {
        int shipCount = 0;
        for (int i = 0; i < Common.WIDTH * Common.HEIGHT; i++) {
            Button button = (Button) findViewById(i);
            if (button.getText().toString().length() > 0) {
                shipCount++;
            }
        }
        return shipCount;
    }

    /**
     * ゲーム開始後のボタンクリックイベント
     * 
     * @author N.Wada
     * 
     */
    private class OnClickButtonGameStart implements OnClickListener {

        @Override
        public void onClick(final View v) {
            // ボタンのテキストが何もない場合、何もしない
            Button button = (Button) findViewById(v.getId());
            if (button.getText().toString().equals("")) {
                return;
            }

            // 行動を選択させる
            AlertDialog.Builder builder = new AlertDialog.Builder(BattleShip.this);
            builder.setTitle("行動選択");
            builder.setMessage("攻撃／移動どちらを行いますか？");
            builder.setNegativeButton("攻撃", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    _selectButtonId = v.getId();
                    ArrayList<Button> buttons = GetAttackableButton(v.getId());
                    int color = Color.RED;
                    int cnt = 0;

                    for (int i = 0; i < WIDTH * HEIGHT; i++) {
                        Button btn = (Button) findViewById(i);

                        if (i == buttons.get(cnt).getId()) {
                            btn.setBackgroundColor(color);
                            btn.setOnClickListener(new OnClickAttackButton());
                            cnt++;
                        } else {
                            btn.setOnClickListener(null);
                        }

                        // 対象のボタン数以上カウントがされたら処理を終了する
                        if (cnt >= buttons.size())
                            break;
                    }
                }
            });
            builder.setNeutralButton("移動", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 移動ができるマスを青くする
                    _selectButtonId = v.getId();
                    int colNum = _selectButtonId % WIDTH;
                    int rowNum = _selectButtonId / WIDTH;
                    int color = Color.BLUE;

                    for (int btnId : _btnIDs.get(rowNum)) {
                        if (btnId == _selectButtonId) {
                            continue;
                        }
                        Button btn = (Button) findViewById(btnId);
                        if (btn.getText().toString().isEmpty()) {
                            // 移動できるのは他の船がいない箇所
                            btn.setBackgroundColor(color);
                            btn.setOnClickListener(new OnClickMoveButton());
                        } else {
                            // 他の船がいる場合、onClickListenerをクリアする
                            btn.setOnClickListener(null);
                        }
                    }

                    for (ArrayList<Integer> list : _btnIDs) {
                        int btnId = list.get(colNum);
                        if (btnId == _selectButtonId) {
                            continue;
                        }
                        Button btn = (Button) findViewById(btnId);
                        if (btn.getText().toString().isEmpty()) {
                            // 移動できるのは他の船がいない箇所
                            btn.setBackgroundColor(color);
                            btn.setOnClickListener(new OnClickMoveButton());
                        } else {
                            // 他の船がいる場合、onClickListenerをクリアする
                            btn.setOnClickListener(null);
                        }
                    }
                }
            });
            builder.setPositiveButton("キャンセル", null);
            builder.show();
        }

        /**
         * 攻撃先が選択された際のイベント
         * 
         * @author N.Wada
         * 
         */
        private class OnClickAttackButton implements OnClickListener {

            @Override
            public void onClick(View v) {
                int pointX = v.getId() % WIDTH;
                int pointY = v.getId() / WIDTH;
                ShipType type = ShipType.BATTLESHIP;

                Button btn = (Button) findViewById(_selectButtonId);

                String btnText = btn.getText().toString();
                if (btnText.equals("B")) {
                    type = ShipType.BATTLESHIP;
                } else if (btnText.equals("D")) {
                    type = ShipType.BATTLESHIP;
                } else if (btnText.equals("S")) {
                    type = ShipType.BATTLESHIP;
                }

                AttackResult result = _battleShip.AttackEnemy(pointX, pointY, type);
                ClearButtonColor();
                SetGameStartEvent();

                String logText = LogMsg.MakeAttackLogText(pointX, pointY, type, result);
                LogMsg.AddLogMessage(logText);
            }
        }

        /**
         * 移動先が選択された際のイベント
         * 
         * @author N.Wada
         * 
         */
        private class OnClickMoveButton implements OnClickListener {

            @Override
            public void onClick(View v) {
                int pointX = v.getId() % WIDTH;
                int pointY = v.getId() / WIDTH;
                ShipType type = ShipType.BATTLESHIP;

                Button btn = (Button) findViewById(_selectButtonId);

                // 移動する船の種類を取得
                String btnText = btn.getText().toString();
                if (btnText.equals("B")) {
                    type = ShipType.BATTLESHIP;
                    ClearButtonText("B");
                    ((Button) findViewById(v.getId())).setText("B");
                } else if (btnText.equals("D")) {
                    type = ShipType.DESTROYER;
                    ClearButtonText("D");
                    ((Button) findViewById(v.getId())).setText("D");
                } else if (btnText.equals("S")) {
                    type = ShipType.SUBMARINE;
                    ClearButtonText("S");
                    ((Button) findViewById(v.getId())).setText("S");
                }

                String logText = LogMsg.MakeMoveLogText(_battleShip.GetPositionX(type),
                        _battleShip.GetPositionY(type), pointX, pointY, type);
                LogMsg.AddLogMessage(logText);
                // TODO 通信先にログを投げる

                _battleShip.Movement(pointX, pointY, type);
                ClearButtonColor();
                SetGameStartEvent();

                // 終了したことを相手側に送信する
                moveSend mvSend = new moveSend(comm, BattleShip.this);
                mvSend.execute();
                // doInBackgroundの終了
                // mvSend.isCancelled();
            }
        }

        /**
         * ボタンの色を解除する
         */
        private void ClearButtonColor() {
            for (int i = 0; i < WIDTH * HEIGHT; i++) {
                // 色を戻す
                ((Button) findViewById(i)).setBackgroundDrawable(_draw);
            }
        }

        /**
         * ボタンのイベントをOnClickButtonGameStartに変更する
         */
        private void SetGameStartEvent() {
            for (int i = 0; i < WIDTH * HEIGHT; i++) {
                ((Button) findViewById(i)).setOnClickListener(new OnClickButtonGameStart());
            }
        }

        /**
         * 攻撃できるボタンのリストを取得する
         * 
         * @param id
         * @return
         */
        private ArrayList<Button> GetAttackableButton(int id) {
            int rowNumber = id / WIDTH;
            ArrayList<Button> buttonIDs = new ArrayList<Button>();

            if ((id % WIDTH) == 0) {
                // 左にボタンはない
                if (rowNumber == 0) {
                    // 上にボタンはない
                    // 最上段左端
                    buttonIDs.add((Button) findViewById(id + 1));
                    buttonIDs.add((Button) findViewById(id + WIDTH));
                    buttonIDs.add((Button) findViewById(id + WIDTH + 1));
                } else if (rowNumber == (HEIGHT - 1)) {
                    // 下にボタンはない
                    // 最下段左端
                    buttonIDs.add((Button) findViewById(id + (WIDTH * -1)));
                    buttonIDs.add((Button) findViewById(id + ((WIDTH - 1) * -1)));
                    buttonIDs.add((Button) findViewById(id + 1));
                } else {
                    // 左端
                    buttonIDs.add((Button) findViewById(id + (WIDTH * -1)));
                    buttonIDs.add((Button) findViewById(id + ((WIDTH - 1) * -1)));
                    buttonIDs.add((Button) findViewById(id + 1));
                    buttonIDs.add((Button) findViewById(id + WIDTH));
                    buttonIDs.add((Button) findViewById(id + WIDTH + 1));
                }
            } else if ((id % WIDTH) == (WIDTH - 1)) {
                // 右にボタンはない
                if (rowNumber == 0) {
                    // 上にボタンはない
                    // 最上段右端
                    buttonIDs.add((Button) findViewById(id - 1));
                    buttonIDs.add((Button) findViewById(id + (WIDTH - 1)));
                    buttonIDs.add((Button) findViewById(id + WIDTH));
                } else if (rowNumber == (HEIGHT - 1)) {
                    // 下にボタンはない
                    // 最下段右端
                    buttonIDs.add((Button) findViewById(id + ((WIDTH + 1) * -1)));
                    buttonIDs.add((Button) findViewById(id + (WIDTH * -1)));
                    buttonIDs.add((Button) findViewById(id - 1));
                } else {
                    // 右端
                    buttonIDs.add((Button) findViewById(id + ((WIDTH + 1) * -1)));
                    buttonIDs.add((Button) findViewById(id + (WIDTH * -1)));
                    buttonIDs.add((Button) findViewById(id - 1));
                    buttonIDs.add((Button) findViewById(id + (WIDTH - 1)));
                    buttonIDs.add((Button) findViewById(id + WIDTH));
                }
            } else {
                // 左右にボタンがある
                if (rowNumber == 0) {
                    // 上にボタンはない
                    buttonIDs.add((Button) findViewById(id - 1));
                    buttonIDs.add((Button) findViewById(id + 1));
                    buttonIDs.add((Button) findViewById(id + (WIDTH - 1)));
                    buttonIDs.add((Button) findViewById(id + WIDTH));
                    buttonIDs.add((Button) findViewById(id + (WIDTH + 1)));
                } else if (rowNumber == (HEIGHT - 1)) {
                    // 下にボタンはない
                    buttonIDs.add((Button) findViewById(id + ((WIDTH + 1) * -1)));
                    buttonIDs.add((Button) findViewById(id + (WIDTH * -1)));
                    buttonIDs.add((Button) findViewById(id + ((WIDTH - 1) * -1)));
                    buttonIDs.add((Button) findViewById(id - 1));
                    buttonIDs.add((Button) findViewById(id + 1));
                } else {
                    buttonIDs.add((Button) findViewById(id + ((WIDTH + 1) * -1)));
                    buttonIDs.add((Button) findViewById(id + (WIDTH * -1)));
                    buttonIDs.add((Button) findViewById(id + ((WIDTH - 1) * -1)));
                    buttonIDs.add((Button) findViewById(id - 1));
                    buttonIDs.add((Button) findViewById(id + 1));
                    buttonIDs.add((Button) findViewById(id + (WIDTH - 1)));
                    buttonIDs.add((Button) findViewById(id + WIDTH));
                    buttonIDs.add((Button) findViewById(id + (WIDTH + 1)));
                }
            }

            return buttonIDs;
        }

    }

    private DialogInterface.OnClickListener onDialogClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String selectText = _adapter.getItem(which).getShipName();
            ClearButtonText(selectText);

            _selectedIndex = which;
            _selectedButton.setText(selectText);
            _alertDialog.dismiss();
        }
    };
}

package com.example.battleship;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

public class BattleShip extends CommActivity implements Common {

    // 変数
    BattleshipClass _battleShip = null;
    private int _selectedIndex;
    private Button _selectedButton;
    private ArrayList<ArrayList<Integer>> _btnIDs;

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
        TableLayout layout = (TableLayout) findViewById(R.id.TableLayout1);
        TableRow row = new TableRow(this);

        LayoutParams param = new LayoutParams();
        param.span = x;

        // ListView作成
        ListView list = new ListView(this);
        list.setId(LISTVIEWID);

        row.addView(list, param);
        layout.addView(row);
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

            ClearButtonText(shortName);
            _selectedButton.setText(shortName);

            // 配置されている船の数を取得
            int shipCount = getShipCount();

            if (shipCount < 3) {
                _alertDialog = createSelectShipDialog(BattleShip.this);
                _alertDialog.show();
            } else {
                for (int i = 0; i < WIDTH * HEIGHT; i++) {
                    ((Button) findViewById(i)).setOnClickListener(new OnClickButtonGameStart());
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
                    // TODO 自分を中心としたマスを赤くする
                    // ↓X軸の考え方↓
                    // [押されたボタンID] % WIDTH == 0 の場合、左にボタンはない
                    // [押されたボタンID] % WIDTH == WIDTH - 1 の場合、右にボタンはない
                    // それ以外の場合、左右にボタンがある(±1)

                    // ↓Y軸の考え方↓
                    // [押されたボタンID]が属している最初のArrayListが0の場合、上のボタンはない
                    // [押されたボタンID]が属している最初のArrayListがHEIGHT - 1の場合、下にボタンはない
                    // それ以外の場合、上下にボタンがある([押されたボタン] % HEIGHT ± HEIGHT)
                }
            });
            builder.setNeutralButton("移動", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO 自分の上下左右のマスを全て青くする
                    // ↓X軸の考え方↓
                    // [押されたボタンID]が属している二番目のArrayListが全て対象

                    // ↓Y軸の考え方↓
                    // 各最初のArrayList中、二番目のItemが[[押されたボタンID] % HEIGHT]のものが対象
                }
            });
            builder.setPositiveButton("キャンセル", null);
            builder.show();
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

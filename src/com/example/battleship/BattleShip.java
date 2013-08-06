package com.example.battleship;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

import com.example.battleship.code.ShipType;

public class BattleShip extends CommActivity {

    // 変数
	BattleshipClass _battleShip = null;
	private ArrayAdapter<Ship> _adapter;
	private AlertDialog _alertDialog;
	private int _selectedIndex;
	private Button _selectedButton;
	private ArrayList<ArrayList<Integer>> _btnIDs;
	
	// 定数
	// ListViewのID
	private final int LISTVIEWID = 100;
    private final int WIDTH = 5;
    private final int HEIGHT = 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battle_ship);

		// 端末接続設定
		connectionSetting();
		
		_btnIDs = new ArrayList<ArrayList<Integer>>();
		
		this.SetButtons(WIDTH, HEIGHT);
		this.SetListView(WIDTH);
		_battleShip = new BattleshipClass();

		_adapter = new ArrayAdapter<Ship>(this, android.R.layout.simple_list_item_single_choice);

		_adapter.add(new Ship(ShipType.BATTLESHIP, "戦艦"));
		_adapter.add(new Ship(ShipType.DESTROYER, "駆逐艦"));
		_adapter.add(new Ship(ShipType.SUBMARINE, "潜水艦"));
	}

	/**
	 * ボタンを動的に配置
	 * @param  x
	 * @param  y
	 */
	private void SetButtons(int x, int y){
		int id = 0;
		TableLayout layout = (TableLayout)findViewById(R.id.TableLayout1);
		
		for(int i = 0; i < x; i++){
			TableRow row = new TableRow(this);
			ArrayList<Integer> aryList = new ArrayList<Integer>();
			for(int j = 0; j < y; j++){
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
	 * @param x
	 */
	private void SetListView(int x){
		TableLayout layout = (TableLayout)findViewById(R.id.TableLayout1);
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
	 * @param selectText
	 */
	private void ClearButtonText(String selectText){
	    // ボタンを一個ずつ見て、同じ文字が存在するか確認
	    for(ArrayList<Integer> btnId : _btnIDs){
	        for(int id : btnId)
	        {
    	        Button button = (Button)findViewById(id);
    	        if(button.getText().toString().equals(selectText)){
    	            // 同じ文字が見つかったらクリアする
    	            button.setText("");
    	            break;
    	        }
	        }
	    }
	}


	private class OnClickButtonWhenFirst implements OnClickListener{

		@Override
		public void onClick(View v) {
			_selectedButton = (Button)findViewById(v.getId());
			
			AlertDialog.Builder builder = new AlertDialog.Builder(BattleShip.this);
			builder.setTitle("配置する船の選択");
			builder.setSingleChoiceItems(_adapter, _selectedIndex, onDialogClickListener);
			_alertDialog = builder.create();
			_alertDialog.show();
		}
	}
	
	/**
	 * ゲーム開始後のボタンクリックイベント
	 * @author N.Wada
	 *
	 */
	private class OnClickButtonGameStart implements OnClickListener{

        @Override
        public void onClick(View v) {
            // ボタンのテキストが何もない場合、何もしない
            Button button = (Button)findViewById(v.getId());
            if(button.getText().toString().equals("")){
                return;
            }
            
            // 行動を選択させる
            AlertDialog.Builder builder = new AlertDialog.Builder(BattleShip.this);
            builder.setTitle("行動選択");
            builder.setMessage("攻撃／移動どちらを行いますか？");
            builder.setPositiveButton("攻撃", new DialogInterface.OnClickListener() {
                
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
            builder.setNegativeButton("移動", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO 自分の上下左右のマスを全て青くする
                    // ↓X軸の考え方↓
                    // [押されたボタンID]が属している二番目のArrayListが全て対象

                    // ↓Y軸の考え方↓
                    // 各最初のArrayList中、二番目のItemが[[押されたボタンID] % HEIGHT]のものが対象
                }
            });
            builder.setNeutralButton("キャンセル", null);
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

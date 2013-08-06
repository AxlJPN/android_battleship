package com.example.battleship;

import java.util.ArrayList;
import java.util.List;

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

public class BattleShip extends Activity {
	
	BattleshipClass _battleShip = null;
	private ArrayAdapter<Ship> _adapter;
	private AlertDialog _alertDialog;
	private int _selectedIndex;
	private Button _selectedButton;
	private ArrayList<Integer> _btnIDs;
	
	// ListViewのID
	private final int _listViewId = 100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battle_ship);
		
		_btnIDs = new ArrayList<Integer>();
		
		this.SetButtons(5, 5);
		this.SetListView(5);
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
				_btnIDs.add(id);
				
				// IDをインクリメント
				id++;
			}
			
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
		list.setId(_listViewId);
		
		row.addView(list, param);
		layout.addView(row);
	}
	
	/**
	 * 選択されたテキストと同じ物を見つけてクリアする
	 * @param selectText
	 */
	private void ClearButtonText(String selectText){
	    // ボタンを一個ずつ見て、同じ文字が存在するか確認
	    for(int id : _btnIDs){
	        Button button = (Button)findViewById(id);
	        if(button.getText().toString().equals(selectText)){
	            // 同じ文字が見つかったらクリアする
	            button.setText("");
	            break;
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

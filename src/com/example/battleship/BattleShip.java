package com.example.battleship;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

public class BattleShip extends Activity {
	
	BattleshipClass mBattleship = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battle_ship);
		
		this.SetButtons(5, 5);
		this.SetListView(5);
		mBattleship = new BattleshipClass();
	}
	
	/**
	 * ボタンを動的に配置する
	 * @param x 横軸に配置するボタンの数
	 * @param y 縦軸に配置するボタンの数
	 */
	private void SetButtons(int x, int y){
		int id = 0;
		TableLayout layout = (TableLayout)findViewById(R.id.TableLayout1);
		
		for(int i = 0; i < x; i++){
			TableRow row = new TableRow(this);
			for(int j = 0; j < y; j++){
				// ボタンを動的に配置
				Button button = new Button(this);
				// IDは連番で振る
				button.setId(id);
				button.setText("");
				
				row.addView(button);
				
				// ボタンクリック時のイベントを設定
				button.setOnClickListener(new OnClickButton());
				
				// IDをインクリメント
				id++;
			}
			
			layout.addView(row);
		}
	}
	
	/**
	 * ログを表示するグリッドビューを配置する
	 * @param x 横軸に配置したボタンの数
	 */
	private void SetListView(int x){
		TableLayout layout = (TableLayout)findViewById(R.id.TableLayout1);
		TableRow row = new TableRow(this);
		
		LayoutParams param = new LayoutParams();
		param.span = x;
		
		row.addView(new ListView(this), param);
		layout.addView(row);
	}
	
	
	private class OnClickButton implements OnClickListener{

		@Override
		public void onClick(View v) {
			Button button = (Button)findViewById(v.getId());
			String buttonText = button.getText().toString();
			
			if(buttonText != null && !buttonText.isEmpty()){
				// アラートを表示して終了
				AlertDialog.Builder alert = new AlertDialog.Builder(BattleShip.this);
				alert.setTitle("エラー");
				alert.setMessage("すでに設定されています。");
				alert.setPositiveButton("OK", null);
				alert.show();
				return;
			}
			
			button.setText("");
		}
	}

}

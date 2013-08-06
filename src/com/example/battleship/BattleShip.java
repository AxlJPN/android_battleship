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
	 * �{�^���𓮓I�ɔz�u����
	 * @param x �����ɔz�u����{�^���̐�
	 * @param y �c���ɔz�u����{�^���̐�
	 */
	private void SetButtons(int x, int y){
		int id = 0;
		TableLayout layout = (TableLayout)findViewById(R.id.TableLayout1);
		
		for(int i = 0; i < x; i++){
			TableRow row = new TableRow(this);
			for(int j = 0; j < y; j++){
				// �{�^���𓮓I�ɔz�u
				Button button = new Button(this);
				// ID�͘A�ԂŐU��
				button.setId(id);
				button.setText("");
				
				row.addView(button);
				
				// �{�^���N���b�N���̃C�x���g��ݒ�
				button.setOnClickListener(new OnClickButton());
				
				// ID���C���N�������g
				id++;
			}
			
			layout.addView(row);
		}
	}
	
	/**
	 * ���O��\������O���b�h�r���[��z�u����
	 * @param x �����ɔz�u�����{�^���̐�
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
				// �A���[�g��\�����ďI��
				AlertDialog.Builder alert = new AlertDialog.Builder(BattleShip.this);
				alert.setTitle("�G���[");
				alert.setMessage("���łɐݒ肳��Ă��܂��B");
				alert.setPositiveButton("OK", null);
				alert.show();
				return;
			}
			
			button.setText("");
		}
	}

}

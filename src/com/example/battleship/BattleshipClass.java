package com.example.battleship;

import java.util.HashMap;
import java.util.Map;

public class BattleshipClass {
	
	private Map<ShipType, ShipParameter> ships = new HashMap<ShipType, BattleshipClass.ShipParameter>();
	
	public BattleshipClass(){
		
		// 戦艦を追加
		ShipParameter ship = new ShipParameter();
		ship.AttackPower = 1;
		ship.HitPoint = 3;
		ship.PositionX = 0;
		ship.PositionY = 0;
		ships.put(ShipType.BATTLESHIP, ship);
		
		//　駆逐艦を追加
		ship = new ShipParameter();
		ship.AttackPower = 1;
		ship.HitPoint = 2;
		ship.PositionX = 0;
		ship.PositionY = 0;
		ships.put(ShipType.DESTROYER, ship);
		
		// 潜水艦を追加
		ship = new ShipParameter();
		ship.AttackPower = 1;
		ship.HitPoint = 1;
		ship.PositionX = 0;
		ship.PositionY = 0;
		ships.put(ShipType.SUBMARINE, ship);
	}
	
	private class ShipParameter{
		public int HitPoint;	// 耐久力
		public int PositionX;	// 位置X
		public int PositionY;	// 位置Y
		public int AttackPower;	// 攻撃力
	}
	
	/**
	 * 位置を設定
	 * @param pointX
	 * @param pointY
	 * @param shipType
	 */
	public void SetPosition(int pointX, int pointY, ShipType shipType){
		ships.get(shipType).PositionX = pointX;
		ships.get(shipType).PositionY = pointY;
	}
	
	/**
	 * 攻撃
	 * @param pointX
	 * @param pointY
	 * @param shipType
	 */
	public void AttackEnemy(int pointX, int pointY, ShipType shipType){
		int pwr = ships.get(shipType).AttackPower;
		
		// TODO 通信先に投げる
		this.AttackRolls(pointX, pointY, pwr);
	}
	
	/**
	 * 移動
	 * @param pointX
	 * @param pointY
	 */
	public void Movement(int pointX, int pointY){
	}
	
	/**
	 * 攻撃判定
	 * @param pointX
	 * @param pointY
	 * @param attackPower
	 * @return
	 */
	public int AttackRolls(int pointX, int pointY, int attackPower){
		
		
		return 0;
	}
}

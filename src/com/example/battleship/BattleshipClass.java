package com.example.battleship;

import java.util.HashMap;
import java.util.Map;

public class BattleshipClass {
	
	private Map<ShipType, ShipParameter> ships = new HashMap<ShipType, BattleshipClass.ShipParameter>();
	
	public BattleshipClass(){
		mBattleship = new ShipParameter();
		mBattleship.AttackPower = 1;
		mBattleship.HitPoint = 3;
		mBattleship.PositionX = 0;
		mBattleship.PositionY = 0;
		mBattleship.Type = ShipType.BATTLESHIP;
		
		mDestroyer = new ShipParameter();
		mDestroyer.AttackPower = 1;
		mDestroyer.HitPoint = 2;
		mDestroyer.PositionX = 0;
		mDestroyer.PositionY = 0;
		mDestroyer.Type = ShipType.DESTROYER;
		
		mSubmarine = new ShipParameter();
		mSubmarine.AttackPower = 1;
		mSubmarine.HitPoint = 1;
		mSubmarine.PositionX = 0;
		mSubmarine.PositionY = 0;
		mSubmarine.Type = ShipType.SUBMARINE;
		
		ships.put(ShipType.BATTLESHIP, mBattleship);
		ships.put(ShipType.BATTLESHIP, mBattleship);
		ships.put(ShipType.BATTLESHIP, mBattleship);
	}
	
	private class ShipParameter{
		public int HitPoint;	// ÏvÍ
		public int PositionX;	// ÊuX
		public int PositionY;	// ÊuY
		public int AttackPower;	// UÍ
		public ShipType Type;	// DÌíÞ
	}
	
	public ShipParameter mBattleship = null;
	public ShipParameter mDestroyer = null;
	public ShipParameter mSubmarine = null;
	
	/**
	 * ÊuðÝè·é
	 * @param pointX
	 * @param pointY
	 * @param shipType
	 */
	public void SetPosition(int pointX, int pointY, ShipType shipType){
		ships.get(shipType).PositionX = pointX;
		ships.get(shipType).PositionY = pointY;
	}
	
	/**
	 * U
	 * @param pointX
	 * @param pointY
	 * @param shipType
	 */
	public void AttackEnemy(int pointX, int pointY, ShipType shipType){
		int pwr = ships.get(shipType).AttackPower;
		
		// TODO ÊMæÉn·
		this.AttackRolls(pointX, pointY, pwr);
	}
	
	/**
	 * Ú®
	 * @param pointX
	 * @param pointY
	 */
	public void Movement(int pointX, int pointY){
	}
	
	/**
	 * U»è
	 * @param pointX
	 * @param pointY
	 * @param attackPower
	 * @return
	 */
	public int AttackRolls(int pointX, int pointY, int attackPower){
		
		
		return 0;
	}
}

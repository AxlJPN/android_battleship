package com.example.battleship;

import java.util.HashMap;
import java.util.Map;

import android.widget.ListView;

import com.example.battleship.code.AttackResult;
import com.example.battleship.code.ShipType;

public class BattleshipClass {

    private Map<ShipType, ShipParameter> ships = new HashMap<ShipType, BattleshipClass.ShipParameter>();

    public BattleshipClass() {

        // 戦艦を追加
        ShipParameter ship = new ShipParameter();
        ship.AttackPower = 1;
        ship.HitPoint = 3;
        ship.PositionX = 0;
        ship.PositionY = 0;
        ships.put(ShipType.BATTLESHIP, ship);

        // 　駆逐艦を追加
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

    private class ShipParameter {
        public int HitPoint;    // 耐久力
        public int PositionX;   // 位置X
        public int PositionY;   // 位置Y
        public int AttackPower; // 攻撃力
    }

    /**
     * 位置を設定
     * 
     * @param pointX
     * @param pointY
     * @param shipType
     */
    public void SetPosition(int pointX, int pointY, ShipType shipType) {
        ships.get(shipType).PositionX = pointX;
        ships.get(shipType).PositionY = pointY;
    }

    /**
     * 攻撃
     * 
     * @param pointX
     * @param pointY
     * @param shipType
     */
    public void AttackEnemy(int pointX, int pointY, ShipType shipType) {
        int pwr = ships.get(shipType).AttackPower;

        // TODO 通信先に投げる
        this.AttackRolls(pointX, pointY, pwr);
    }

    /**
     * 移動
     * 
     * @param pointX
     * @param pointY
     * @param shipType
     * @return
     */
    public void Movement(int pointX, int pointY, ShipType shipType) {
        ships.get(shipType).PositionX = pointX;
        ships.get(shipType).PositionY = pointY;
        
        // TODO ListViewにログ表示
        String type = "";
        // 「XYへ【種類】が移動」 的な
        switch(shipType){
        case BATTLESHIP:
            type = "戦艦";
            break;
            
        case DESTROYER:
            type = "駆逐艦";
            break;
            
        case SUBMARINE:
            type = "潜水艦";
            break;
        }
        
//        String ret = type + "が(" + pointX + ", " + pointY + ")へ移動";
//        return ret;
    }

    /**
     * 攻撃判定
     * ※複数のターゲットの近くに当たった場合はどうするか
     * (下図の1～4、中心の●に当たった場合)
     * ● = ターゲット
     * ○ = 空き
     * ○12
     * ○●●
     * ○34
     * 
     * @param pointX
     * @param pointY
     * @param attackPower
     * @return
     */
    public AttackResult AttackRolls(int pointX, int pointY, int attackPower) {
        AttackResult ret = AttackResult.FAIL;
        
        for(ShipType type : ships.keySet()){
            int x = ships.get(type).PositionX;
            int y = ships.get(type).PositionY;
            
            if(x == pointX && y == pointY){
                // X軸、Y軸ともにどんぴしゃの場合
                // HPを減らす
                ships.get(type).HitPoint -= attackPower;
                
                // TODO ListViewにログを表示
                // 「命中！」など？

                if(ret != AttackResult.HIT)
                    ret = AttackResult.HIT;
            }
            else if(x+1 == pointX || x-1 == pointX || y+1 == pointY || y-1 == pointY){
                // X軸±1、Y軸±1の場合
                // TODO ListViewにログを表示
                // 「【種類】、波高し」、「【種類】、水しぶき」など
                
                if(ret == AttackResult.FAIL)
                    ret = AttackResult.NEAR;
            }
        }

        return ret;
    }
}

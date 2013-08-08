package com.example.battleship;

import java.util.HashMap;
import java.util.Map;

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
        ship.Sink = false;
        ships.put(ShipType.BATTLESHIP, ship);

        // 駆逐艦を追加
        ship = new ShipParameter();
        ship.AttackPower = 1;
        ship.HitPoint = 2;
        ship.PositionX = 0;
        ship.PositionY = 0;
        ship.Sink = false;
        ships.put(ShipType.DESTROYER, ship);

        // 潜水艦を追加
        ship = new ShipParameter();
        ship.AttackPower = 1;
        ship.HitPoint = 1;
        ship.PositionX = 0;
        ship.PositionY = 0;
        ship.Sink = false;
        ships.put(ShipType.SUBMARINE, ship);
    }

    public int GetPositionX(ShipType type) {
        return ships.get(type).PositionX;
    }

    public int GetPositionY(ShipType type) {
        return ships.get(type).PositionY;
    }

    private class ShipParameter {
        public int HitPoint; // 耐久力
        public int PositionX; // 位置X
        public int PositionY; // 位置Y
        public int AttackPower; // 攻撃力
        public boolean Sink; // 沈没フラグ
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
    public AttackResult AttackEnemy(int pointX, int pointY, ShipType shipType) {
        int pwr = ships.get(shipType).AttackPower;

        // TODO 通信先に投げ、その結果を返す
        // AttackResult result = this.AttackRolls(pointX, pointY, pwr);
        // result -> 結果？
        //
        return this.AttackRolls(pointX, pointY, pwr);
    }

    /**
     * 移動
     * 
     * @param pointX
     * @param pointY
     * @param shipType
     */
    public void Movement(int pointX, int pointY, ShipType shipType) {
        ships.get(shipType).PositionX = pointX;
        ships.get(shipType).PositionY = pointY;

        // TODO 通信先に投げる
        String sendMsg = pointX + "," + pointY;
    }

    /**
     * 攻撃判定 (下図の1～4、中心の●に当たった場合、すべての情報を送る) ● = ターゲット ○ = 空き ○12 ○●● ○34
     * 
     * @param pointX
     * @param pointY
     * @param attackPower
     * @return
     */
    public AttackResult AttackRolls(int pointX, int pointY, int attackPower) {
        AttackResult ret = AttackResult.FAIL;

        for (ShipType type : ships.keySet()) {
            int x = ships.get(type).PositionX;
            int y = ships.get(type).PositionY;

            if ((x == pointX && y == pointY) && !ships.get(type).Sink) {
                // X軸、Y軸ともにどんぴしゃの場合
                // HPを減らす
                ships.get(type).HitPoint -= attackPower;

                // TODO ListViewにログを表示
                // 「命中！」など？

                if (ret != AttackResult.HIT)
                    ret = AttackResult.HIT;
                if (IsSink(type)) {
                    // 沈没した場合
                    // TODO 通信先に「【種類】を撃沈！」を表示
                    // TODO 自分のログに「【種類】が沈没！」を表示
                    ships.get(type).Sink = true;
                }
            } else if ((x + 1 == pointX && y + 1 == pointY) || (x + 1 == pointX && y - 1 == pointY)
                    || (x - 1 == pointX && y + 1 == pointY) || (x - 1 == pointX && y - 1 == pointY)) {
                // X軸±1、Y軸±1の場合
                // TODO ListViewにログを表示
                // 「【種類】、波高し」、「【種類】、水しぶき」など

                if (ret == AttackResult.FAIL)
                    ret = AttackResult.NEAR;
            }
        }

        // TODO 通信先に投げる
        return ret;
    }

    /**
     * 攻撃結果、船が沈没したか判定
     * 
     * @param type
     * @return
     */
    private boolean IsSink(ShipType type) {
        int hitPoint = ships.get(type).HitPoint;
        return hitPoint == 0 ? true : false;
    }
}

package com.example.battleship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.example.battleship.code.AttackResult;
import com.example.battleship.code.ShipType;

public class BattleshipClass extends BattleShip {

    private Map<ShipType, ShipParameter> ships = new HashMap<ShipType, BattleshipClass.ShipParameter>();

    private Context context;
    public BattleshipClass(Context context) {

        this.context = context;

        // 戦艦を追加
        ShipParameter ship = new ShipParameter();
        ship.attackPower = 1;
        ship.hitPoint = 3;
        ship.positionX = 0;
        ship.positionY = 0;
        ship.sink = false;
        ships.put(ShipType.BATTLESHIP, ship);

        // 駆逐艦を追加
        ship = new ShipParameter();
        ship.attackPower = 1;
        ship.hitPoint = 2;
        ship.positionX = 0;
        ship.positionY = 0;
        ship.sink = false;
        ships.put(ShipType.DESTROYER, ship);

        // 潜水艦を追加
        ship = new ShipParameter();
        ship.attackPower = 1;
        ship.hitPoint = 1;
        ship.positionX = 0;
        ship.positionY = 0;
        ship.sink = false;
        ships.put(ShipType.SUBMARINE, ship);
    }

    public int GetPositionX(ShipType type) {
        return ships.get(type).positionX;
    }

    public int GetPositionY(ShipType type) {
        return ships.get(type).positionY;
    }

    public int GetPower(ShipType type) {
        return ships.get(type).attackPower;
    }

    private class ShipParameter {
        public int hitPoint; // 耐久力
        public int positionX; // 位置X
        public int positionY; // 位置Y
        public int attackPower; // 攻撃力
        public boolean sink; // 沈没フラグ
    }

    /**
     * 位置を設定
     *
     * @param pointX
     * @param pointY
     * @param shipType
     */
    public void SetPosition(int pointX, int pointY, ShipType shipType) {
        ships.get(shipType).positionX = pointX;
        ships.get(shipType).positionY = pointY;
    }

    /**
     * 攻撃
     *
     * @param pointX
     * @param pointY
     * @param shipType
     */
    public AttackResult AttackEnemy(int pointX, int pointY, ShipType shipType, View v, Context context) {
        int pwr = ships.get(shipType).attackPower;

        // TODO 通信先に投げ、その結果を返す
        // AttackResult result = this.AttackRolls(pointX, pointY, pwr);
        // result -> 結果？
        //
        return this.AttackRolls(pointX, pointY, pwr, v, context);
    }

    /**
     * 移動
     *
     * @param pointX
     * @param pointY
     * @param shipType
     */
    public void Movement(int pointX, int pointY, ShipType shipType) {
        ships.get(shipType).positionX = pointX;
        ships.get(shipType).positionY = pointY;
    }

    /**
     * 攻撃判定 (下図の1～4、中心の●に当たった場合、すべての情報を送る) ● = ターゲット ○ = 空き ○12 ○●● ○34
     *
     * @param pointX
     * @param pointY
     * @param attackPower
     * @return
     */
    public AttackResult AttackRolls(int pointX, int pointY, int attackPower, View v, Context context) {
        AttackResult ret = AttackResult.FAIL;
        String logText;

        for (ShipType type : ships.keySet()) {
            int x = ships.get(type).positionX;
            int y = ships.get(type).positionY;

            if ((x == pointX && y == pointY) && !ships.get(type).sink) {
                // X軸、Y軸ともにどんぴしゃの場合
                // HPを減らす
                ships.get(type).hitPoint -= attackPower;

                // TODO ListViewにログを表示
                // 「命中！」など？

                if (ret != AttackResult.HIT)
                    ret = AttackResult.HIT;
                if (IsSink(type)) {
                    // 沈没した場合
                    ships.get(type).sink = true;

                    // 自分のログ
                    logText = LogMsg.GetShipName(type) + "が沈没！";
                    LogMsg.AddLogMessage(logText);

                    // 通信先のログ
                    logText = LogMsg.GetShipName(type) + "を撃沈！";
                    // TODO 通信先に「【種類】を撃沈！」を表示
                }
            }
        }

        ArrayList<Button> buttons = GetAttackableButton(v.getId(), context);
        for (Button btn : buttons) {
            String btnText = btn.getText().toString();

            // ボタンに文字列が設定されていない場合、何もしない
            if (btnText.isEmpty())
                continue;

            if (ret == AttackResult.FAIL)
                ret = AttackResult.NEAR;

            // 「【種類】、波高し」、「【種類】、水しぶき」など
            logText = LogMsg.GetShipNameByShorName(btnText) + "、波高し";
            LogMsg.AddLogMessage(logText);
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
        int hitPoint = ships.get(type).hitPoint;
        return hitPoint == 0 ? true : false;
    }
}

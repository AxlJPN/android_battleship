package com.example.battleship;

import com.example.battleship.code.AttackResult;
import com.example.battleship.code.ShipType;

/**
 * ログを出力する
 * 
 * @author N.Wada
 * 
 */
public class LogMsg {

    /**
     * 攻撃時のログメッセージを生成する
     * 
     * @param pointX
     * @param pointY
     * @param result
     * @return
     */
    public static String MakeAttackLogText(int pointX, int pointY, ShipType type,
            AttackResult result) {
        String attackMsg = "";
        switch (result) {
        case HIT:
            attackMsg = "命中！";
            break;

        case NEAR:
            attackMsg = new LogMsg().GetShipName(type) + "、水しぶき";
            break;

        case FAIL:
            attackMsg = "攻撃命中せず";
            break;
        }
        return "【" + pointX + "," + pointY + "への攻撃】 " + attackMsg;
    }

    /**
     * 移動時のログメッセージを生成する
     * 
     * @param fromX
     *            移動元のX位置
     * @param fromY
     *            移動元のY位置
     * @param toX
     *            移動先のX位置
     * @param toY
     *            移動先のY位置
     * @param type
     *            船の種類
     * @return
     */
    public static String MakeMoveLogText(int fromX, int fromY, int toX, int toY, ShipType type) {
        String shipName = new LogMsg().GetShipName(type);

        int moveX = fromX - toX;
        int moveY = fromY - toY;
        String moveToMsg = "";
        if (moveX < 0) {
            // 右へ移動
            moveToMsg = "右へ" + Math.abs(moveX);
        } else if (moveX > 0) {
            // 左へ移動
            moveToMsg = "左へ" + Math.abs(moveX);
        } else if (moveY < 0) {
            // 下へ移動
            moveToMsg = "下へ" + Math.abs(moveY);
        } else if (moveY > 0) {
            // 上へ移動
            moveToMsg = "上へ" + Math.abs(moveY);
        }

        return shipName + "が" + moveToMsg + "マス移動";
    }

    /**
     * 船の名前を返す
     * 
     * @param type
     * @return
     */
    private String GetShipName(ShipType type) {
        switch (type) {
        case BATTLESHIP:
            return "戦艦";

        case DESTROYER:
            return "駆逐艦";

        case SUBMARINE:
            return "潜水艦";
        }

        return null;
    }

    /**
     * ログを表示する
     * 
     * @param logText
     */
    public static void AddLogMessage(String logText) {
        BattleShip._logAdapter.add(logText);
        BattleShip._logAdapter.notifyDataSetChanged();
    }
}

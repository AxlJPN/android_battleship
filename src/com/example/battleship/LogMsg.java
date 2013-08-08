package com.example.battleship;

import com.example.battleship.code.AttackResult;
import com.example.battleship.code.ShipType;

/**
 * ログを出力する
 * @author N.Wada
 *
 */
public class LogMsg {
    
    /**
     * 攻撃時のログメッセージを生成する
     * @param pointX
     * @param pointY
     * @param result
     * @return
     */
    public static String MakeAttackLogText(int pointX, int pointY, ShipType type, AttackResult result){
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
     * @param pointX
     * @param pointY
     * @param type
     * @return
     */
    public static String MakeMoveLogText(int pointX, int pointY, ShipType type){
        String shipName = new LogMsg().GetShipName(type);
        return shipName + "が(" + pointX + "," + pointY + ")へ移動";
    }
    
    /**
     * 船の名前を返す
     * @param type
     * @return
     */
    private String GetShipName(ShipType type){
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
     * @param logText
     */
    public static void AddLogMessage(String logText){
        BattleShip._logAdapter.add(logText);
        BattleShip._logAdapter.notifyDataSetChanged();
    }
}

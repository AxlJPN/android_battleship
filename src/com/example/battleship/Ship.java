package com.example.battleship;

import com.example.battleship.code.ShipType;

/**
 * 船クラス
 * 
 * @author T.Iga
 * 
 */
public class Ship {

    /**
     * 船の種類
     */
    private ShipType shipType;
    /**
     * 船の名前
     */
    private String shipName;
    /**
     * 船の短い名前
     */
    private String shortShipName;

    /**
     * デフォルトコンストラクタ
     */
    public Ship() {

    }

    /**
     * コンストラクタ
     * 
     * @param shipType
     *            船の種類
     * @param shipName
     *            船の名前
     */
    public Ship(ShipType shipType, String shipName) {
        this.shipType = shipType;
        this.shipName = shipName;
    }

    /**
     * コンストラクタ
     * 
     * @param shipType
     *            船の種類
     * @param shipName
     *            船の名前
     * @param shortShipName
     *            船の短い名前
     */
    public Ship(ShipType shipType, String shipName, String shortShipName) {
        this.shipType = shipType;
        this.shipName = shipName;
        this.shortShipName = shortShipName;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getShortShipName() {
        return this.shortShipName;
    }

    public void setShortShipName(String shortShipName) {
        this.shortShipName = shortShipName;
    }
}

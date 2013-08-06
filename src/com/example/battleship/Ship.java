package com.example.battleship;

import com.example.battleship.code.ShipType;

public class Ship {

    private ShipType shipType;
    private String shipName;
    private String shortShipName;

    public Ship(){
        
    }
    
    public Ship(ShipType shipType, String shipName){
        this.shipType = shipType;
        this.shipName = shipName;
    }
    public Ship(ShipType shipType, String shipeName, String shortShipName) {
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

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
}

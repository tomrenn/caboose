package com.tomrenn.njtrains.caboose.transit;

import javax.annotation.Nullable;

/**
 *
 */
public class TransitData {
    TransitItem rail;
    TransitItem bus;

    public static TransitData createRailOnly(TransitItem rail){
        TransitItem bus = new TransitItem(0, "", "");
        return new TransitData(rail, bus);
    }

    public TransitData(TransitItem rail, TransitItem bus) {
        this.rail = rail;
        this.bus = bus;
    }

    public TransitItem getRail() {
        return rail;
    }

    public TransitItem getBus() {
        return bus;
    }

    public boolean isNewer(@Nullable TransitData transitData){
        if (transitData == null){
            return true;
        }
        boolean isBusNewer = bus != null && bus.isNewer(transitData.bus);
        boolean isRailNewer = rail != null && rail.isNewer(transitData.rail);
        return isBusNewer || isRailNewer;
    }

    @Override
    public String toString() {
        return "TransitData{" +
                "rail=" + rail +
                ", bus=" + bus +
                '}';
    }
}

package com.tomrenn.njtrains.caboose;

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

    private TransitData(TransitItem rail, TransitItem bus) {
        this.rail = rail;
        this.bus = bus;
    }

    public TransitItem getRail() {
        return rail;
    }

    public TransitItem getBus() {
        return bus;
    }

    public static class TransitItem {
        long lastUpdated;
        String checksum;
        String url;

        public TransitItem(long lastUpdated, String checksum, String url) {
            this.lastUpdated = lastUpdated;
            this.checksum = checksum;
            this.url = url;
        }

        public long getLastUpdated() {
            return lastUpdated;
        }

        public String getChecksum() {
            return checksum;
        }

        public String getUrl() {
            return url;
        }
    }
}

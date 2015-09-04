package com.tomrenn.njtrains.caboose;

/**
 *
 */
public class TransitData {
    LatestZip rail;
    LatestZip bus;

    public static TransitData createRailOnly(LatestZip rail){
        LatestZip bus = new LatestZip("", "", "");
        return new TransitData(rail, bus);
    }

    private TransitData(LatestZip rail, LatestZip bus) {
        this.rail = rail;
        this.bus = bus;
    }

    public LatestZip getRail() {
        return rail;
    }

    public LatestZip getBus() {
        return bus;
    }

    public static class LatestZip {
        String lastUpdated;
        String checksum;
        String url;

        public LatestZip(String lastUpdated, String checksum, String url) {
            this.lastUpdated = lastUpdated;
            this.checksum = checksum;
            this.url = url;
        }

        public String getLastUpdated() {
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

package com.tomrenn.njtrains.caboose.transit;

public class TransitItem {
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

    public boolean isNewer(TransitItem transitItem){
        return transitItem == null || this.lastUpdated > transitItem.lastUpdated;
    }

    @Override
    public String toString() {
        return "TransitItem{" +
                "checksum='" + checksum + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

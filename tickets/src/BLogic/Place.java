package BLogic;

import DataLayer.DBProcessor;

public class Place {
    private String sector;
    private int placeNum;
    private int placeID;

    public Place(int placeID, String sector, int placeNum) {
        this.sector = sector;
        this.placeNum = placeNum;
        this.placeID = placeID;
    }

    public Place(String sector, int placeNum) {
        this.sector = sector;
        this.placeNum = placeNum;
    }

    public Place(int placeID) {
        this.placeID = placeID;
    }

    public String getPlaceString () {
        return new String(sector + " " + placeNum);
    }

    public int getPlaceID() {
        return placeID;
    }

    public String getSector() {
        return sector;
    }

    public int getPlaceNum() {
        return placeNum;
    }

    public void setPlaceID(int placeID) {
        this.placeID = placeID;
    }

    public boolean updatePlace (Stadium stadium) {
        return DBProcessor.editPlace(this, stadium);
    }

    public void deletePlace () {
        DBProcessor.deletePlace(this);
    }
}

package BLogic;

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

    public String getPlaceString () {
        return new String(sector + " " + placeNum);
    }

    public int getPlaceID() {
        return placeID;
    }
}

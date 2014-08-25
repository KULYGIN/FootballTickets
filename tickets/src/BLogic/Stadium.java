package BLogic;

import java.util.ArrayList;

public class Stadium {
    private int stadiumID;
    private String stadiumName;
    private String aboutStadium;
    private ArrayList <Place> places = new ArrayList<Place>();
    private String picName;

    public Stadium(int stadiumID, String stadiumName, String aboutStadium, String picName) {
        this.stadiumID = stadiumID;
        this.stadiumName = stadiumName;
        this.aboutStadium = aboutStadium;
        this.picName = picName;
    }

    public Stadium(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public String getPicName() {
        return picName;
    }

    public int getStadiumID() {
        return stadiumID;
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }
}

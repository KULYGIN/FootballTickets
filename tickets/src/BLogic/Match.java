package BLogic;

import DataLayer.DBProcessor;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Match {
    private int matchID;
    private String matchName;
    private String command1;
    private String command2;
    private Timestamp dateTime;
    private Stadium stadium;
    private ArrayList<Tickets> tickets;

    public Match(int matchID, String matchName, String command1,
                 String command2, Timestamp dateTime, Stadium stadium) {
        this.matchID = matchID;
        this.matchName = matchName;
        this.command1 = command1;
        this.command2 = command2;
        this.dateTime = dateTime;
        this.stadium = stadium;
    }

    public Match(String matchName, String command1, String command2,
                 Timestamp dateTime, Stadium stadium) {
        this.matchName = matchName;
        this.command1 = command1;
        this.command2 = command2;
        this.dateTime = dateTime;
        this.stadium = stadium;
    }

    public Match(int matchID) {
        this.matchID = matchID;
    }

    public String getMatchString () {
        String line =  new String(matchName + ". " + command1 + "/" + command2 +". " + stadium.getStadiumName() + ". " + dateTime);
        return line.substring(0, line.length() - 5); //Что бы избавиться от наносекунд
    }

    public int getMatchID() {
        return matchID;
    }

    public String getPicName() {
        return stadium.getPicName();
    }

    public int getStadiumID() {
        return stadium.getStadiumID();
    }

    public void setTickets(ArrayList<Tickets> tickets) {
        this.tickets = tickets;
    }

    public ArrayList<Tickets> getTickets() {
        return tickets;
    }

    public String getMatchName() {
        return matchName;
    }

    public String getCommand1() {
        return command1;
    }

    public String getCommand2() {
        return command2;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public boolean updateMatch () {
        return DBProcessor.editMatch(this, false);
    }

    public void deleteMatch() {
        DBProcessor.deleteMatch(this);
    }

    public boolean addNewTicket (Place place, int cost) {
        return DBProcessor.addNewTicket(this, place, cost);
    }

    public void setStadium(Stadium stadium) {
        this.stadium = stadium;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public void setCommand1(String command1) {
        this.command1 = command1;
    }

    public void setCommand2(String command2) {
        this.command2 = command2;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }
}

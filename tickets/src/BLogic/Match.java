package BLogic;

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

    public String getMatchString () {
        String line =  new String(matchName + ". " + command1 + "/" + command2 +". " + stadium.getStadiumName() + ". " + dateTime);
        return line.substring(0,line.length() - 4); //Что бы избавиться от наносекунд
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
}

package ServiceLayer;

import BLogic.Match;
import BLogic.Tickets;
import DataLayer.DBProcessor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;


public class JSONBuilder {
    private static JSONObject mainObj = new JSONObject();
    public static JSONObject prepareAndGetJSON (int matchID) {
        Match match = new Match(matchID);
        match = DBProcessor.getStadiumPlace(match);
        ArrayList <Tickets> tickets = match.getTickets();

        JSONArray matchLine = new JSONArray();
        matchLine.add(match.getMatchID());
        matchLine.add(match.getMatchName());
        matchLine.add(match.getCommand1());
        matchLine.add(match.getCommand2());
        matchLine.add(match.getDateTime());
        matchLine.add(match.getStadiumID());


        for (Tickets ticket: tickets) {
            JSONObject ticketsObjects = new JSONObject();
            ticketsObjects.put("ticket_id", ticket.getTicketId());
            ticketsObjects.put("place_id", ticket.getPlace().getPlaceID());
            ticketsObjects.put("ticket_status", ticket.getStatus());
            ticketsObjects.put("ticket_cost", ticket.getTicketCost());

            JSONObject matchTicketsLine = new JSONObject();
            matchTicketsLine.put("ticket", ticketsObjects);
            matchLine.add(matchTicketsLine);
        }
        mainObj.put("match", matchLine);
        return  mainObj;
    }
}

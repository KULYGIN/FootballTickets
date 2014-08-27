package BLogic;

import DataLayer.DBProcessor;

public class Tickets {
    private Place place;
    private int ticketCost;
    private int status;
    private Match match;
    private int ticketId;

    public Tickets(Place place, int ticketCost, int status, Match match, int ticketId) {
        this.place = place;
        this.ticketCost = ticketCost;
        this.status = status;
        this.match = match;
        this.ticketId = ticketId;
    }

    public Tickets(Place place, int ticketCost, int status, int ticketId) {
        this.place = place;
        this.ticketCost = ticketCost;
        this.status = status;
        this.ticketId = ticketId;
    }

    public String getTicketString () {
        String ticketStatus = "";
        switch (status) {
            case 1:
            {
                ticketStatus = "Забронирован";
                break;
            }
            case 2:
            {
                ticketStatus = "Куплен";
                break;
            }
            case 0:
            {
                ticketStatus = "Свобобдный";
                break;
            }
        }
        return new String(match.getMatchString() + " " + place.getPlaceString() +
                " Стоимость: " + ticketCost + " " + ticketStatus);
    }

    public int getStatus() {
        return status;
    }

    public int getTicketId() {
        return ticketId;
    }

    public Place getPlace() {
        return place;
    }

    public Match getMatch() {
        return match;
    }

    public int getTicketCost() {
        return ticketCost;
    }

    public void setTicketCost(int ticketCost) {
        this.ticketCost = ticketCost;
    }

    public void updateTicketCost () {
        DBProcessor.editTicket(this);
    }

    public void deleteTicket() {
        DBProcessor.deleteTicket(this);
    }
}

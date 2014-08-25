package BLogic;

import DataLayer.DBProcessor;

import java.util.ArrayList;

public class User {
    private String login;
    private String password;
    private boolean isAdmin;
    private int userID;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User addNewUser () {
        return DBProcessor.addNewUser(this);
    }

    public User loginUser () {
        return DBProcessor.loginUser(this);
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void saveBuyTicket (Tickets ticket, int status) {
      DBProcessor.saveBuyTicket (this, ticket, status);
    }

    public ArrayList<Tickets> getUserTickets () {
       return  DBProcessor.getUserTickets(this);
    }

    public void buyTicket(Tickets ticket) {
        DBProcessor.changeTicketStatus(ticket, 2);
    }

    public void returnTicket(Tickets ticket) {
        DBProcessor.returnTicket(ticket);
    }
}

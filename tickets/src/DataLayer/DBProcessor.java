package DataLayer;

import BLogic.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class DBProcessor {
    private static String serverAdres = "127.0.0.1:5432";
    private static String DBname = "football";
    private static String clientName = "postgres";
    private static String password = "master";
    private static Connection connection;

    public static Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return null;
        }
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://" + serverAdres + "/" + DBname, clientName, password);
        } catch (SQLException e) {
            System.err.println("Connection Failed! Check output console");
            e.printStackTrace();
            return null;
        }
        if (connection == null) {
            System.err.println("Failed to make connection!");
        }
        return connection;
    }

    public static User addNewUser(User user) {
        if (checkUser(user.getLogin())) {
            return null;
        }
        try {
            PreparedStatement pr  = connection.prepareStatement("INSERT INTO \"user\" (login, password) VALUES (?,?)\n" +
                    "RETURNING user_id");
            pr.setString(1, user.getLogin());
            pr.setString(2, user.getPassword());
            ResultSet rs = pr.executeQuery();
            if (!rs.next()) {
                 return null;
            }
            user.setUserID(rs.getInt(1));
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static boolean checkUser(String userName) {
        boolean exists = false;
        try {
            PreparedStatement pr  = connection.prepareStatement("SELECT login FROM \"user\" WHERE login = ?");
            pr.setString(1, userName);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                exists = true;
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    public static User loginUser (User user) {
        try {
            PreparedStatement pr  = connection.prepareStatement("SELECT is_admin, user_id FROM \"user\" WHERE login = ? AND password = ?");
            pr.setString(1, user.getLogin());
            pr.setString(2, user.getPassword());
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                user.setAdmin(rs.getBoolean(1));
                user.setUserID(rs.getInt(2));
            } else {
                user = null;
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static ArrayList<Match> getMatchAfterTime (Timestamp nowTime, int searchType, String stringToSearch, Timestamp thisTime) {
        ArrayList<Match> availableMatch = new ArrayList<Match>();
        try {
            String sql = ("SELECT match.match_id, match.match_name, match.command1, " +
                    "match.command2, match.datetime, match.stadium_id, stadium.stadium_name, stadium.about, stadium.pic_name \n" +
                    "FROM match\n" +
                    "JOIN stadium ON (match.stadium_id = stadium.stadium_id)\n" +
                    "WHERE datetime >=  ?\n");
            PreparedStatement pr  = null;
            switch (searchType) {
                case 1: {
                    sql += "AND to_char (match.datetime, 'yyyy') = ? AND to_char (match.datetime, 'mm') = ? AND to_char (match.datetime, 'dd') = ?";
                    pr  = connection.prepareStatement(sql);
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(thisTime.getTime());
                    pr.setString(2, String.valueOf(cal.get(Calendar.YEAR)));
                    pr.setString(3, String.valueOf(cal.get(Calendar.MONTH) + 1));
                    pr.setString(4, String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                    break;
                }
                case 2: {
                    sql += "AND (UPPER(match.match_name) LIKE UPPER(?) OR UPPER(match.command1) LIKE UPPER(?) OR " +
                            "UPPER(match.command1) LIKE UPPER(?) OR " +
                            "UPPER(stadium.stadium_name) LIKE UPPER(?))";
                    pr  = connection.prepareStatement(sql);
                    pr.setString(2, "%" + stringToSearch + "%");
                    pr.setString(3, "%" + stringToSearch + "%");
                    pr.setString(4, "%" + stringToSearch + "%");
                    pr.setString(5, "%" + stringToSearch + "%");
                    break;
                }
                default: {
                    pr  = connection.prepareStatement(sql);
                    break;
                }
            }
            pr.setTimestamp(1, nowTime);
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                int match_id = rs.getInt(1);
                String match_name = rs.getString(2);
                String command1 = rs.getString(3);
                String command2 = rs.getString(4);
                Timestamp dateTime = rs.getTimestamp(5);
                int stadium_id = rs.getInt(6);
                String stadium_name = rs.getString(7);
                String about = rs.getString(8);
                String pic_name = rs.getString(9);

                Stadium stadium = new Stadium(stadium_id, stadium_name, about, pic_name);

                Match match = new Match(match_id, match_name, command1, command2, dateTime, stadium);
                availableMatch.add(match);
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableMatch;
    }

    public static Match getStadiumPlace (Match match) {
        ArrayList<Tickets> tickets = new ArrayList<Tickets>();
        try {
            PreparedStatement pr  = connection.prepareStatement("SELECT place.place_id, place.sector, place.place_num,\n" +
                    "tickets.cost, tickets.ticket_id, tickets.status\n" +
                    "FROM match_tickets\n" +
                    "JOIN match ON (match.match_id = match_tickets.match_id)\n" +
                    "JOIN tickets ON (tickets.ticket_id = match_tickets.ticket_id)\n" +
                    "JOIN place ON (place.place_id = tickets.place_id)\n" +
                    "WHERE match.match_id =  ? AND tickets.status = 0");
            pr.setInt(1, match.getMatchID());
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                int place_id = rs.getInt(1);
                String sector = rs.getString(2);
                int place_num = rs.getInt(3);
                int cost = rs.getInt(4);
                int ticket_id = rs.getInt(5);
                int status = rs.getInt(6);
                Place place = new Place(place_id, sector, place_num);
                Tickets ticket = new Tickets(place, cost, status, match, ticket_id);
                tickets.add(ticket);
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        match.setTickets(tickets);
        return match;
    }


    public static ArrayList<Tickets> getUserTickets (User user) {
        ArrayList<Tickets> tickets = new ArrayList<Tickets>();
        try {
            PreparedStatement pr  = connection.prepareStatement("SELECT match.match_name, match.command1, " +
                    "match.command2, match.datetime, stadium.stadium_name, \n" +
                    "place.sector, place.place_num, tickets.ticket_id, tickets.cost, tickets.status\n" +
                    "FROM match\n" +
                    "JOIN match_tickets ON (match.match_id = match_tickets.match_id)\n" +
                    "JOIN user_ticket ON (match_tickets.ticket_id = user_ticket.ticket_id)\n" +
                    "JOIN \"user\" ON (\"user\".user_id = user_ticket.user_id)\n" +
                    "JOIN tickets ON (user_ticket.ticket_id = tickets.ticket_id)\n" +
                    "JOIN place ON (place.place_id = tickets.place_id)\n" +
                    "JOIN stadium ON (stadium.stadium_id = match.stadium_id )\n" +
                    "WHERE \"user\".user_id = ?\n" +
                    "ORDER BY tickets.status");
            pr.setInt(1, user.getUserID());
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                String match_name = rs.getString(1);
                String command1 = rs.getString(2);
                String command2 = rs.getString(3);
                Timestamp datetime = rs.getTimestamp(4);
                String stadium_name = rs.getString(5);
                String sector = rs.getString(6);
                int place_num = rs.getInt(7);
                int ticket_id = rs.getInt(8);
                int cost = rs.getInt(9);
                int status = rs.getInt(10);

                Stadium stadium = new Stadium(stadium_name);
                Match match = new Match(match_name, command1, command2, datetime, stadium);
                Place place = new Place(sector, place_num);
                Tickets ticket = new Tickets(place, cost, status, match, ticket_id);
                tickets.add(ticket);
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
         return tickets;
    }


    public static void saveBuyTicket (User user, Tickets ticket, int status) {
        try {
            PreparedStatement pr  = connection.prepareStatement("INSERT INTO user_ticket (user_id, ticket_id)\n" +
                    "VALUES (?, ?)");
            pr.setInt(1, user.getUserID());
            pr.setInt(2, ticket.getTicketId());
            pr.execute();
            pr.close();
            changeTicketStatus(ticket, status);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void changeTicketStatus(Tickets ticket, int newStatus) {
        try {
            PreparedStatement pr  = connection.prepareStatement("UPDATE tickets set status = ? \n" +
                    "WHERE ticket_id = ?");
            pr.setInt(1, newStatus);
            pr.setInt(2, ticket.getTicketId());
            pr.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addTicketToMatch (int ticketID, Match match, User user) {
        try {
            PreparedStatement pr  = connection.prepareStatement("INSERT INTO match_tickets (match_id, ticket_id)\n" +
                    "VALUES (?, ?)\n" +
                    "RETURNING match_tickets_id");
            pr.setInt(1, match.getMatchID());
            pr.setInt(2, ticketID);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                if (!addTicketToUser (ticketID, user)) {
                    return false;
                }
            } else {
                return false;
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean addTicketToUser (int ticketID, User user) {
        try {
            PreparedStatement pr  = connection.prepareStatement("INSERT INTO user_ticket (user_id, ticket_id)\n" +
                    "VALUES (?, ?)\n" +
                    "RETURNING user_ticket_id");
            pr.setInt(1,user.getUserID());
            pr.setInt(2, ticketID);
            ResultSet rs = pr.executeQuery();
            if (!rs.next()) {
                return false;
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void returnTicket (Tickets ticket) {
        try {
            PreparedStatement pr  = connection.prepareStatement("DELETE FROM user_ticket\n" +
                    "WHERE user_ticket.ticket_id = ?");
            pr.setInt(1, ticket.getTicketId());
            pr.executeQuery();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        changeTicketStatus(ticket, 0);
    }
}
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
            PreparedStatement pr = connection.prepareStatement("INSERT INTO \"user\" (login, password) VALUES (?,?)\n" +
                    "RETURNING user_id");
            pr.setString(1, user.getLogin());
            pr.setString(2, user.getPassword());
            ResultSet rs = pr.executeQuery();
            if (!rs.next()) {
                return null;
            }
            user.setUserID(rs.getInt(1));
            user.setAdmin(false);
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
            PreparedStatement pr = connection.prepareStatement("SELECT login FROM \"user\" WHERE login = ?");
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

    public static User loginUser(User user) {
        try {
            PreparedStatement pr = connection.prepareStatement("SELECT is_admin, user_id FROM \"user\" WHERE login = ? AND password = ?");
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

    public static ArrayList<Match> getMatchAfterTime(Timestamp nowTime, int searchType, String stringToSearch, Timestamp thisTime) {
        ArrayList<Match> availableMatch = new ArrayList<Match>();
        try {
            String sql = ("SELECT match.match_id, match.match_name, match.command1, " +
                    "match.command2, match.datetime, match.stadium_id, stadium.stadium_name, stadium.about, stadium.pic_name \n" +
                    "FROM match\n" +
                    "JOIN stadium ON (match.stadium_id = stadium.stadium_id)\n" +
                    "WHERE datetime >=  ?\n");
            PreparedStatement pr = null;
            switch (searchType) {
                case 1: {
                    sql += "AND to_char (match.datetime, 'yyyy') = ? AND to_char (match.datetime, 'mm') = ? AND to_char (match.datetime, 'dd') = ?";
                    pr = connection.prepareStatement(sql);
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
                    pr = connection.prepareStatement(sql);
                    pr.setString(2, "%" + stringToSearch + "%");
                    pr.setString(3, "%" + stringToSearch + "%");
                    pr.setString(4, "%" + stringToSearch + "%");
                    pr.setString(5, "%" + stringToSearch + "%");
                    break;
                }
                default: {
                    pr = connection.prepareStatement(sql);
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

    public static Match getStadiumPlace(Match match) {
        ArrayList<Tickets> tickets = new ArrayList<Tickets>();
        int stadiumId = 0;
        String match_name = "";
        String command1 = "";
        String command2 = "";
        Timestamp datetime = null;
        try {
            PreparedStatement pr = connection.prepareStatement("SELECT place.place_id, place.sector, place.place_num,\n" +
                    "tickets.cost, tickets.ticket_id, tickets.status, match.stadium_id, match.match_name,\n" +
                    "match.command1, match.command2, match.datetime\n" +
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
                stadiumId = rs.getInt(7);
                match_name = rs.getString(8);
                command1 = rs.getString(9);
                command2 = rs.getString(10);
                datetime = rs.getTimestamp(11);
                tickets.add(ticket);
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        match.setTickets(tickets);
        Stadium stadium = new Stadium(stadiumId);
        match.setStadium(stadium);
        match.setCommand1(command1);
        match.setCommand2(command2);
        match.setDateTime(datetime);
        match.setMatchName(match_name);
        return match;
    }


    public static ArrayList<Tickets> getUserTickets(User user) {
        ArrayList<Tickets> tickets = new ArrayList<Tickets>();
        try {
            PreparedStatement pr = connection.prepareStatement("SELECT match.match_name, match.command1, " +
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


    public static void saveBuyTicket(User user, Tickets ticket, int status) {
        try {
            PreparedStatement pr = connection.prepareStatement("INSERT INTO user_ticket (user_id, ticket_id)\n" +
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
            PreparedStatement pr = connection.prepareStatement("UPDATE tickets set status = ? \n" +
                    "WHERE ticket_id = ?");
            pr.setInt(1, newStatus);
            pr.setInt(2, ticket.getTicketId());
            pr.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addTicketToMatch(int ticketID, Match match, User user) {
        try {
            PreparedStatement pr = connection.prepareStatement("INSERT INTO match_tickets (match_id, ticket_id)\n" +
                    "VALUES (?, ?)\n" +
                    "RETURNING match_tickets_id");
            pr.setInt(1, match.getMatchID());
            pr.setInt(2, ticketID);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                if (!addTicketToUser(ticketID, user)) {
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

    public static boolean addTicketToUser(int ticketID, User user) {
        try {
            PreparedStatement pr = connection.prepareStatement("INSERT INTO user_ticket (user_id, ticket_id)\n" +
                    "VALUES (?, ?)\n" +
                    "RETURNING user_ticket_id");
            pr.setInt(1, user.getUserID());
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

    public static void returnTicket(Tickets ticket) {
        try {
            PreparedStatement pr = connection.prepareStatement("DELETE FROM user_ticket\n" +
                    "WHERE user_ticket.ticket_id = ?");
            pr.setInt(1, ticket.getTicketId());
            pr.executeQuery();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        changeTicketStatus(ticket, 0);
    }

    public static ArrayList<Stadium> getStadiums() {
        ArrayList<Stadium> stadiums = new ArrayList<Stadium>();
        try {
            PreparedStatement pr = connection.prepareStatement("" +
                    "SELECT stadium_id, stadium_name, about, pic_name FROM stadium");
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                int stadium_id = rs.getInt(1);
                String stadium_name = rs.getString(2);
                String about = rs.getString(3);
                String pic_name = rs.getString(4);
                Stadium stadium = new Stadium(stadium_id, stadium_name, about, pic_name);
                stadiums.add(stadium);
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stadiums;
    }

    public static Stadium getStadiumPlaces(Stadium stadium) {
        ArrayList<Place> places = new ArrayList<Place>();
        try {
            PreparedStatement pr = connection.prepareStatement("" +
                    "SELECT place.place_id, place.sector, place.place_num\n" +
                    "FROM place\n" +
                    "JOIN stadium_place ON (stadium_place.place_id = place.place_id)\n" +
                    "WHERE stadium_place.stadium_id = ?");
            pr.setInt(1, stadium.getStadiumID());
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                int place_id = rs.getInt(1);
                String sector = rs.getString(2);
                int place_num = rs.getInt(3);
                Place place = new Place(place_id, sector, place_num);
                places.add(place);
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        stadium.setPlaces(places);
        return stadium;
    }

    public static boolean addNewStadium(Stadium stadium) {
        if (checkStadiumName(stadium, true)) {
            return false;
        }
        try {
            PreparedStatement pr = connection.prepareStatement("INSERT INTO stadium (stadium_name, about, pic_name)\n" +
                    "VALUES (?, ?, ?)");
            pr.setString(1, stadium.getStadiumName());
            pr.setString(2, stadium.getAboutStadium());
            pr.setString(3, stadium.getPicName());
            pr.execute();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean checkStadiumName(Stadium stadium, boolean isNew) {
        boolean res = false;
        try {
            PreparedStatement pr = connection.prepareStatement("" +
                    "SELECT stadium_id FROM stadium\n" +
                    "WHERE UPPER(stadium_name) = UPPER(?)");
            pr.setString(1, stadium.getStadiumName());
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                if (!isNew) {
                    int newStadiumID = rs.getInt(1);
                    if (newStadiumID != stadium.getStadiumID()) {
                        res = true;
                    }
                } else {
                    res = true;
                }
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void deleteStadium(Stadium stadium) {
        try {
            PreparedStatement pr = connection.prepareStatement("DELETE FROM stadium\n" +
                    "WHERE stadium_id = ?");
            pr.setInt(1, stadium.getStadiumID());
            pr.execute();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addNewStadiumPlace(Stadium stadium, Place place) {
        if (checkPlaceName(stadium, place)) {
            return false;
        }
        try {
            PreparedStatement pr = connection.prepareStatement("INSERT INTO place (sector, place_num)\n" +
                    "VALUES (?, ?)\n" +
                    "RETURNING place_id");
            pr.setString(1, place.getSector());
            pr.setInt(2, place.getPlaceNum());
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                place.setPlaceID(rs.getInt(1));
                addNewStadiumPlaceTable(stadium, place);
            }
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean checkPlaceName(Stadium stadium, Place place) {
        boolean res = false;
        try {
            PreparedStatement pr = connection.prepareStatement("" +
                    "SELECT place.place_id FROM place\n" +
                    "JOIN stadium_place ON (place.place_id = stadium_place.place_id)\n" +
                    "WHERE UPPER(place.sector) = UPPER(?) AND place.place_num = ? AND stadium_place.stadium_id = ?");
            pr.setString(1, place.getSector());
            pr.setInt(2, place.getPlaceNum());
            pr.setInt(3, stadium.getStadiumID());
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                res = true;
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static boolean addNewStadiumPlaceTable(Stadium stadium, Place place) {
        try {
            PreparedStatement pr = connection.prepareStatement("INSERT INTO stadium_place (stadium_id, place_id)\n" +
                    "VALUES (?, ?)");
            pr.setInt(1, stadium.getStadiumID());
            pr.setInt(2, place.getPlaceID());
            pr.execute();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean editPlace(Place place, Stadium stadium) {
        if (checkPlaceName(stadium, place)) {
            return false;
        }
        try {
            PreparedStatement pr = connection.prepareStatement("UPDATE place set sector = ?, place_num = ? \n" +
                    "WHERE place_id = ?");
            pr.setString(1, place.getSector());
            pr.setInt(2, place.getPlaceNum());
            pr.setInt(3, place.getPlaceID());
            pr.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void deletePlace(Place place) {
        try {
            PreparedStatement pr = connection.prepareStatement("DELETE FROM place\n" +
                    "WHERE place_id = ?");
            pr.setInt(1, place.getPlaceID());
            pr.execute();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean editStadium(Stadium stadium, boolean isNew) {
        if (checkStadiumName(stadium, isNew)) {
            return false;
        }
        try {
            PreparedStatement pr = connection.prepareStatement("UPDATE stadium set stadium_name = ?, pic_name = ?, about = ? \n" +
                    "WHERE stadium_id = ?");
            pr.setString(1, stadium.getStadiumName());
            pr.setString(2, stadium.getPicName());
            pr.setString(3, stadium.getAboutStadium());
            pr.setInt(4, stadium.getStadiumID());
            pr.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static ArrayList<Match> getStadiumMatches(Stadium stadium) {
        ArrayList<Match> matches = new ArrayList<Match>();
        try {
            PreparedStatement pr = connection.prepareStatement("" +
                    "SELECT match.match_id, match.match_name, match.command1, match.command2, match.datetime  FROM match\n" +
                    "where match.stadium_id = ?");
            pr.setInt(1, stadium.getStadiumID());
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                int match_id = rs.getInt(1);
                String match_name = rs.getString(2);
                String command1 = rs.getString(3);
                String command2 = rs.getString(4);
                Timestamp datetime = rs.getTimestamp(5);
                Match match = new Match(match_id, match_name, command1, command2, datetime, stadium);
                matches.add(match);
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    public static boolean editMatch(Match match, boolean isNew) {
        if (checkMatchName(match, isNew)) {
            return false;
        }
        try {
            PreparedStatement pr = connection.prepareStatement("UPDATE match set match_name = ?, command1 = ?, command2 = ?, \n" +
                    "datetime = ?\n" +
                    "WHERE match_id = ?");
            pr.setString(1, match.getMatchName());
            pr.setString(2, match.getCommand1());
            pr.setString(3, match.getCommand2());
            pr.setTimestamp(4, match.getDateTime());
            pr.setInt(5, match.getMatchID());
            pr.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean checkMatchName(Match match, boolean isNew) {
        boolean res = false;
        try {
            PreparedStatement pr = connection.prepareStatement("" +
                    "SELECT match_id FROM match\n" +
                    "WHERE UPPER(match_name) = UPPER(?)");
            pr.setString(1, match.getMatchName());
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                if (!isNew) {
                    int newMatchID = rs.getInt(1);
                    if (newMatchID != match.getMatchID()) {
                        res = true;
                    }
                }else {
                    res = true;
                }
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static boolean addNewMatch(Match match, Stadium stadium) {
        if (checkMatchName(match, true)) {
            return false;
        }
        try {
            PreparedStatement pr = connection.prepareStatement("INSERT INTO match (match_name, command1, command2, stadium_id, datetime)\n" +
                    "VALUES (?, ?, ?, ?, ?)");
            pr.setString(1, match.getMatchName());
            pr.setString(2, match.getCommand1());
            pr.setString(3, match.getCommand2());
            pr.setInt(4, stadium.getStadiumID());
            pr.setTimestamp(5, match.getDateTime());
            pr.execute();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void deleteMatch(Match match) {
        try {
            PreparedStatement pr = connection.prepareStatement("DELETE FROM match \n" +
                    "WHERE match_id = ?");
            pr.setInt(1, match.getMatchID());
            pr.execute();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Tickets getPlaceTicket(Place place) {
        Tickets ticket = null;
        try {
            PreparedStatement pr = connection.prepareStatement("" +
                    "SELECT ticket_id, status, cost FROM tickets\n" +
                    "where place_id = ?");
            pr.setInt(1, place.getPlaceID());
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                int ticket_id = rs.getInt(1);
                int status = rs.getInt(2);
                int cost = rs.getInt(3);
                ticket = new Tickets(place, cost, status, ticket_id);
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ticket;
    }
    public static void editTicket(Tickets ticket) {
        try {
            PreparedStatement pr = connection.prepareStatement("UPDATE tickets set cost = ? \n" +
                    "WHERE ticket_id = ?");
            pr.setInt(1, ticket.getTicketCost());
            pr.setInt(2, ticket.getTicketId());
            pr.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTicket(Tickets ticket) {
        try {
            PreparedStatement pr = connection.prepareStatement("DELETE FROM tickets \n" +
                    "WHERE ticket_id = ?");
            pr.setInt(1, ticket.getTicketId());
            pr.execute();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addNewTicket (Match match, Place place, int cost) {
        if (!checkPlace(match, place)) {
            return false;
        }
        try {
            PreparedStatement pr = connection.prepareStatement("INSERT INTO tickets (place_id, status, cost)\n" +
                    "VALUES (?, 0, ?)\n" +
                    "RETURNING ticket_id");
            pr.setInt(1, place.getPlaceID());
            pr.setInt(2, cost);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                int ticket_id = rs.getInt(1);
                addNewMatchTicket(match, ticket_id);
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean checkPlace(Match match, Place place) {
        boolean res = true;
        try {
            PreparedStatement pr = connection.prepareStatement("" +
                    "SELECT tickets.ticket_id FROM tickets\n" +
                    "JOIN match_tickets ON (tickets.ticket_id = match_tickets.ticket_id)" +
                    "where match_tickets.match_id = ? AND tickets.place_id = ? ");
            pr.setInt(1, match.getMatchID());
            pr.setInt(2, place.getPlaceID());
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                res = false;
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void addNewMatchTicket (Match match, int ticketId) {
        try {
            PreparedStatement pr = connection.prepareStatement("INSERT INTO match_tickets (match_id, ticket_id)\n" +
                    "VALUES (?, ?)");
            pr.setInt(1, match.getMatchID());
            pr.setInt(2, ticketId);
            pr.execute();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
import java.sql.*;

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

    public static boolean addNewUser(String userName, String userPassword) {
        if (checkUser(userName)) {
            return false;
        }
        try {
            PreparedStatement pr  = connection.prepareStatement("INSERT INTO \"user\" (login, password) VALUES (?,?)");
            pr.setString(1, userName);
            pr.setString(2, userPassword);
            pr.execute();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean checkUser(String userName) {
        boolean exsists = false;
        try {
            PreparedStatement pr  = connection.prepareStatement("SELECT login FROM \"user\" WHERE login = ?");
            pr.setString(1, userName);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                exsists = true;
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exsists;
    }

    public static boolean loginUser (String userName, String userPassword) {
        boolean exsists = false;
        try {
            PreparedStatement pr  = connection.prepareStatement("SELECT login FROM \"user\" WHERE login = ? AND password = ?");
            pr.setString(1, userName);
            pr.setString(2, userPassword);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                exsists = true;
            }
            rs.close();
            pr.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exsists;
    }
}
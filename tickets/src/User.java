import java.util.ArrayList;

public class User {
    private String login;
    private boolean isAdmin;
    private ArrayList<Tickets> ticketses;

    public boolean addNewUser (String login, String password) {
        boolean result = DBProcessor.addNewUser(login,password);
        if (result) {
            this.login = login;
        }
        return result;
    }

    public boolean loginUser (String login, String password) {
        return DBProcessor.loginUser(login, password);
    }
}

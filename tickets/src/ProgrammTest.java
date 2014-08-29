import BLogic.Tickets;
import BLogic.User;
import DataLayer.DBProcessor;
import org.junit.Test;

import java.util.ArrayList;

public class ProgrammTest {
    @Test
    public void testAddNewPlayer() throws Exception {
        String newLogin = "test";
        String newPassword = "test";
        DBProcessor.connect();
        User user = new User(newLogin, newPassword);
        DBProcessor.addNewUser(user);
        if (DBProcessor.loginUser(user) != null) {
            System.out.println("Пользователь добавлен успешно!");
        } else  {
            System.err.println("Пользователь не добавлен!");
        }
    }

    @Test
    public void testIsAdmin () throws Exception {
        DBProcessor.connect();
        User user = new User("log", "pas");
        user = DBProcessor.loginUser(user);
        if (user.isAdmin()){
            System.out.println("Свойство \"администратор\" работает успешно!");
        } else  {
            System.err.println("Свойство \"администратор\" не работает!");
        }
    }

    @Test
    public void testUpdateTicket () throws Exception {
        DBProcessor.connect();
        User user = new User("log", "pas");
        user = DBProcessor.loginUser(user);
        ArrayList<Tickets> tickets = DBProcessor.getUserTickets(user);
        tickets.get(1).setTicketCost(123);
        int newCost = tickets.get(1).getTicketCost();
        if (newCost == 123)  {
            System.out.println("Билеты успешно меняются!");
        } else  {
            System.err.println("Билеты не меняются!");
        }
    }
}
import DataLayer.DBProcessor;
import PresentationLayer.LoginWindow;

public class FootballTickets {
    public static void main (String args[]) {

        DBProcessor.connect();
        LoginWindow.initFrame();
    }
}

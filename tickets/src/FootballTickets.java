import DataLayer.DBProcessor;
import HttpLayer.HttpServer;
import PresentationLayer.Window.LoginWindow;

public class FootballTickets {
    public static void main (String args[]) {
        DBProcessor.connect();
        LoginWindow.initFrame();
        HttpServer httpServer = new HttpServer();
        new Thread(httpServer).start();
    }
}

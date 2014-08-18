public class FootballTickets {
    public static void main (String args[]) {

        DBProcessor.connect();
        System.out.println(DBProcessor.addNewUser("log2", "pas"));
    }
}

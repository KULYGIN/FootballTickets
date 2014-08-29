package PresentationLayer.Window;

import BLogic.Match;
import BLogic.User;
import PresentationLayer.Frame.*;
import ServiceLayer.MatchTimeProcessor;
import ServiceLayer.ModalDialog;
import com.toedter.calendar.JCalendar;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;

public class MainWindow implements ActionListener {
    private User user;
    private JFrame mainFrame;
    private JMenuItem exitAction = new JMenuItem("Выход");
    private JMenuItem ticketsAction = new JMenuItem("Мои билеты");
    private JMenuItem stadiumMenu = new JMenuItem("Стадионы");
    private JMenuItem matchMenu = new JMenuItem("Матчи");
    private JMenuItem ticketMenu = new JMenuItem("Билеты");
    private JList matchList = new JList();
    private JButton showTicketsButton = new JButton("Показать доступные билеты");
    private ArrayList<Match> matches;
    private int currentMatch = -1;
    private JCalendar jCalendar = new JCalendar();
    private JTextField nameSearchField = new JTextField();
    private JButton dateSearchButton = new JButton("Найти");
    private JButton nameSearchButton = new JButton("Найти");
    private MatchTicketsFrame matchTicketsFrame;
    private JLabel statusLable =  new JLabel("");
    private JButton clearSearch = new JButton("Очистить поиск");
    public MainWindow(User user) {
        try {
            UIManager.setLookAndFeel(UIManager
                    .getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }

        this.user = user;
        mainFrame = new JFrame("FootballTickets " + user.getLogin());
        JMenuBar menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);
        JMenu accountMenu = new JMenu("Аккаунт");
        menuBar.add(accountMenu);

        JMenu adminMenu = new JMenu("Меню администратора");
        menuBar.add(adminMenu);
        adminMenu.add(stadiumMenu);
        adminMenu.add(matchMenu);
        adminMenu.add(ticketMenu);

        if (!user.isAdmin()) {
             adminMenu.setVisible(false);
        }

        accountMenu.add(exitAction);
        exitAction.addActionListener(this);
        accountMenu.add(ticketsAction);
        ticketsAction.addActionListener(this);
        stadiumMenu.addActionListener(this);
        matchMenu.addActionListener(this);
        ticketMenu.addActionListener(this);

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dim = kit.getScreenSize();

        mainFrame.setSize(800, 500);

        mainFrame.setLocation(dim.width / 2 - mainFrame.getWidth() / 2,
                dim.height / 2 - mainFrame.getHeight() / 2);
        mainFrame.setVisible(true);

        intiMainFrame();
    }

    public void intiMainFrame() {
        clearFrame();
        statusLable.setText("");
        JPanel matchPanel = new JPanel();
        matchPanel.setLayout(new BorderLayout());

        matchList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        matchList.setLayoutOrientation(JList.VERTICAL);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(matchList);
        scrollPane.setPreferredSize(new Dimension(400, 800));

        matchList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        matchList.addListSelectionListener(new SharedListSelectionHandler());

        JLabel availableMatchLabel = new JLabel("Доступные матчи");
        availableMatchLabel.setFont(new Font("Serif", Font.BOLD, 20));
        availableMatchLabel.setHorizontalAlignment(SwingConstants.CENTER);
        matchPanel.add(availableMatchLabel, BorderLayout.NORTH);
        matchPanel.add(scrollPane, BorderLayout.CENTER);
        matchPanel.add(showTicketsButton, BorderLayout.SOUTH);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());

        JPanel dateSearchPanel = new JPanel();
        dateSearchPanel.setLayout(new BorderLayout());

        JLabel dateLabel = new JLabel("По дате");
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);

        dateSearchPanel.add(dateLabel, BorderLayout.NORTH);
        dateSearchPanel.add(jCalendar, BorderLayout.CENTER);
        dateSearchPanel.add(dateSearchButton, BorderLayout.SOUTH);

        JPanel nameSearchPanel = new JPanel();
        nameSearchPanel.setLayout(new BorderLayout());

        JLabel nameLabel = new JLabel("По названию");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameSearchPanel.add(nameLabel, BorderLayout.NORTH);
        nameSearchPanel.add(nameSearchField, BorderLayout.CENTER);
        nameSearchField.setPreferredSize(new Dimension(100, 30));
        nameSearchPanel.add(nameSearchButton, BorderLayout.SOUTH);

        JLabel searchLabel = new JLabel("Поиск:");
        searchLabel.setFont(new Font("Serif", Font.BOLD, 20));
        searchLabel.setHorizontalAlignment(SwingConstants.CENTER);
        searchPanel.add(searchLabel, BorderLayout.NORTH);
        searchPanel.add(dateSearchPanel, BorderLayout.CENTER);
        searchPanel.add(nameSearchPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(statusLable);
        topPanel.add(clearSearch);
        clearSearch.setVisible(false);

        mainFrame.add(matchPanel, BorderLayout.WEST);
        mainFrame.add(searchPanel, BorderLayout.CENTER);
        mainFrame.add(topPanel, BorderLayout.NORTH);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nameSearchButton.addActionListener(this);
        dateSearchButton.addActionListener(this);
        showTicketsButton.addActionListener(this);
        clearSearch.addActionListener(this);
        loadMatchList(0, null, null);
    }

    public void loadMatchList(int searchType, String stringToSearch, Timestamp thisTime) {
        matchList.removeAll();
        ArrayList<Match> matches = MatchTimeProcessor.getAvalibleMatch(searchType, stringToSearch, thisTime);
            DefaultListModel listModel = new DefaultListModel();
            for (Match match : matches) {
                listModel.addElement(match.getMatchString());
            }
            this.matches = matches;
            matchList.setModel(listModel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == showTicketsButton) {
            if (currentMatch < 0 || currentMatch >= matches.size() ) {
                ModalDialog.showEror(mainFrame, "Не выбран матч");
                return;
            }
            clearFrame();
            matchTicketsFrame = new MatchTicketsFrame(mainFrame, user, matches.get(currentMatch), this);
        }
        if (source == nameSearchButton) {
            String stringToSearch = nameSearchField.getText().trim();
            if (stringToSearch == null || stringToSearch.equals("")) {
                ModalDialog.showEror(mainFrame, "Название не может быть пустым");
            }
            loadMatchList(2, stringToSearch, null);
            statusLable.setText("Результаты поиска по запросу: " + stringToSearch);
            clearSearch.setVisible(true);
            mainFrame.revalidate();
        }

        if (source == dateSearchButton) {
            Timestamp date = new Timestamp(jCalendar.getDate().getTime());
            statusLable.setText("Результаты поиска по запросу: " + date.toString().substring(0, date.toString().length()-13));
            loadMatchList(1, null, date);
            clearSearch.setVisible(true);
            mainFrame.revalidate();
        }

        if (source == clearSearch) {
            intiMainFrame();
            mainFrame.revalidate();
        }

        if (source == ticketsAction) {
            clearFrame();
            new MyTicketsFrame(mainFrame, user, this);
        }

        if (source == stadiumMenu) {
            clearFrame();
            new StadiumMenuFrame(mainFrame, this);
        }

        if (source == matchMenu) {
            clearFrame();
            new MatchMenuFrame(mainFrame, this);
        }

        if (source == ticketMenu) {
            clearFrame();
            new TicketMenuFrame(mainFrame, this);
        }
    }

    private class SharedListSelectionHandler implements javax.swing.event.ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            JList lsm = (JList) e.getSource();
            if (lsm == matchList) {
                int selectedIndex = lsm.getSelectedIndex();
                if (selectedIndex >= 0) {
                    currentMatch = selectedIndex;
                }
            }
        }
    }

   public void updatePlace () {
       matchTicketsFrame.fillPlace();
   }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public void clearFrame() {
        mainFrame.getContentPane().removeAll();
        mainFrame.revalidate();
    }
}

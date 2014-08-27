package PresentationLayer;

import BLogic.Match;
import BLogic.Place;
import BLogic.Stadium;
import BLogic.Tickets;
import DataLayer.DBProcessor;
import ServiceLayer.ModalDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TicketMenuFrame implements ActionListener {
    private JList ticketList = new JList();
    private int currentStadiumIndex = -1;
    private int currentMatchIndex = -1;
    private int currentPlaceIndex = -1;
    private int currentTicket = -1;
    private JFrame frame;
    private MainWindow mainWindow;
    private JButton backButton = new JButton("Назад");
    private JComboBox stadiumSelector = new JComboBox();
    private JComboBox matchSelector = new JComboBox();
    private JComboBox placeSelector = new JComboBox();
    private ArrayList<Stadium> stadiums;
    private ArrayList<Place> places;
    private ArrayList<Match> matches;
    private ArrayList<Tickets> tickets;
    private JTextField priceField = new JTextField("Стоимость");
    private JButton editPriceButton = new JButton("Изменить стоимость");
    private JButton newTicketButton = new JButton("Новый билет");
    private JButton deleteTicketButton = new JButton("Удалить билет");

    public TicketMenuFrame(JFrame frame, MainWindow mainWindow) {
        this.frame = frame;
        this.mainWindow = mainWindow;

        ticketList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        ticketList.setLayoutOrientation(JList.VERTICAL);

        JPanel stadiumPanel = new JPanel();
        stadiumPanel.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(ticketList);
        scrollPane.setPreferredSize(new Dimension(400, 800));

        stadiumPanel.add(new JLabel("Билеты"), BorderLayout.NORTH);
        stadiumPanel.add(scrollPane, BorderLayout.CENTER);

        ticketList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        ticketList.addListSelectionListener(new SharedListSelectionHandler());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

        JPanel leftTopPanel = new JPanel();
        leftTopPanel.setLayout(new BorderLayout());
        JLabel stdiumLable = new JLabel("Стадионы:");
        stdiumLable.setHorizontalAlignment(SwingConstants.CENTER);
        stdiumLable.setFont(new Font("Serif", Font.BOLD, 20));
        leftTopPanel.add(stdiumLable, BorderLayout.NORTH);
        leftTopPanel.add(stadiumSelector, BorderLayout.CENTER);
        JLabel matchLable = new JLabel("Матчи:");
        matchLable.setHorizontalAlignment(SwingConstants.CENTER);
        matchLable.setFont(new Font("Serif", Font.BOLD, 20));
        leftTopPanel.add(matchLable, BorderLayout.SOUTH);

        JPanel leftCenterPanel = new JPanel();
        leftCenterPanel.setLayout(new BorderLayout());
        leftCenterPanel.add(matchSelector, BorderLayout.NORTH);
        JLabel placeLabel = new JLabel("Место:");
        placeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        placeLabel.setFont(new Font("Serif", Font.BOLD, 20));
        leftCenterPanel.add(placeLabel, BorderLayout.CENTER);
        leftCenterPanel.add(placeSelector, BorderLayout.SOUTH);

        JPanel leftDownPanel = new JPanel();
        leftDownPanel.setLayout(new BorderLayout());
        JLabel priceLabel = new JLabel("Стоимость:");
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        priceLabel.setFont(new Font("Serif", Font.BOLD, 20));
        leftDownPanel.add(priceLabel, BorderLayout.NORTH);
        leftDownPanel.add(priceField, BorderLayout.CENTER);
        leftDownPanel.add(editPriceButton, BorderLayout.SOUTH);

        leftPanel.add(leftTopPanel, BorderLayout.NORTH);
        leftPanel.add(leftCenterPanel, BorderLayout.CENTER);
        leftPanel.add(leftDownPanel, BorderLayout.SOUTH);

        JPanel downPanel = new JPanel();
        downPanel.setLayout(new FlowLayout());
        downPanel.add(newTicketButton);
        downPanel.add(deleteTicketButton);
        downPanel.add(backButton);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(stadiumPanel, BorderLayout.CENTER);
        mainPanel.add(downPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        fillTStadiums();
        frame.revalidate();
        backButton.addActionListener(this);
        stadiumSelector.addActionListener(this);
        matchSelector.addActionListener(this);
        placeSelector.addActionListener(this);
        editPriceButton.addActionListener(this);
        newTicketButton.addActionListener(this);
        deleteTicketButton.addActionListener(this);
    }

    public void fillTStadiums() {
        stadiumSelector.removeAllItems();

        stadiums = DBProcessor.getStadiums();
        for (int i = 0; i < stadiums.size(); i++){
            stadiumSelector.addItem(stadiums.get(i).getStadiumName());
        }
        frame.revalidate();
    }

    public void fillMatches() {
        matchSelector.removeAllItems();
        matches = DBProcessor.getStadiumMatches(stadiums.get(currentStadiumIndex));
        for (int i = 0; i < matches.size(); i++){
            matchSelector.addItem(matches.get(i).getMatchName());
        }
        frame.revalidate();
    }

    public void fillPlaces() {
        placeSelector.removeAllItems();
        Stadium stadium = DBProcessor.getStadiumPlaces(stadiums.get(currentStadiumIndex));
        places = stadium.getPlaces();

        for (int i = 0; i < places.size(); i++){
            placeSelector.addItem(places.get(i).getPlaceString());
        }
        frame.revalidate();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
       Object source = e.getSource();
        if (source == backButton) {
            mainWindow.intiMainFrame();
            frame.setVisible(true);
        }
        if (source == stadiumSelector) {
            placeSelector.removeAllItems();
            ticketList.setModel(new DefaultListModel());
            priceField.setText("Стоимость");
            currentStadiumIndex = stadiumSelector.getSelectedIndex();
            if (currentStadiumIndex >= 0) {
                fillMatches();
                frame.revalidate();
            }
        }
        if (source == matchSelector) {
            currentMatchIndex = matchSelector.getSelectedIndex();
            if (currentMatchIndex >= 0) {
                fillPlaces();
                fillTickets(matches.get(currentMatchIndex));
                frame.revalidate();
            }
        }
        if (source == placeSelector) {
            currentPlaceIndex = placeSelector.getSelectedIndex();
            if (currentPlaceIndex >= 0) {
               Tickets ticket = DBProcessor.getPlaceTicket(places.get(currentPlaceIndex));
               if (ticket!= null) {
                   priceField.setText(String.valueOf(ticket.getTicketCost()));
               }else {
                   priceField.setText("билета нет");
               }
                frame.revalidate();
            }
        }

        if (source == editPriceButton) {
            if (currentTicket < 0) {
                ModalDialog.showEror(frame, "Билет не выбран!");
                return;
            }
        try
        {
            Integer.parseInt(priceField.getText().trim());
        }
        catch(NumberFormatException ex)
        {
            ModalDialog.showEror(frame, "Стоимость не число!");
            return;
        }
            int price = Integer.valueOf(priceField.getText().trim());
            if (price <= 0) {
                ModalDialog.showEror(frame, "Не корректная стоимость!");
                return;
            }
            tickets.get(currentTicket).setTicketCost(price);
            tickets.get(currentTicket).updateTicketCost();
            ModalDialog.showComplete(frame, "Стоимость обновлена!");
            fillTickets(matches.get(currentMatchIndex));
        }

        if (source == deleteTicketButton) {
            if (currentTicket < 0 || tickets.isEmpty()) {
                ModalDialog.showEror(frame, "Билет не выбран!");
                return;
            }
            tickets.get(currentTicket).deleteTicket();
            ModalDialog.showComplete(frame, "Билет удален!");
            fillTickets(matches.get(currentMatchIndex));
        }

        if (source == newTicketButton) {
            if (currentPlaceIndex < 0 || currentMatchIndex < 0 || currentStadiumIndex < 0) {
                ModalDialog.showEror(frame, "Выберите матч, стадион и место!");
            }

            try
            {
                Integer.parseInt(priceField.getText().trim());
            }
            catch(NumberFormatException ex)
            {
                ModalDialog.showEror(frame, "Стоимость не число!");
                return;
            }
            int price = Integer.valueOf(priceField.getText().trim());
            if (price <= 0) {
                ModalDialog.showEror(frame, "Не корректная стоимость!");
                return;
            }
            if (!matches.get(currentMatchIndex).addNewTicket(places.get(currentPlaceIndex), Integer.valueOf(priceField.getText().trim()))) {
                ModalDialog.showEror(frame, "Билет на это место уже существует!");
            }
            ModalDialog.showComplete(frame, "Билет добавлен!");
            fillTickets(matches.get(currentMatchIndex));
        }
    }

    private boolean checkPlaceFiel() {
        if (currentStadiumIndex < 0) {
            ModalDialog.showEror(frame, "Выберите стадион!");
            return false;
        }


//        if (sectorField.getText().trim().isEmpty() || placeField.getText().trim().isEmpty()) {
//            ModalDialog.showEror(frame, "Сектор и место не могут быть пустыми!");
//            return false;
//        }
//
//        try
//        {
//            Integer.parseInt(placeField.getText().trim());
//        }
//        catch(NumberFormatException ex)
//        {
//            ModalDialog.showEror(frame, "Номер местa не число!");
//            return false;
//        }
        return true;
    }

    public void fillTickets(Match match) {
        ticketList.removeAll();
        DefaultListModel listModel = new DefaultListModel();
        Match newMatch = DBProcessor.getStadiumPlace(match);
        tickets = newMatch.getTickets();
        for (Tickets ticket : tickets) {
           listModel.addElement(ticket.getTicketString());
        }
        ticketList.setModel(listModel);

        frame.revalidate();
    }

    private class SharedListSelectionHandler implements javax.swing.event.ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            JList lsm = (JList) e.getSource();
            if (lsm == ticketList) {
                int selectedIndex = lsm.getSelectedIndex();
                if (selectedIndex >= 0) {
                    currentTicket = selectedIndex;
                    priceField.setText(String.valueOf(tickets.get(currentTicket).getTicketCost()));
                    int placeID = tickets.get(currentTicket).getPlace().getPlaceID();
                    int findIndex = 0;
                    for (int i = 0; i< places.size(); i++) {
                        if (places.get(i).getPlaceID() == placeID) {
                            findIndex = i;
                            break;
                        }
                    }
                    placeSelector.setSelectedIndex(findIndex);
                    frame.revalidate();
                    }
                }
            }
        }
}

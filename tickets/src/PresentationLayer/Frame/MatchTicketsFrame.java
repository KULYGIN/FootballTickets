package PresentationLayer.Frame;

import BLogic.Match;
import BLogic.Tickets;
import BLogic.User;
import DataLayer.DBProcessor;
import DataLayer.ImageProcessor;
import PresentationLayer.Window.MainWindow;
import PresentationLayer.Window.SaveBuyWindow;
import ServiceLayer.ModalDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MatchTicketsFrame implements ActionListener {
    private JList placeList = new JList();
    private JButton saveButton = new JButton("Забронировать");
    private Match match;
    private Tickets currentTicket = null;
    private User user;
    private JFrame frame;
    private MainWindow mainWindow;
    private JButton backButton = new JButton("Назад");
    public MatchTicketsFrame(JFrame frame, User user, Match match, MainWindow mainWindow) {
        this.frame = frame;
        this.match = match;
        this.user = user;
        this.mainWindow = mainWindow;

        placeList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        placeList.setLayoutOrientation(JList.VERTICAL);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(placeList);
        scrollPane.setPreferredSize(new Dimension(400, 800));

        placeList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        placeList.addListSelectionListener(new SharedListSelectionHandler());

        JPanel upPanel = new JPanel();
        upPanel.setLayout(new FlowLayout());

        JLabel titleLable = new JLabel("Билеты на матч " + match.getMatchString());
        titleLable.setHorizontalAlignment(SwingConstants.CENTER);

        upPanel.add(titleLable);
        upPanel.add(backButton);

        ImageIcon imageIcon = ImageProcessor.getImageIcon(match.getPicName(), 500, 400);
        JLabel imageLable = new JLabel(imageIcon);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BorderLayout());

        middlePanel.add(imageLable, BorderLayout.WEST);
        JPanel chosePanel = new JPanel();
        chosePanel.setLayout(new BorderLayout());

        chosePanel.add(new JLabel("Выбрать место"), BorderLayout.NORTH);
        chosePanel.add(scrollPane, BorderLayout.CENTER );
        chosePanel.add(saveButton, BorderLayout.SOUTH);

        middlePanel.add(chosePanel, BorderLayout.CENTER);

        mainPanel.add(upPanel, BorderLayout.NORTH);
        mainPanel.add(middlePanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        fillPlace ();
        frame.revalidate();
        saveButton.addActionListener(this);
        backButton.addActionListener(this);
    }

    public void fillPlace () {
        match = DBProcessor.getStadiumPlace(match);
        ArrayList <Tickets> tickets = match.getTickets();
        if (!tickets.isEmpty()) {
            DefaultListModel listModel = new DefaultListModel();
            for (Tickets ticket : tickets) {
                listModel.addElement(ticket.getFormatedTicketString());
            }
            placeList.setModel(listModel);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       Object source = e.getSource();
        if (source == saveButton) {
            if (currentTicket == null) {
                ModalDialog.showEror(frame, "Не выбрано место");
                return;
            }
           new SaveBuyWindow(user, mainWindow, currentTicket );
            frame.setVisible(false);
        }
        if (source == backButton) {
            mainWindow.intiMainFrame();
            frame.setVisible(true);
        }
    }

    private class SharedListSelectionHandler implements javax.swing.event.ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            JList lsm = (JList) e.getSource();
            if (lsm == placeList) {
                int selectedIndex = lsm.getSelectedIndex();
                if (selectedIndex >= 0) {
                    currentTicket = match.getTickets().get(selectedIndex);
                }
            }
        }
    }
}

package PresentationLayer;

import BLogic.Tickets;
import BLogic.User;
import ServiceLayer.ModalDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MyTicketsFrame implements ActionListener {
    private JList placeList = new JList();
    private int currentIndex = -1;
    private User user;
    private JFrame frame;
    private MainWindow mainWindow;
    private JButton backButton = new JButton("Назад");
    private JButton buyButton = new JButton("Купить");
    private JButton cancelButton = new JButton("Отменить бронирование");
    private ArrayList<Tickets> tickets;

    public MyTicketsFrame(JFrame frame, User user, MainWindow mainWindow) {
        this.frame = frame;
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

        JLabel titleLable = new JLabel("Ваши билеты");
        titleLable.setHorizontalAlignment(SwingConstants.CENTER);

        upPanel.add(titleLable);
        upPanel.add(backButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(buyButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(upPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        fillTickets();
        switchEnable(false);
        frame.revalidate();
        backButton.addActionListener(this);
        buyButton.addActionListener(this);
        cancelButton.addActionListener(this);
    }

    public void fillTickets() {
        currentIndex = -1;
        tickets = user.getUserTickets();
        DefaultListModel listModel = new DefaultListModel();
        String color = "<html><p style='color:green;'>";
        for (Tickets ticket : tickets) {
            if (ticket.getStatus() == 1) {
                listModel.addElement(color + ticket.getTicketString());
            }  else {
                listModel.addElement(ticket.getTicketString());
            }
        }
        placeList.setModel(listModel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       Object source = e.getSource();
        if (source == backButton) {
            mainWindow.intiMainFrame();
            frame.setVisible(true);
        }
        if (source == buyButton) {
            user.buyTicket(tickets.get(currentIndex));
            ModalDialog.showComplete(frame, "Билет куплен");
            fillTickets();
        }
        if (source == cancelButton) {
            user.returnTicket(tickets.get(currentIndex));
            ModalDialog.showComplete(frame, "Бронь снята");
            fillTickets();
        }
    }

    public void switchEnable (boolean type) {
        buyButton.setEnabled(type);
        cancelButton.setEnabled(type);
        frame.revalidate();
    }

    private class SharedListSelectionHandler implements javax.swing.event.ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            JList lsm = (JList) e.getSource();
            if (lsm == placeList) {
                int selectedIndex = lsm.getSelectedIndex();
                if (selectedIndex >= 0) {
                    currentIndex = selectedIndex;
                    if (tickets.get(currentIndex).getStatus() == 1) {
                        switchEnable(true);
                    } else {
                        switchEnable(false);
                    }
                }
            }
        }
    }
}

package PresentationLayer.Window;

import BLogic.Tickets;
import BLogic.User;
import ServiceLayer.ModalDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SaveBuyWindow implements ActionListener{
    private JFrame mainFrame;
    private JButton saveButton = new JButton("Забронировать билет");
    private JButton buyButton = new JButton("Купить билет");
    private JButton cancelButton = new JButton("Отмена");
    private User user;
    private MainWindow mainWindow;
    private Tickets ticket;

    public SaveBuyWindow(User user, final MainWindow mainWindow, Tickets ticket) {
        this.user = user;
        this.mainWindow = mainWindow;
        this.ticket = ticket;

        mainFrame = new JFrame("Забронировать/купить билет");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(new JLabel("Билет: " + ticket.getMatch().getMatchString()), BorderLayout.NORTH);
        mainPanel.add(new JLabel("Место: " + ticket.getPlace().getPlaceString()));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(buyButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainFrame.add(mainPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dim = kit.getScreenSize();
        mainFrame.setSize(600, 150);
        mainFrame.setLocation(dim.width / 2 - mainFrame.getWidth() / 2,
                dim.height / 2 - mainFrame.getHeight() / 2);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                mainFrame.dispose();
                mainWindow.getMainFrame().setVisible(true);
            }
        });

        saveButton.addActionListener(this);
        buyButton.addActionListener(this);
        cancelButton.addActionListener(this);
        mainFrame.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == saveButton) {
           user.saveBuyTicket(ticket, 1);
           ModalDialog.showComplete(mainFrame, "Бронирование успешно");
           mainWindow.getMainFrame().setVisible(true);
           mainWindow.updatePlace();
           mainFrame.dispose();
        }
        if (source == buyButton) {
            user.saveBuyTicket(ticket, 2);
            ModalDialog.showComplete(mainFrame, "Покупка успешна");
            mainWindow.getMainFrame().setVisible(true);
            mainWindow.updatePlace();
            mainFrame.dispose();
        }
        if (source == cancelButton) {
            mainFrame.dispose();
            mainWindow.getMainFrame().setVisible(true);
        }
    }
}

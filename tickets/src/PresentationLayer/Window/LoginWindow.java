package PresentationLayer.Window;

import BLogic.User;
import ServiceLayer.ModalDialog;
import ServiceLayer.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LoginWindow implements ActionListener, KeyListener {
    private JFrame mainFrame;
    private JPanel mainPanel = new JPanel();
    private JButton enterButton = new JButton("Войти");
    private JButton registerButton = new JButton("Зарегистрироваться");
    private JTextField loginField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();

    private LoginWindow() {
        mainFrame = new JFrame("FootballTickets login");

        mainPanel.setLayout(new SpringLayout());

        JLabel loginLabel = new JLabel("Логин");
        JLabel passwordLabel = new JLabel("Пароль");

        loginLabel.setLabelFor(loginField);
        passwordLabel.setLabelFor(passwordField);

        mainPanel.add(loginLabel);
        mainPanel.add(loginField);
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);
        mainPanel.add(registerButton);
        mainPanel.add(enterButton);

        SpringUtilities.makeCompactGrid(mainPanel,
                3, 2,
                6, 6,
                6, 6);

        mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dim = kit.getScreenSize();
        mainFrame.setSize(400, 150);
        mainFrame.setLocation(dim.width / 2 - mainFrame.getWidth() / 2,
                dim.height / 2 - mainFrame.getHeight() / 2);
        enterButton.addActionListener(this);
        registerButton.addActionListener(this);
        passwordField.addKeyListener(this); //Что бы заходить по enter
    }

    public static void initFrame() {
        try {
            UIManager.setLookAndFeel(UIManager
                    .getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }

        LoginWindow converter = new LoginWindow();

    }

    public User onEnter (String login, String password) {
        User user = new User(login,password);
        user = user.loginUser();
        if (user != null) {
            System.out.print("User " + login + "try to enter...");
            return user;
        } else {
            ModalDialog.showEror(mainFrame, "Не верный логин или пароль");
            return null;
        }
    }

    public User onRegister(String login, String password) {
        User user = new User(login,password);
        if (user.addNewUser() != null) {
            System.out.println("User " + login + " was added");
            user.setAdmin(false);
            return user;
        } else {
            ModalDialog.showEror(mainFrame, "Пользователь с таким именем существует");
            return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (!checkFields(login, password)) {
            return;
        }

        User user = null;
        if (source == enterButton) {
            user = onEnter(login,password);
        }
        if (source == registerButton) {
            user = onRegister(login,password);
        }
        if (user == null) {
            return;
        }
        mainFrame.dispose();
        new MainWindow(user);
    }

    public boolean checkFields (String login, String password) {
        if (login.isEmpty() || password.isEmpty()) {
            ModalDialog.showEror(mainFrame, "Имя или пароль не могут быть пустыми");
            return false;
        }
        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar() == '\n'){
            String login = loginField.getText().trim();
            String password = passwordField.getText().trim();

            if (!checkFields(login, password)) {
                return;
            }
            User user = onEnter(login,password);

            if (user == null) {
                return;
            }
            mainFrame.dispose();
            new MainWindow(user);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}


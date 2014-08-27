package PresentationLayer;

import BLogic.Match;
import BLogic.Stadium;
import DataLayer.DBProcessor;
import ServiceLayer.ModalDialog;
import com.toedter.calendar.JCalendar;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MatchMenuFrame implements ActionListener {
    private JList matchList = new JList();
    private int currentStadiumIndex = -1;
    private int currentMatchIndex = -1;
    private JFrame frame;
    private MainWindow mainWindow;
    private JButton backButton = new JButton("Назад");
    private JComboBox stadiumSelector = new JComboBox();
    private ArrayList<Stadium> stadiums;
    private ArrayList <Match> matches;
    private JTextField matchNameField = new JTextField("Название матча");
    private JTextField command1NameField = new JTextField("Команда1");
    private JTextField command2NameField = new JTextField("Команда2");
    private JTextField minutesField = new JTextField("00");
    private JTextField hoursField = new JTextField("00");
    private JCalendar jCalendar = new JCalendar();
    private JButton addMatch = new JButton("Добавить матч");
    private JButton editMatch = new JButton("Редактировать матч");
    private JButton deleteMatch = new JButton("Удалить матч");
    public MatchMenuFrame(JFrame frame, MainWindow mainWindow) {
        this.frame = frame;
        this.mainWindow = mainWindow;

        matchList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        matchList.setLayoutOrientation(JList.VERTICAL);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(matchList);
        scrollPane.setPreferredSize(new Dimension(400, 800));

        matchList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        matchList.addListSelectionListener(new SharedListSelectionHandler());

        JPanel stadiumPanel = new JPanel();
        stadiumPanel.setLayout(new BorderLayout());

        stadiumPanel.add(new JLabel("Матчи"), BorderLayout.NORTH);
        stadiumPanel.add(scrollPane, BorderLayout.CENTER);


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
        leftTopPanel.add(matchNameField, BorderLayout.SOUTH);

        JPanel leftCenterPanel = new JPanel();
        leftCenterPanel.setLayout(new BorderLayout());
        leftCenterPanel.add(command1NameField, BorderLayout.NORTH);
        leftCenterPanel.add(command2NameField, BorderLayout.CENTER);
        leftCenterPanel.add(jCalendar, BorderLayout.SOUTH);

        JPanel leftDownPanel = new JPanel();
        leftDownPanel.setLayout(new BorderLayout());
        leftDownPanel.add(new JLabel("Время"), BorderLayout.NORTH);
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new FlowLayout());
        timePanel.add(hoursField);
        timePanel.add(new JLabel(":"));
        timePanel.add(minutesField);
        leftDownPanel.add(timePanel, BorderLayout.CENTER);

        leftPanel.add(leftTopPanel, BorderLayout.NORTH);
        leftPanel.add(leftCenterPanel, BorderLayout.CENTER);
        leftPanel.add(leftDownPanel, BorderLayout.SOUTH);

        JPanel downPanel = new JPanel();
        downPanel.setLayout(new FlowLayout());
        downPanel.add(addMatch);
        downPanel.add(editMatch);
        downPanel.add(deleteMatch);
        downPanel.add(backButton);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(stadiumPanel, BorderLayout.CENTER);
        mainPanel.add(downPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        fillTStadiums();
        frame.revalidate();
        backButton.addActionListener(this);
        stadiumSelector.addActionListener(this);
        addMatch.addActionListener(this);
        editMatch.addActionListener(this);
        deleteMatch.addActionListener(this);
    }

    public void fillTStadiums() {
        stadiumSelector.removeAllItems();
        stadiums = DBProcessor.getStadiums();
        for (int i = 0; i < stadiums.size(); i++){
            stadiumSelector.addItem(stadiums.get(i).getStadiumName());
        }
        frame.revalidate();
    }

    public void fillMatches (Stadium stadium) {
        matchList.removeAll();
        DefaultListModel listModel = new DefaultListModel();
        stadium.fillPlaces();
        matches = DBProcessor.getStadiumMatches(stadiums.get(currentStadiumIndex));
        for (Match match : matches) {
            listModel.addElement(match.getMatchString());
        }
        matchList.setModel(listModel);
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
            currentStadiumIndex = stadiumSelector.getSelectedIndex();
            if (currentStadiumIndex >= 0) {
                fillMatches(stadiums.get(currentStadiumIndex));
                frame.revalidate();
            }
        }

        if (source == editMatch) {
            if (currentMatchIndex <0 || currentStadiumIndex < 0) {
                ModalDialog.showEror(frame, "Не выбран матч!");
                return;
            }

            Match match = checkMatchFields(false);
            if (match == null) {
                return;
            }

            if (!match.updateMatch()) {
                ModalDialog.showEror(frame, "Матч с таким именем существует!");
                return;
            }
            fillMatches(stadiums.get(currentStadiumIndex));
            ModalDialog.showComplete(frame, "Матч изменен!");
        }

        if (source == addMatch) {
            if (currentStadiumIndex < 0) {
                ModalDialog.showEror(frame, "Не выбран стадион!");
                return;
            }

            Match match = checkMatchFields(true);
            if (match == null) {
                return;
            }

            if (!DBProcessor.addNewMatch(match, stadiums.get(currentStadiumIndex))) {
                ModalDialog.showEror(frame, "Матч с таким именем существует!");;
                return;
            }
            fillMatches(stadiums.get(currentStadiumIndex));
            ModalDialog.showComplete(frame, "Матч добавлен!");
        }

        if (source == deleteMatch) {
            if (currentMatchIndex <0 || currentStadiumIndex < 0) {
                ModalDialog.showEror(frame, "Не выбран матч!");
                return;
            }
            matches.get(currentMatchIndex).deleteMatch();
            fillMatches(stadiums.get(currentStadiumIndex));
            ModalDialog.showComplete(frame, "Матч удален!");
        }
    }

    public Match checkMatchFields (boolean isNew) {
        Date calendarDate = jCalendar.getDate();

        String day = new SimpleDateFormat("dd").format(calendarDate);
        String month = new SimpleDateFormat("MM").format(calendarDate);
        String year = new SimpleDateFormat("yyyy").format(calendarDate);
        String hour = hoursField.getText().trim();
        String minutes = minutesField.getText().trim();

        if (hour.isEmpty() || minutes.isEmpty()) {
            ModalDialog.showEror(frame, "Время не может быть пустым!");
            return null;
        }
        try
        {
            int hourInt = Integer.parseInt(hour);
            int minutesInt = Integer.parseInt(minutes);

            if (hourInt > 24 || hourInt < 0 || minutesInt > 60 || minutesInt < 0) {
                ModalDialog.showEror(frame, "Не корректное время!");
                return null;
            }
        }
        catch(NumberFormatException ex)
        {
            ModalDialog.showEror(frame, "Время не число!");
            return null;
        }

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy/HH-mm");
        Date date = null;
        try {
            date = dateFormat.parse(day + "/" + month + "/" + year + "/" + hour + "-" + minutes);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        long time = date.getTime();
        Timestamp newTime = new Timestamp(time);

        String matchName = matchNameField.getText().trim();
        String command1Name = command1NameField.getText().trim();
        String command2Name = command2NameField.getText().trim();

        if (matchName.isEmpty() || command1Name.isEmpty() || command2Name.isEmpty()) {
            ModalDialog.showEror(frame, "Поля матча не могут быть пустыми!");
            return null;
        }

        if (command1Name.toUpperCase().equals(command2Name.toUpperCase())) {
            ModalDialog.showEror(frame, "Комманды должны быть разными!");
            return null;
        }
        if (isNew) {
            return new Match(matchName, command1Name, command2Name,
                    newTime, stadiums.get(currentStadiumIndex));
        } else {
            return new Match(matches.get(currentMatchIndex).getMatchID(), matchName, command1Name, command2Name,
                    newTime, stadiums.get(currentStadiumIndex));
        }
    }

    private class SharedListSelectionHandler implements javax.swing.event.ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            JList lsm = (JList) e.getSource();
            if (lsm == matchList) {
                int selectedIndex = lsm.getSelectedIndex();
                if (selectedIndex >= 0) {
                    currentMatchIndex = selectedIndex;
                    matchNameField.setText(matches.get(currentMatchIndex).getMatchName());
                    command1NameField.setText(matches.get(currentMatchIndex).getCommand1());
                    command2NameField.setText(matches.get(currentMatchIndex).getCommand2());
                    Date thisDate = new Date(matches.get(currentMatchIndex).getDateTime().getTime());
                    jCalendar.setDate(thisDate);

                    String hour = new SimpleDateFormat("HH").format(thisDate);
                    String minutes = new SimpleDateFormat("mm").format(thisDate);

                    hoursField.setText(hour);
                    minutesField.setText(minutes);
                    frame.revalidate();
                    }
                }
            }
        }
}

package PresentationLayer;

import BLogic.Place;
import BLogic.Stadium;
import DataLayer.DBProcessor;
import ServiceLayer.ModalDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class StdiumMenuFrame implements ActionListener {
    private JList placeList = new JList();
    private int currentStadiumIndex = -1;
    private int currentPlaceIndex = -1;
    private JFrame frame;
    private MainWindow mainWindow;
    private JButton backButton = new JButton("Назад");
    private JComboBox stadiumSelector = new JComboBox();
    private JButton editStadiumNameButton = new JButton("Редактировать стадион");
    private JButton deleteStadiumButton = new JButton("Удалить стадион");
    private JTextField newStadiumNameField = new JTextField("Название стадиона");
    private JButton newStadiumButton = new JButton("Добавить новый стадион");
    private JTextField sectorField = new JTextField("Сектор          ");
    private JTextField placeField = new JTextField("Место            ");
    private JButton addPlaceButton = new JButton("Добавить место");
    private JButton deletePlaceButton = new JButton("Удалить место");
    private JButton editPlaceButton = new JButton("Редактировать место");
    private JTextField aboutField = new JTextField("Описание");
    private JTextField picNameField = new JTextField("Имя изображения");
    private ArrayList<Stadium> stadiums;
    private ArrayList<Place> places;
    public StdiumMenuFrame(JFrame frame, MainWindow mainWindow) {
        this.frame = frame;
        this.mainWindow = mainWindow;

        placeList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        placeList.setLayoutOrientation(JList.VERTICAL);

        JPanel stadiumPanel = new JPanel();
        stadiumPanel.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(placeList);
        scrollPane.setPreferredSize(new Dimension(400, 800));

        stadiumPanel.add(new JLabel("Места"), BorderLayout.NORTH);
        stadiumPanel.add(scrollPane, BorderLayout.CENTER);

        placeList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        placeList.addListSelectionListener(new SharedListSelectionHandler());

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
        leftTopPanel.add(editStadiumNameButton, BorderLayout.SOUTH);

        JPanel leftCenterPanel = new JPanel();
        leftCenterPanel.setLayout(new BorderLayout());
        leftCenterPanel.add(deleteStadiumButton, BorderLayout.NORTH);
        JLabel newStadiumLabel = new JLabel("Новый стадион:");
        newStadiumLabel.setHorizontalAlignment(SwingConstants.CENTER);
        newStadiumLabel.setFont(new Font("Serif", Font.BOLD, 20));
        leftCenterPanel.add(newStadiumLabel, BorderLayout.CENTER);
        leftCenterPanel.add(newStadiumNameField, BorderLayout.SOUTH);

        JPanel leftDownPanel = new JPanel();
        leftDownPanel.setLayout(new BorderLayout());
        leftDownPanel.add(aboutField, BorderLayout.NORTH);
        leftDownPanel.add(picNameField, BorderLayout.CENTER);
        leftDownPanel.add(newStadiumButton, BorderLayout.SOUTH);

        leftPanel.add(leftTopPanel, BorderLayout.NORTH);
        leftPanel.add(leftCenterPanel, BorderLayout.CENTER);
        leftPanel.add(leftDownPanel, BorderLayout.SOUTH);

        JPanel downPanel = new JPanel();
        downPanel.setLayout(new FlowLayout());
        downPanel.add(new JLabel("Новое место:"));
        downPanel.add(sectorField);
        downPanel.add(placeField);
        downPanel.add(addPlaceButton);
        downPanel.add(editPlaceButton);
        downPanel.add(deletePlaceButton);
        downPanel.add(backButton);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(stadiumPanel, BorderLayout.CENTER);
        mainPanel.add(downPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        fillTStadiums();
        frame.revalidate();
        backButton.addActionListener(this);
        editStadiumNameButton.addActionListener(this);
        deleteStadiumButton.addActionListener(this);
        newStadiumButton.addActionListener(this);
        addPlaceButton.addActionListener(this);
        deletePlaceButton.addActionListener(this);
        editPlaceButton.addActionListener(this);
        stadiumSelector.addActionListener(this);
    }

    public void fillTStadiums() {
        stadiumSelector.removeAllItems();
        stadiums = DBProcessor.getStadiums();
        for (int i = 0; i < stadiums.size(); i++){
            stadiumSelector.addItem(stadiums.get(i).getStadiumName());
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
            currentStadiumIndex = stadiumSelector.getSelectedIndex();
            if (currentStadiumIndex >= 0) {
                fillPlaces(stadiums.get(currentStadiumIndex));
                newStadiumNameField.setText(stadiums.get(currentStadiumIndex).getStadiumName());
                aboutField.setText(stadiums.get(currentStadiumIndex).getAboutStadium());
                picNameField.setText(stadiums.get(currentStadiumIndex).getPicName());
                frame.revalidate();
            }
        }

        if (source == newStadiumButton) {
            String stadiumName = newStadiumNameField.getText().trim();
            String stadiumAbout = aboutField.getText().trim();
            String picName = picNameField.getText().trim();

            if (stadiumName.isEmpty()) {
                ModalDialog.showEror(frame, "Имя не может быть пустым!");
            }

            Stadium stadium = new Stadium(stadiumName, stadiumAbout, picName);
            if (!DBProcessor.addNewStadium(stadium)) {
                ModalDialog.showEror(frame, "Стадион с таким именем уже существует!");
            } else {
                fillTStadiums();
                ModalDialog.showComplete(frame, "Стадион успешно добавлен!");
            }
        }

        if (source == deleteStadiumButton) {
            if (currentStadiumIndex >= 0) {
                stadiums.get(currentStadiumIndex).deleteStadium();
                ModalDialog.showComplete(frame, "Стадион успешно удален!");
                fillTStadiums();
            } else {
                ModalDialog.showEror(frame, "Выберите стадион!");
            }
        }

        if (source == addPlaceButton) {
            if (!checkPlaceFiel()) {
                return;
            }
            String sector = sectorField.getText().trim();
            int place_num = Integer.valueOf(placeField.getText().trim());
            Place place = new Place(sector, place_num);
            if (!DBProcessor.addNewStadiumPlace(stadiums.get(currentStadiumIndex), place)) {
                ModalDialog.showEror(frame, "Такое место уже существует!");
            }   else {
                sectorField.setText("Сектор          ");
                placeField.setText("Место            ");
                ModalDialog.showComplete(frame, "Место добавлено!");
                fillPlaces(stadiums.get(currentStadiumIndex));
            }
        }

        if (source == editPlaceButton) {
            if (!checkPlaceFiel()) {
                return;
            }

            if (currentPlaceIndex <0) {
                ModalDialog.showEror(frame, "Место не выбрано!");
                return;
            }

            String sector = sectorField.getText().trim();
            int place_num = Integer.valueOf(placeField.getText().trim());
            Place place = new Place(places.get(currentPlaceIndex).getPlaceID(), sector, place_num);
            if (!place.updatePlace(stadiums.get(currentStadiumIndex))) {
                ModalDialog.showEror(frame, "Такое место уже существует!");
            }   else {
                sectorField.setText("Сектор          ");
                placeField.setText("Место            ");
                ModalDialog.showComplete(frame, "Место отредактированно!");
                fillPlaces(stadiums.get(currentStadiumIndex));
            }
            sectorField.setText("Сектор          ");
            placeField.setText("Место            ");
        }

        if (source == deletePlaceButton) {
            if (currentPlaceIndex < 0) {
                ModalDialog.showEror(frame, "Место не выбрано!");
                return;
            }
            places.get(currentPlaceIndex).deletePlace();
            sectorField.setText("Сектор          ");
            placeField.setText("Место            ");
            ModalDialog.showComplete(frame, "Место удалено!");
            fillPlaces(stadiums.get(currentStadiumIndex));
        }

        if (source == editStadiumNameButton) {
            if (currentStadiumIndex >= 0) {
                String stadiumName = newStadiumNameField.getText().trim();
                String stadiumAbout = aboutField.getText().trim();
                String picName = picNameField.getText().trim();

                if (stadiumName.isEmpty()) {
                    ModalDialog.showEror(frame, "Имя не может быть пустым!");
                }

                Stadium stadium = new Stadium(stadiums.get(currentStadiumIndex).getStadiumID(), stadiumName, stadiumAbout, picName);
                if (!stadium.editStadium()) {
                    ModalDialog.showEror(frame, "Стадион с таким именем уже существует!");
                } else {
                    fillTStadiums();
                    ModalDialog.showComplete(frame, "Стадион успешно изменен!");
                }
            } else {
                ModalDialog.showEror(frame, "Выберите стадион!");
            }
        }
    }

    private boolean checkPlaceFiel() {
        if (currentStadiumIndex < 0) {
            ModalDialog.showEror(frame, "Выберите стадион!");
            return false;
        }


        if (sectorField.getText().trim().isEmpty() || placeField.getText().trim().isEmpty()) {
            ModalDialog.showEror(frame, "Сектор и место не могут быть пустыми!");
            return false;
        }

        try
        {
            Integer.parseInt(placeField.getText().trim());
        }
        catch(NumberFormatException ex)
        {
            ModalDialog.showEror(frame, "Номер местa не число!");
            return false;
        }
        return true;
    }

    public void fillPlaces(Stadium stadium) {
        placeList.removeAll();
        DefaultListModel listModel = new DefaultListModel();
        stadium.fillPlaces();
        places = stadium.getPlaces();
        for (Place place : places) {
           listModel.addElement(place.getPlaceString());
        }
        placeList.setModel(listModel);
        frame.revalidate();
    }

    private class SharedListSelectionHandler implements javax.swing.event.ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            JList lsm = (JList) e.getSource();
            if (lsm == placeList) {
                int selectedIndex = lsm.getSelectedIndex();
                if (selectedIndex >= 0) {
                    currentPlaceIndex = selectedIndex;
                    sectorField.setText(places.get(currentPlaceIndex).getSector());
                    placeField.setText(String.valueOf(places.get(currentPlaceIndex).getPlaceNum()));
                    frame.revalidate();
                    }
                }
            }
        }
}

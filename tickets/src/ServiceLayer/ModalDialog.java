package ServiceLayer;

import javax.swing.*;

public class ModalDialog {
    public static void showEror (JFrame frame, String text) {
        JOptionPane.showMessageDialog(frame,
                text,
                "Внимание!",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void showComplete (JFrame frame, String text) {
        JOptionPane.showMessageDialog(frame,
                text,
                "Успешно!",
                JOptionPane.PLAIN_MESSAGE);
    }
}

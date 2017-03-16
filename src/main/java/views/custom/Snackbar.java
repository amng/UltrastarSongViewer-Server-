package views.custom;

import de.craften.ui.swingmaterial.MaterialColor;

import javax.swing.*;
import java.awt.*;

public class Snackbar extends JPanel {

    public static final int DURATION_LONG = 3000;
    public static final int DURATION_SHORT = 1000;
    private JLabel label = new JLabel();
    private JPanel snackbarPanel = new JPanel();
    private static Snackbar instance = null;
    private int duration = 0;
    private boolean isDisplaying = false;

    private Snackbar(){
        setOpaque(false);
        setLayout(new BorderLayout());
        add(snackbarPanel, BorderLayout.SOUTH);
        snackbarPanel.setLayout(new BorderLayout());
        snackbarPanel.setBackground(MaterialColor.GREY_900);
        snackbarPanel.setPreferredSize(new Dimension(400, 50));
        snackbarPanel.add(label);
        label.setForeground(MaterialColor.WHITE);
        label.setFont(new Font(null, Font.BOLD, 15));
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    public static Snackbar getInstance(){
        return instance == null ? instance = new Snackbar() : instance;
    }


    public void showMessage(String message, int duration){
        if (duration != DURATION_LONG && duration != DURATION_SHORT) {
            System.err.println("Duration must be one of DURATION_LONG, DURATION_SHORT");
        }
        label.setText(message);
        this.duration = duration;
        display();
    }

    private void display() {
        if (isDisplaying)
            hideSnackbar();
        setVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setVisible(false);
                isDisplaying = false;
            }
        }).start();
    }

    private void hideSnackbar() {
        setVisible(false);
    }
}

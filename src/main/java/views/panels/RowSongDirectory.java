package views.panels;

import controller.managers.DatabaseManager;
import controller.managers.FileManager;
import de.craften.ui.swingmaterial.MaterialColor;
import de.craften.ui.swingmaterial.MaterialPanel;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import model.database.Song;
import model.database.SongDirectoryInfo;
import views.MainFrame;
import views.constants.Extras;
import views.custom.MaterialIcoButton;
import views.custom.MaterialProgressBar;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class RowSongDirectory extends MaterialPanel implements Observer {
    private SongDirectoryInfo model;
    private JLabel label = new JLabel();
    private JButton removeButton ;
    private JButton startAnalizing;
    private JLabel labelSongCount = new JLabel();
    private MaterialProgressBar progressBar = new MaterialProgressBar();

    public RowSongDirectory(){
        init();
    }

    public RowSongDirectory(SongDirectoryInfo model){
        init();
        update(model);
    }

    public void update(SongDirectoryInfo model) {
        this.model = model;
        label.setText(model.directory);
    }

    private void init(){
        progressBar.setMinimum(0);
        progressBar.setVisible(false);
        setBackground(MaterialColor.WHITE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(0, 200));
        label.setFont(new Font(null, Font.BOLD, 15));
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        removeButton = new MaterialIcoButton(new ImageIcon(getClass().getResource("/delete.png")));
        startAnalizing = new MaterialIcoButton(new ImageIcon(getClass().getResource("/refresh.png")));
        //removeButton.setMaximumSize(new Dimension(56, 56));
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getInstance().getMainPanel().removeElement(model);
            }
        });

        startAnalizing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisible(true);
                        removeButton.setEnabled(false);
                        startAnalizing.setEnabled(false);
                        FileManager.getInstance().addObserver(RowSongDirectory.this);
                        FileManager.getInstance().syncData(label.getText());
                        removeButton.setEnabled(true);
                        startAnalizing.setEnabled(true);
                        progressBar.setVisible(false);
                        updateSongCount();
                    }
                }).start();
            }
        });
        JPanel btnsPanel = new JPanel();
        JPanel infoPanel = new JPanel();
        setupInfoPanel(infoPanel);
        btnsPanel.setOpaque(false);
        btnsPanel.setBackground(MaterialColor.WHITE);
        btnsPanel.setLayout(new FlowLayout());
        btnsPanel.add(removeButton);
        btnsPanel.add(startAnalizing);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(btnsPanel, BorderLayout.EAST);
        centerPanel.add(infoPanel);
        add(centerPanel, BorderLayout.CENTER);
        add(label, BorderLayout.NORTH);
        add(progressBar, BorderLayout.SOUTH);
    }

    private void setupInfoPanel(JPanel panel) {
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        updateSongCount();
        labelSongCount.setForeground(MaterialColor.GREY_500);
        labelSongCount.setFont(new Font(null,Font.BOLD,40));
        labelSongCount.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        panel.add(labelSongCount);
    }

    public void updateSongCount(){
        EntityManager em = DatabaseManager.getInstance().getFactory();
        Integer count = ((Long)em.createQuery("select count(*) from " + Song.class.getSimpleName())
                .getSingleResult()).intValue();
        labelSongCount.setText(count.toString());
    }

    public JButton getRemoveButton() {
        return removeButton;
    }

    @Override
    public void update(Observable o, Object arg) {
        Pair pairs = (Pair) arg;
        String key = ((String)pairs.getKey());
        if (key.equals(Extras.SONGS_COUNT)) {
            progressBar.setMaximum((Integer) pairs.getValue());

        } else if (key.equals(Extras.SONGS_PROGRESS)) {
            progressBar.setValue((Integer) pairs.getValue()+1);
        }
    }
}

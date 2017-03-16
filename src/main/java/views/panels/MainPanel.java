package views.panels;

import controller.managers.DatabaseManager;
import de.craften.ui.swingmaterial.MaterialColor;
import de.craften.ui.swingmaterial.MaterialIconButton;
import hello.Application;
import model.database.SongDirectoryInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import views.MainFrame;
import views.constants.Colors;
import views.constants.Strings;
import views.custom.Snackbar;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

public class MainPanel extends JPanel {

    private JLabel textField = new JLabel(Strings.HINT_CHOOSE_FOLDER);
    private MaterialIconButton button = new MaterialIconButton();
    private JTable list;
    private DefaultTableModel model;
    private Vector<SongDirectoryInfo> listData = new Vector<SongDirectoryInfo>();
    private boolean serverRunning = false;
    private MaterialIconButton startServerBtn;
    private ConfigurableApplicationContext context;
    private boolean isStarting = false;

    public MainPanel(){
        this.setLayout(new BorderLayout());
        init();
        initActionButtons();
        add(list);
    }

    private void initActionButtons() {
        MaterialIconButton addBtn = new MaterialIconButton();
        addBtn.setIcon(new ImageIcon(getClass().getResource("/add.png")));
        MainFrame.getInstance().getToolBar().addActionButton(addBtn);
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(MainFrame.getInstance());

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    addToList(file);
                }
            }
        });

        startServerBtn = new MaterialIconButton();
        setServerButtonIcon();
        startServerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isStarting) {
                    if (!serverRunning) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                isStarting = true;
                                context = SpringApplication.run(Application.class);
                                serverRunning = true;
                                isStarting = false;
                                setServerButtonIcon();
                            }
                        }).start();
                    } else {
                        if (context != null) {
                            SpringApplication.exit(context);
                            serverRunning = false;
                        }
                    }
                }
                setServerButtonIcon();
            }
        });
        MainFrame.getInstance().getToolBar().addActionButton(startServerBtn);
    }

    public void setServerButtonIcon(){
        if (!serverRunning) {
            startServerBtn.setIcon(new ImageIcon(getClass().getResource("/play.png")));
        } else {
            startServerBtn.setIcon(new ImageIcon(getClass().getResource("/stop.png")));
        }
    }

    private void init() {
        initList();
        button.setText("Choose Folder");
        //button.setType(MaterialButton.Type.RAISED);
        button.setEnabled(true);
        button.setBackground(Colors.ACCENT);
        //button.setMinimumSize(new Dimension(300, 100));
        button.setPreferredSize(new Dimension(56, 56));
        button.setIcon(new ImageIcon(getClass().getResource("/add.png")));
    }

    private void addToList(File f){
        SongDirectoryInfo songDirectoryInfo = new SongDirectoryInfo();
        songDirectoryInfo.directory = f.getAbsolutePath();
        if (songDirectoryInfo.save()) {
            listData.add(songDirectoryInfo);
            model.fireTableDataChanged();
        } else {
            Snackbar.getInstance().showMessage(Strings.ERROR_ADD_DIR, Snackbar.DURATION_LONG);
        }
    }

    private void initList() {
        model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return RowSongDirectory.class;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public int getRowCount() {
                return listData.size();
            }

            @Override
            public Object getValueAt(int row, int column) {
                return listData.get(row);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        EntityManager em = DatabaseManager.getInstance().getFactory();
        listData.addAll(em.createQuery("from " + SongDirectoryInfo.class.getSimpleName()).getResultList());
        list = new JTable(model);
        list.setFocusable(false);
        list.setShowGrid(false);
        list.setRowMargin(0);
        list.setIntercellSpacing(new Dimension(10, 5));
        list.setRowSelectionAllowed(false);
        list.setVisible(true);
        list.setRowHeight(175);
        list.setDefaultRenderer(RowSongDirectory.class, new PanelCellEditorRenderer());
        list.setDefaultEditor(RowSongDirectory.class, new PanelCellEditorRenderer());
        list.setBackground(MaterialColor.GREY_300);
        list.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
    }

    public void removeElement(SongDirectoryInfo elem){
        listData.remove(elem);
        model.fireTableStructureChanged();
        elem.delete();
    }

    public class PanelCellEditorRenderer extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
        RowSongDirectory feedComponent;

        PanelCellEditorRenderer(){
            feedComponent = new RowSongDirectory();
        }

        public Component getTableCellEditorComponent(JTable table, final Object value,
                                                     boolean isSelected, int row, int column) {
            feedComponent.update(listData.get(row));
            return feedComponent;
        }

        public Object getCellEditorValue() {
            return null;
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus, int row, int column) {
            feedComponent.update(listData.get(row));
            return feedComponent;
        }
    }
}

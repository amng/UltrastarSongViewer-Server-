package views;

import controller.ComponentResize;
import de.craften.ui.swingmaterial.MaterialColor;
import views.constants.Colors;
import views.constants.Strings;
import views.custom.Snackbar;
import views.panels.MainPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    private MaterialToolbar toolbar = new MaterialToolbar();
    private static MainFrame instance = null;
    private MainPanel mainPanel;

    private MainFrame() {}

    public static MainFrame getInstance(){
        return instance == null ? instance = new MainFrame() : instance;
    }

    public void initWindow(){
        mainPanel = new MainPanel();
        JPanel contentPanel = new JPanel();
        Border padding = BorderFactory.createEmptyBorder(1, 1, 1, 1);
        contentPanel.setBorder(padding);
        setContentPane(contentPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(800, 600));
        setLayout(new BorderLayout());
        setLocation(20, 20);
        setUndecorated(true);
        ComponentResize cr = new ComponentResize();
        cr.registerComponent(this);

        add(toolbar, BorderLayout.NORTH);
        add(mainPanel);

        setGlassPane(Snackbar.getInstance());
        setVisible(true);
    }

    public MaterialToolbar getToolBar() {
        return toolbar;
    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }


    public class MaterialToolbar extends JPanel implements MouseListener, MouseMotionListener {
        private boolean dispatched = false;
        private Point initP = new Point();
        private JPanel actionsBtnPanel = new JPanel();
        private String title = Strings.APP_TITLE;
        private Color statusBarColor = Colors.PRIMARY_DARK_COLOR;
        private Color toolbarColor = Colors.PRIMARY_COLOR;
        private Color titleColor = MaterialColor.WHITE;

        public MaterialToolbar(){
            setLayout(new BorderLayout());
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
            init();
        }

        private void init() {
            setupStatusBar();
            setupToolbar();
            actionsBtnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            actionsBtnPanel.setOpaque(false);
        }

        private void setupStatusBar() {
            JPanel statusBar = new JPanel();
            statusBar.setBackground(statusBarColor);
            statusBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
            statusBar.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, statusBarColor));
            statusBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
            statusBar.addMouseListener(this);
            statusBar.addMouseMotionListener(this);
            setupButtons(statusBar);
            add(statusBar, BorderLayout.NORTH);
        }

        private void setupButtons(JPanel statusBar) {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(12, 12));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setIcon(new ImageIcon(getClass().getResource("/close.png")));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            statusBar.add(button);
        }

        private Point diffPoints(Point p1, Point p2){
            return new Point(p1.x - p2.x, p1.y - p2.y);
        }

        private void setupToolbar() {
            JPanel toolbar = new JPanel();
            toolbar.setLayout(new BorderLayout());
            toolbar.setBackground(toolbarColor);
            JLabel label = new JLabel(title);
            label.setBorder(new EmptyBorder(0, 24, 0, 0));
            label.setFont(new Font(null, Font.BOLD, 30));
            label.setForeground(titleColor);
            toolbar.add(label, BorderLayout.WEST);
            toolbar.add(actionsBtnPanel, BorderLayout.EAST);
            add(toolbar, BorderLayout.CENTER);
        }

        public void mouseClicked(MouseEvent e) {
            redispatchToParent(e);
        }

        public void mousePressed(MouseEvent e) {
            initP.setLocation(e.getPoint());
            redispatchToParent(e);
        }

        public void mouseReleased(MouseEvent e) {
            redispatchToParent(e);
        }

        public void mouseEntered(MouseEvent e) {
            redispatchToParent(e);
        }

        public void mouseExited(MouseEvent e) {
            redispatchToParent(e);
        }

        public void mouseDragged(MouseEvent e){
            MainFrame.this.setLocation(diffPoints(e.getLocationOnScreen(), initP));
            redispatchToParent(e);
        }

        public void mouseMoved(MouseEvent e) {
            redispatchToParent(e);
        }

        private boolean redispatchToParent(MouseEvent e) {
            //Todo: resize top
            if (e.getPoint().y < 5 && !dispatched) {
                dispatched = true;
                return true;
            } else if(e.getPoint().y >= 5) {
                dispatched = false;

            }
            return false;
        }

        public void addActionButton(JButton btn){
            actionsBtnPanel.add(btn);
        }
    }

}

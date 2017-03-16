package views.custom;

import javax.swing.*;
import java.awt.*;

public class MaterialIcoButton extends JButton {

    public MaterialIcoButton(ImageIcon icon){
        setBorderPainted(false);
        setContentAreaFilled(false);
        setBorder(BorderFactory.createEmptyBorder());
        setIcon(icon);
        setFocusPainted(false);
        setFocusable(false);
        setPreferredSize(new Dimension(56,56));
    }
}

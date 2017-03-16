package views.custom;

import views.constants.Colors;

import javax.swing.*;
import java.awt.*;

public class MaterialProgressBar extends JProgressBar{

    public MaterialProgressBar(){
        setBorderPainted(false);
        setBackground(Colors.ACCENT_LIGHT);
        setForeground(Colors.ACCENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        setPreferredSize(new Dimension(200, 5));
    }
}

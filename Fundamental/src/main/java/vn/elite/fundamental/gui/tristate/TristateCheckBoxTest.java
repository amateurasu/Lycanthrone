package vn.elite.fundamental.gui.tristate;

import javax.swing.*;
import java.awt.*;

public class TristateCheckBoxTest {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new JFrame("TristateCheckBoxTest");
        frame.setLayout(new GridLayout(0, 1, 15, 15));
        UIManager.LookAndFeelInfo[] lfs = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo lf : lfs) {
            System.out.println("Look&Feel " + lf.getName());
            UIManager.setLookAndFeel(lf.getClassName());
            frame.add(makePanel());
        }
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private static JPanel makePanel() {
        final TristateCheckBox tristateBox = new TristateCheckBox("Tristate checkbox");
        tristateBox.addItemListener(e -> {
            switch (tristateBox.getState()) {
                case SELECTED:
                    System.out.println("Selected");
                    break;
                case DESELECTED:
                    System.out.println("Not Selected");
                    break;
                case INDETERMINATE:
                    System.out.println("Tristate Selected");
                    break;
            }
        });
        tristateBox.addActionListener(System.out::println);
        final JCheckBox normalBox = new JCheckBox("Normal checkbox");
        normalBox.addActionListener(System.out::println);

        final JCheckBox enabledBox = new JCheckBox("Enable", true);
        enabledBox.addItemListener(e -> {
            tristateBox.setEnabled(enabledBox.isSelected());
            normalBox.setEnabled(enabledBox.isSelected());
        });

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel(UIManager.getLookAndFeel().getName()));
        panel.add(tristateBox);
        panel.add(normalBox);
        panel.add(enabledBox);
        return panel;
    }
}

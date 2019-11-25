package vn.elite.snatcher.test;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;

public class DemoJFileChooser extends JFrame {
    private JButton go = new JButton();

    public DemoJFileChooser() {
        setPreferredSize(new Dimension(200, 200));

        initComponents();
    }

    public static void main(String[] s) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        new DemoJFileChooser().setVisible(true);
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout());

        go.setText("Do it!");
        go.addActionListener(evt -> btnGoActionPerformed());
        getContentPane().add(go);
        pack();
    }

    private void btnGoActionPerformed() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // disable the "All files" option.
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
        } else {
            System.out.println("No Selection ");
        }
    }
}

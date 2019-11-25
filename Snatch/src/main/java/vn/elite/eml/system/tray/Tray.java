package vn.elite.eml.system.tray;

import javax.swing.*;
import java.awt.*;

public class Tray {

    public static void main(String[] args) throws Exception {
        new Tray();
    }

    private MenuItem action;
    private MenuItem exit;

    public Tray() throws Exception {
        if (SystemTray.isSupported()) {
            initComponents();
        } else {
            throw new Exception("System tray is not supported!");
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Initiating Components">
    private void initComponents() {
        SystemTray systemTray = SystemTray.getSystemTray();
        PopupMenu popupMenu = new PopupMenu();

        popupMenu.add(action = new MenuItem("Action"));
        popupMenu.addSeparator();
        popupMenu.add(exit = new MenuItem("Exit"));

        // setting tray icon
        Image image = Toolkit.getDefaultToolkit().getImage("src/emlprocess/SystemTray/M-Logo.png");
        TrayIcon trayIcon = new TrayIcon(image, "EML Processor", popupMenu);
        trayIcon.setImageAutoSize(true);

        try {
            systemTray.add(trayIcon);
            setEventListener();
        } catch (AWTException ignored) {
        }
    }

    private void setEventListener() {
        action.addActionListener(e -> JOptionPane.showMessageDialog(null, "Action Clicked "));
        exit.addActionListener(e -> System.exit(0));
    }
    //</editor-fold>
}

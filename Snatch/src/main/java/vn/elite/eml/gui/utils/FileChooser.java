package vn.elite.eml.gui.utils;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileChooser extends JDialog {

    public static Path showFileChooser(String title) {
        return showFileChooser(title, Paths.get("."));
    }

    public static Path showFileChooser(String title, Path path) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(path.toFile());
        chooser.setDialogTitle(title);

        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(true);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().toPath();
        } else {
            return null;
        }
    }

    public static Path showFolderChooser(String title) {
        return showFolderChooser(title, Paths.get("."));
    }

    public static Path showFolderChooser(String title, Path path) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(path.toFile());
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getCurrentDirectory().toPath();
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        FileChooser.showFileChooser("");
        FileChooser.showFileChooser("Golden Path", Paths.get("."));
        FileChooser.showFolderChooser("");
    }


}

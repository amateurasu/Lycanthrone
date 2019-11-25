package vn.elite.fundamental.java.fx;

import javafx.concurrent.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CopyTask extends Task<List<File>> {

    @Override
    protected List<File> call() throws Exception {
        File dir = new File("C:/Windows");
        File[] files = dir.listFiles();
        int count = files.length;

        List<File> copied = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                this.copy(file);
                copied.add(file);
            }

            this.updateProgress(i, count);
        }
        return copied;
    }

    private void copy(File file) throws Exception {
        this.updateMessage("Copying: " + file.getAbsolutePath());
        Thread.sleep(100);
    }
}

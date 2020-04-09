package vn.elite.snatcher.manga;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

@Slf4j
public class HttpDownload extends SwingWorker<Boolean, Void> {

    // private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36";
    private static HttpDownload instance = new HttpDownload();

    private boolean setTarget = false;
    private String target;

    private boolean setFile = false;
    private String file;

    private boolean setList = false;
    private List<String> list;

    private boolean setRange = false;
    private int min;
    private int max;

    private HttpDownload() {}

    public static HttpDownload getDownloader() {
        return instance;
    }

    private String pattern;

    public static String byteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }

        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public HttpDownload setFile(String file) {
        setFile = true;
        this.file = file;
        return this;
    }

    public HttpDownload setRange(String pattern, int min, int max) {
        setRange = true;
        this.pattern = pattern;
        this.min = min;
        this.max = max;
        return this;
    }

    public HttpDownload setList(List<String> list) {
        setList = true;
        this.list = list;
        return this;
    }

    public HttpDownload setTarget(String target) {
        File file = new File(target);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        setTarget = true;
        this.target = target;
        return this;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        log.info("Starting...");
        if (!setTarget) {
            throw new IOException("Target folder is not set!");
        }

        if (setFile) {
            return downloadByFile();
        }

        if (setList) {
            log.info("downloadByList()");
            return downloadByList();
        }

        if (setRange) {
            log.info("downloadByRange()");
            return downloadByRange();
        }
        return null;
    }

    private Boolean downloadByFile() {
        try {
            List<String> links = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
            setList(links);
            return downloadByList();
        } catch (IOException e) {
            return false;
        }
    }

    private boolean downloadByRange() {
        for (int i = min; i <= max; i++) {
            try {
                String link = String.format(pattern, i);
                log.info(link);
                download(link, target);
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
        return true;
    }

    private boolean downloadByList() {
        for (String link : list) {
            try {
                download(link, target);
            } catch (IOException ignored) { }
        }
        return true;
    }

    private static void download(String link, String path) throws IOException {
        URL url = new URL(link);
        val con = url.openConnection();
        con.setRequestProperty("User-Agent", USER_AGENT);
        int contentLength = con.getContentLength();

        val filename = Paths.get(url.getPath()).getFileName().toString();
        log.info("File {} -> contentLength = {} bytes = {}", filename, contentLength, byteCount(contentLength, false));
        File file = new File(path, filename);
        if (file.exists()) {
            log.info("File '{}' has already existed!", file);
            return;
        }
        try (val inputStream = con.getInputStream(); val outputStream = new FileOutputStream(file)) {
            val buffer = new byte[2048];
            int length;
            int downloaded = 0;
            var oldPercent = 0_000d;
            // Looping until server finishes
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                downloaded += length;
                double percent = downloaded * 100.0 / contentLength;
                if (percent - oldPercent >= 5 || percent == 100) {
                    System.out.format("Download Status: %6.2f%%%n", percent);
                    oldPercent = percent;
                }
            }
        }
    }

    private void download() throws Exception {
        doInBackground();
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        // String s = "https://cdn.hentai.cafe/manga/content/comics/sameda-koban-offline-banger_5cd58f565e371/1-0_5cd58f6327146/%02d.jpg";
        // String mangaName = "[Sameda Koban] Offline Banger";
        // String target = "E:/Pictures/697d0f2554859c3e95577f0d9e9373e7/_Manga/@New/";
        // HttpDownload.getDownloader().setRange(s, 19, 24).setTarget(target + mangaName).doInBackground();

        String folder = "E:\\Pictures\\M Onna Senka";
        HttpDownload.getDownloader().setRange("https://static.hentaicdn.com/hentai/2222/1/M_Onna_Senka_%03d.jpg", 1, 211).setTarget(folder).download();
        Duration duration = Duration.ofMillis(System.currentTimeMillis() - start);
        log.info("Done downloading in {} ms\n", duration);
    }
}

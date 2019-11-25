package vn.elite.snatcher.manga;

import lombok.val;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import vn.elite.core.config.Ini;
import vn.elite.snatcher.examples.ImageInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jsoup.Jsoup.parse;

public class Parser {
    private static String baseUri;
    private static final Pattern url = Pattern.compile("(?:(https?|ftp)://)?\\w+([\\-.]\\w+)*\\.[a-z0-9]{2,5}(:[0-9]{1,5})?(/.*)?");

    public static void main(String[] args) {
        try {
            String path = Parser.class.getResource("resources.ini").getPath();
            System.out.println(path);
            Ini ini = new Ini(path);
            URL resource = Parser.class.getResource("Kissmanga.html");
            File file = new File(resource.getFile());
            Document km = parse(file, "UTF-8");
            Node node = km.childNode(1);
            Matcher matcher = url.matcher(node.attr("#comment"));
            if (matcher.find()) {
                URL link = new URL(matcher.group(0));
                String host = link.getHost().toUpperCase();
                System.out.println(host);
                if (ini.contains(host)) {
                    System.out.println(ini.get(host));
                    // process(km, ini, host);
                }
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    public static void main(String[] args) {
    //        try {
    //            String base = "E:\\Manga\\Dead Tube\\";
    //            baseUri = "http://kissmanga.com/";
    //            traverseDir(base);
    //        } catch (IOException ignored) {
    //        }
    //    }

    private static void traverseDir(String base) throws IOException {
        String[] directories = getChildDirs(base);

        if (!valid(directories)) {
            return;
        }

        for (String directory : directories) {
            processDir(base + directory, "");
        }
    }

    private static String[] getChildDirs(String dir) {
        return new File(dir).list((current, name) -> new File(current, name).isDirectory());
    }

    private static void processDir(String directory, String selector) throws IOException {
        val currentFolder = directory + File.separator;
        val htmlFiles = getHtmlFiles(currentFolder);
        if (htmlFiles == null) return;

        for (File file : htmlFiles) {
            parseFile(currentFolder, file, selector);
            cleanGarbage(currentFolder, file);
        }
    }

    private static File[] getHtmlFiles(String base) {
        File f = new File(base);
        File[] matchingFiles = f.listFiles((dir, name) -> name.endsWith(".HTML"));
        return valid(matchingFiles) ? matchingFiles : null;
    }

    private static void parseFile(String currentFolder, File htmlFile, String selector) {
        try {
            Document document = parse(htmlFile, "UTF-8", baseUri);

            Elements elements = document.select(selector);
            int i = 0;
            for (Element e : elements) {
                File file = new File(currentFolder + e.attr("src").substring(1));
                System.out.println(file.getPath());
                System.out.println(file.isFile());
                if (file.isFile()) {
                    ImageInfo info = ImageInfo.analyze(file);
                    String newName = currentFolder + String.format("%03d", i) + info.extension();
                    rename(file, newName);
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void cleanGarbage(String currentFolder, File htmlFile) throws IOException {
        remove(htmlFile);
        String[] directories = getChildDirs(currentFolder);
        for (String dir : directories) {
            remove(new File(currentFolder + dir));
        }
    }

    private static void remove(File dir) throws IOException {
        if (dir == null) return;

        if (!dir.isDirectory()) {
            Files.deleteIfExists(dir.toPath());
            return;
        }

        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                remove(file);
            }
        }
        Files.deleteIfExists(dir.toPath());
    }

    private static void rename(File file, String newName) {
        File newFile = new File(newName);
        if (file.renameTo(newFile)) {
            System.out.printf("Renamed to %s%n", newName);
        } else {
            System.out.printf("Failed to rename to %s%n", newName);
        }
    }

    private static <T> boolean valid(T[] array) {
        return array != null && array.length > 0;
    }
}


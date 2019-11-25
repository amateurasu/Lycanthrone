package vn.elite.snatcher.examples;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

import static org.jsoup.Jsoup.parse;
import static org.jsoup.parser.Parser.xmlParser;

public class FixCli {
    private static int counter;

    public static void main(String[] args) {
        counter = 0;

        File listFolder = new File("D:\\duclm\\OAM CLI\\cli\\cli\\list");
        File[] folders = listFolder.listFiles();
        if (folders == null) {
            return;
        }

        for (File folder : folders) {
            if (folder == null) {
                return;
            }
            filter(folder);
        }

        System.out.println(counter);
    }

    private static void filter(File folder) {
        System.out.println(folder.getAbsolutePath());
        File[] files = folder.listFiles(f -> f.isFile() && f.getName().endsWith(".xml"));
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file == null) {
                return;
            }
            process(file);
        }
    }

    private static void process(File file) {
        try {
            Document document = parse(new FileInputStream(file), "UTF-8", "", xmlParser());
            Element select = document.select("para[index=1][name=CellId]").get(0);
            select.appendElement("list")
                .attr("name", "3")
                .attr("value", "3")
                .attr("description", "3");
            select.appendElement("list")
                .attr("name", "4")
                .attr("value", "4")
                .attr("description", "4");
            select.appendElement("list")
                .attr("name", "5")
                .attr("value", "5")
                .attr("description", "5");

            save(file, document.toString());
            counter++;
        } catch (Exception ignored) {
        }
    }

    Elements $(Document document, String s) {
        return document.select(s);
    }

    private static void save(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }
}

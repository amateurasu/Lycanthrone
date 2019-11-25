package vn.elite.snatcher.examples;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Scanner;

import static org.jsoup.Jsoup.connect;

public class EH {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print(">>> Link: ");
            String url = scanner.nextLine();
            Document document = connect(url).get();
            Elements select = document.select("div.gdtm>div>a");
            for (Element element : select) {
                String link = element.attr("href");
                System.out.println("Link: " + link);
                Document doc = connect(link).get();
                String src = doc.select("img#img").get(0).attr("src");
                System.out.println("Download: " + src);

                //                byte[] in = connect(src).get();
                //
                //                String name = src.substring(src.lastIndexOf("/"));
                //                String path = "E:\\Pictures\\697d0f2554859c3e95577f0d9e9373e7\\_Chinese Collection\\New folder";
                //                File file = new File(path + name);
                //
                //                FileUtils.save(in, file);
                //                System.out.println(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

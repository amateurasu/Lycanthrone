package vn.elite.core.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Annotation {
    public static void main(String[] args) {
        System.out.println("asdasdawdawd");
        URL[] urls = getUrlsForCurrentClasspath();
        Stream.of(urls).forEach(System.out::println);

        Runtime runtime = Runtime.getRuntime();
        long l = runtime.freeMemory();
        System.out.println(l);
        int processors = runtime.availableProcessors();
        System.out.println(processors);
    }

    private static URL[] getUrlsForCurrentClasspath() {
        List<URL> list = new ArrayList<>();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        while (loader != null) {
            if (loader instanceof URLClassLoader) {
                URL[] urlArray = ((URLClassLoader) loader).getURLs();
                List<URL> urlList = Arrays.asList(urlArray);
                list.addAll(urlList);
            }
            loader = loader.getParent();
        }
        return list.toArray(new URL[0]);
    }
}

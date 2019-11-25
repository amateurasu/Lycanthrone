package vn.elite.fundamental.xtel.phone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class _HashMap {

    private static String filename = "resource/phonefilter/origin.demo.txt";

    public static void main(String[] args) {
        try {
            System.out.println("General: ");
            general();
            System.out.println("\nHash:");
            hash();
            System.out.println("\nList");
            list();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void hash() throws IOException, InterruptedException {
        HashMap<Key, String> hashMap = new HashMap<>();
        HashMap<String, String> hashMap2 = new HashMap<>();

        long start;
        // ==================== de day cho chay trươc
        System.gc();
        start = System.currentTimeMillis();
        Files.lines(Paths.get(filename)).forEach(x -> hashMap2.put(x, ""));
        System.out.println("Put all object to hashMapBegin : " + (System.currentTimeMillis() - start) + " ms");

        Thread.sleep(1000);
        System.gc();
        start = System.currentTimeMillis();
        Files.lines(Paths.get(filename)).forEach(x -> hashMap.put(new Key(x), ""));
        System.out.println("Put all object to hashMap1 : " + (System.currentTimeMillis() - start) + " ms");

        Thread.sleep(1000);
        System.gc();
        start = System.currentTimeMillis();
        Files.lines(Paths.get(filename)).forEach(x -> hashMap2.put(x, ""));
        System.out.println("Put all object to hashMap2 : " + (System.currentTimeMillis() - start) + " ms");
        System.out.println(hashMap.size());
    }

    private static void list() throws IOException, InterruptedException {
        HashMap<String, String> hashMap2 = new HashMap<>();
        List<String> list = new ArrayList<>();
        List<Key> list1 = new ArrayList<>();

        long start;
        // ==================== de day cho chay trươc
        System.gc();
        start = System.currentTimeMillis();
        Files.lines(Paths.get(filename)).forEach(x -> hashMap2.put(x, ""));
        System.out.println("Put all object to hashMapBegin : " + (System.currentTimeMillis() - start) + " ms");

        Thread.sleep(1000);
        System.gc();
        start = System.currentTimeMillis();

        Files.lines(Paths.get(filename)).forEach(list::add);

        System.out.println("Put all object to ArrayList : " + (System.currentTimeMillis() - start) + " ms");

        Thread.sleep(1000);
        System.gc();
        start = System.currentTimeMillis();
        Files.lines(Paths.get(filename)).forEach(x -> list1.add(new Key(x)));
        System.out.println("Put all object to ArrayList1 : " + (System.currentTimeMillis() - start) + " ms");
    }

    private static void general() {
        HashMap<Key, String> hashMap = new HashMap<>();
        HashMap<String, String> hashMap2 = new HashMap<>();
        List<String> list = new ArrayList<>();
        List<Key> list1 = new ArrayList<>();
        TreeSet<String> treeSet = new TreeSet<>();
        Set<String> linkedHashSet = new HashSet<>();

        long start;
        try {
            // ==================== de day cho chay trươc
            System.gc();
            start = System.currentTimeMillis();
            Files.lines(Paths.get(filename)).forEach(x -> hashMap2.put(x, ""));
            System.out.println("Put all object to hashMapBegin : " + (System.currentTimeMillis() - start) + " ms");

            //Thread.sleep(1000);
            //            System.gc();
            //            start = System.currentTimeMillis();
            //            Files.lines(Paths.get(filename)).forEach(x -> hashMap1.put(new Key1(x), ""));
            //            System.out.println("Put all object to hashMap1 : " + (System.currentTimeMillis() - start) + " ms");
            //
            //            Thread.sleep(1000);
            //            System.gc();
            //            start = System.currentTimeMillis();
            //            Files.lines(Paths.get(filename)).forEach(x -> hashMap1.put(new Key1(x), ""));
            //            System.out.println("Put all object to hashMap1 : " + (System.currentTimeMillis() - start) + " ms");

            Thread.sleep(1000);
            System.gc();
            start = System.currentTimeMillis();
            Files.lines(Paths.get(filename)).forEach(x -> hashMap2.put(x, ""));
            System.out.println("Put all object to hashMap2 : " + (System.currentTimeMillis() - start) + " ms");

            Thread.sleep(1000);
            System.gc();
            start = System.currentTimeMillis();
            Files.lines(Paths.get(filename)).forEach(list::add);
            System.out.println("Put all object to ArrayList : " + (System.currentTimeMillis() - start) + " ms");

            Thread.sleep(1000);
            System.gc();
            start = System.currentTimeMillis();
            Files.lines(Paths.get(filename)).forEach(x -> list1.add(new Key(x)));
            System.out.println("Put all object to ArrayList1 : " + (System.currentTimeMillis() - start) + " ms");

            Thread.sleep(1000);
            System.gc();
            start = System.currentTimeMillis();
            Files.lines(Paths.get(filename)).forEach(treeSet::add);
            System.out.println("Put all object to TreeSet : " + (System.currentTimeMillis() - start) + " ms");

            Thread.sleep(1000);
            System.gc();
            start = System.currentTimeMillis();
            Files.lines(Paths.get(filename)).forEach(linkedHashSet::add);
            System.out.println("Put all object to LinkedHashSet : " + (System.currentTimeMillis() - start) + " ms");

            // ======================================== equal =================================================

            //            Thread.sleep(1000);
            //            System.gc();
            //            start = System.currentTimeMillis();
            //            hashMap.containsKey(new Key("84899344697"));
            //            System.out.println("Contains object to hashMap : " + (System.currentTimeMillis() - start) + " ms");
            //
            //            Thread.sleep(1000);
            //            System.gc();
            //            start = System.currentTimeMillis();
            //            hashMap1.containsKey(new Key1("84899344697"));
            //            System.out.println("Contains object to hashMap1 : " + (System.currentTimeMillis() - start) + " ms");

            Thread.sleep(1000);
            System.gc();
            start = System.currentTimeMillis();
            hashMap2.containsKey("84899344697");
            System.out.println("Contains object to hashMap2 : " + (System.currentTimeMillis() - start) + " ms");

            Thread.sleep(1000);
            System.gc();
            start = System.currentTimeMillis();
            list.contains("84899344697");
            System.out.println("Contains object to list : " + (System.currentTimeMillis() - start) + " ms");

            Thread.sleep(1000);
            System.gc();
            start = System.currentTimeMillis();
            list1.contains(new Key("84899344697"));
            System.out.println("Contains object to list1 : " + (System.currentTimeMillis() - start) + " ms");

            Thread.sleep(1000);
            System.gc();
            start = System.currentTimeMillis();
            treeSet.contains("84899344697");
            System.out.println("Contains object to treeSet : " + (System.currentTimeMillis() - start) + " ms");

            Thread.sleep(1000);
            System.gc();
            start = System.currentTimeMillis();
            linkedHashSet.contains("84899344697");
            System.out.println("Contains object to linkedHashSet : " + (System.currentTimeMillis() - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Key {
        private String key;

        public Key(String key) {
            this.key = key;
        }

        public Key() {
        }

        @Override
        public boolean equals(Object obj) {
            return ((Key) obj).key.equals(this.key);
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }
}

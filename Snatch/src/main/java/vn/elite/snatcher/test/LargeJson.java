//package vn.elite.snatcher.test;
//
//import vn.elite.core.utils.FileUtils;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.concurrent.atomic.AtomicReference;
//
//public class LargeJson {
//    public static void main(String[] args) {
//
//        AtomicReference<JsonElement> parse = new AtomicReference<>();
//        benmark(() -> {
//            try {
//                parse.set(JSON.parse(new File("D:\\Projects\\citylots.json")));
//            } catch (ParseException | IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        AtomicReference<String> str = new AtomicReference<>();
//        benmark(() -> str.set(parse.get().toString()));
//
//        benmark(() -> {
//            try {
//                FileUtils.saveLargeFile(str.get(), "D:\\saved.json");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    private static void benmark(Runnable r) {
//        long l = System.currentTimeMillis();
//        new Thread(r).start();
//        long l1 = System.currentTimeMillis();
//        System.out.println(l1 - l);
//    }
//}

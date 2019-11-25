//package vn.elite.snatcher.examples;
//
//import com.elite.markup.json.JSON;
//import com.elite.markup.json.JsonArray;
//import com.elite.markup.json.parser.JsonElement;
//
//import java.io.File;
//
//public class JsonFacebook {
//    public static void main(String[] args) throws Exception {
//        // String endpoint = "https://graph.facebook.com/";
//        // String user = "240819332596343";
//        // String accessToken = "";
//        // String url = String.format("%s%s/albums?access_token=%s", endpoint, user, accessToken);
//        // HttpConnection.defaultProxy("10.61.60.2", 8888);
//        // Response response = Markup.connect(url).ignoreHttpErrors(true).get();
//        // JsonElement json = JSON.parse(response.body());
//        // System.out.println(json.get("paging").get("next"));
//        //
//        // String filename = Paths.get(new URL(url).getPath()).getFileName().toString();
//        // System.out.println(filename);
//        //
//        // System.out.println("Done!");
//
//        final String address = "D:\\Projects\\_Miscellaneous\\albums.json";
//        JsonElement json = JSON.parse(new File(address));
//        JsonArray data = (JsonArray) json.get("data");
//        int count = 0;
//        final Integer[] count2 = {0};
//        for (Object datum : data) {
//            JsonArray album = (JsonArray) ((JsonElement) datum).get("photos").get("data");
////            get("images").get(1).get("source")
//            count += album.size();
//            album.forEach(image -> {
//                count2[0]++;
//                try {
//                    System.out.println(image.get("images").get(0).get("source"));
//                } catch (Exception e) {
//                }
//            });
//        }
//        System.out.println(count + " " + count2[0]);
//    }
//}

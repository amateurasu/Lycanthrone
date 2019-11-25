//package vn.elite.snatcher.examples;
//
//import com.elite.markup.Markup;
//import com.elite.markup.json.JSON;
//import com.elite.markup.json.JsonArray;
//import com.elite.markup.json.JsonValue;
//import vn.elite.snatcher.config.Config;
//
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//public class Imgur {
//    private static LinkedHashMap<String, String> config = Config.getInstance().getSection("IMGUR");
//
//    public static List<String> getLinks(String link) throws Exception {
//        String hash = getHash(link);
//        String imgur = Markup.connect(String.format("https://api.imgur.com/3/album/%s/images", hash))
//            .header("Authorization", "Client-ID " + config.get("clientId"))
//            .get().body();
//
//        JsonArray data = (JsonArray) JSON.parse(imgur).get("data");
//        return data.stream().map(datum -> ((JsonValue) datum.get("link")).getValue().toString())
//            .collect(Collectors.toList());
//    }
//
//    private static String getHash(String link) throws Exception {
//        Pattern album = Pattern.compile(config.get("gallery"));
//        Matcher matcher = album.matcher(link);
//        if (matcher.matches()) {
//            return matcher.group(1);
//        } else {
//            throw new Exception("Cannot get hash from this link!");
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            String link = "https://imgur.com/a/dzdpo";
//            List<String> list = getLinks(link);
//            System.out.println(list.size());
//            list.forEach(System.out::println);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}

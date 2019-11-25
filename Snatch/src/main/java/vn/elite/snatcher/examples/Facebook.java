// package vn.elite.snatcher.examples;
//
// import com.elite.markup.Markup;
// import com.elite.markup.connections.Method;
// import com.elite.markup.connections.Response;
// import com.elite.markup.connections.http.HttpConnection;
// import com.elite.markup.json.JSON;
// import com.elite.markup.json.JsonArray;
// import com.elite.markup.json.JsonValue;
// import com.elite.markup.json.parser.JsonElement;
// import com.elite.markup.utils.StringUtils;
// import com.elite.markup.xml.nodes.Document;
// import lombok.extern.slf4j.Slf4j;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import java.io.IOException;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
//
// @Slf4j
// public class Facebook {
//    //    private static final String token = "1342765882483152|IhhknTclRSMvi-QgSUNhScP_aaY";
//    private static final String endpoint = "https://graph.facebook.com/";
//    private static final String token = "EAACW5Fg5N2IBAJfVdprWTcD0eX02YrfWNn6VH3aJdX2PPLbixUcbahlm8BXsClMbmZAkrQqrToqYb" +
//            "kkphZBAiGETa2vxWgcoFhhjMm60kc0Wl7I59stD5RGsvZBEMMQGtMRgJAtfjKORxczHq4ft4qGy1GQnZBrOLPI6aO1mNE43lbIOEWq6fQ1UnsN6z98ZD";
//
//    public static void main(String[] args) throws IOException, ParseException {
//        checkProxy("https://www.facebook.com", "10.61.60.2", 8888);
//
//        String id = getID("https://www.facebook.com/AzaMiyuko.Fanpage");
// //        String id = getID("https://www.facebook.com/DrLemis");
//        log.info("ID = {}", id);
//        getImages(id);
//    }
//
//    private static void checkProxy(String url, String host, int port) {
//        try {
//            Response response = Markup.connect(url).method(Method.HEAD).execute();
//            Map<String, String> headers = response.headers();
//            headers.forEach((k, v) -> logger.info("{} -> {}", k, v));
//            logger.info("No need proxy");
//        } catch (Exception e) {
//            logger.info("Change proxy to {}:{}", host, port);
//            HttpConnection.defaultProxy(host, port);
//        }
//    }
//
//    private static String getID(String url) throws IOException, ParseException {
//        try {
//            Document fb = Markup.connect(url).get().parse();
//            String attr = fb.select("div[id='pagelet_timeline_main_column']").attr("data-gt");
//            JsonValue owner = (JsonValue) JSON.parse(attr).get("profile_owner");
//            System.out.println(owner.getValue().getClass());
//            return owner.toString();
//        } catch (Exception e) {
//            Matcher matcher = Pattern.compile("https://www\\.facebook\\.com/(.+)/?").matcher(url);
//            if (!matcher.matches()) return "";
//
//            String page = matcher.group(1);
//            String s = String.format("%sv3.0/%s?access_token=%s", endpoint, page, token);
//            HashMap map;
//
//            return JSON.parse(Markup.connect(s).get().body()).get("id").toString();
//        }
//    }
//
//    private static void getImages(String id) throws IOException, ParseException {
//        JsonElement json;
//        logger.info("Downloading...");
//        // AzaMiyuko.Fanpage
//
//        String fields = "albums{name,count,photos{images}}";
//        String addr = String.format("https://graph.facebook.com/%s?fields=%s&access_token=%s", id, fields, token);
//        String content = Markup.connect(addr).get().body();
//        json = JSON.parse(content);
//
//        processJson(json);
//    }
//
//    private static void processJson(JsonElement json) throws IOException, ParseException {
//        JsonArray albums = (JsonArray) json.get("albums").get("data");
//        int sum = 0;
//        for (JsonElement album : albums) {
//            JsonElement photos = album.get("photos");
//            JsonArray photoData = (JsonArray) photos.get("data");
//
//            int current = photoData.size();
//            long count = (long) ((JsonValue) album.get("count")).getValue();
//            String name = StringUtils.unescape(album.get("name").toString());
//
//            logger.info(String.format("%40s - %4d - %4d", name, current, count));
//
//            if (current < count) {
//                getNext(photos);
//            }
// //            photoData.forEach(photo -> {
// //                try {
// //                    logger.info(photo.get("images").get(0).get("source"));
// //                } catch (Exception e) {
// //                }
// //            });
//            sum += count;
//        }
//        logger.info("SUM: {}", sum);
//    }
//
//    private static void processSubJson(JsonElement json) throws IOException, ParseException {
//        JsonArray data = (JsonArray) json.get("data");
//        logger.info(">>> {}", data.size());
//        if (json.get("paging").contains("next")) {
//            getNext(json);
//        }
// //        data.forEach(datum -> {
// //            datum.get("images").get(0).get("source");
// //        });
//    }
//
//    private static void getNext(JsonElement photos) throws IOException, ParseException {
//        String next = StringUtils.unescape(photos.get("paging").get("next").toString());
//        logger.info("--> {}", next);
//        JsonElement content = JSON.parse(Markup.connect(next).get().body());
//        processSubJson(content);
//    }
// }

package vn.elite.core.json;

import org.junit.Test;
import vn.elite.core.json.parser.ContainerFactory;
import vn.elite.core.json.parser.ContentHandler;
import vn.elite.core.json.parser.JSONParser;
import vn.elite.core.json.parser.ParseException;
import vn.elite.core.utils.StringUtils;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author FangYidong<fangyidong @ yahoo.com.cn>
 */
public class JsonTest {

    @Test
    public void testDecode() {
        System.out.println("=======decode=======");

        String s = "[0,{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]";
        Object obj = JSONValue.parse(s);
        JSONArray array = (JSONArray) obj;
        System.out.println("======the 2nd element of array======");
        System.out.println(array.get(1));
        System.out.println();
        assertEquals("{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}", array.get(1).toString());

        JSONObject obj2 = (JSONObject) array.get(1);
        System.out.println("======field \"1\"==========");
        System.out.println(obj2.get("1"));
        assertEquals("{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}", obj2.get("1").toString());

        s = "{}";
        obj = JSONValue.parse(s);
        assertEquals("{}", obj.toString());

        s = "[5,]";
        obj = JSONValue.parse(s);
        assertEquals("[5]", obj.toString());

        s = "[5,,2]";
        obj = JSONValue.parse(s);
        assertEquals("[5,2]", obj.toString());

        s = "[\"hello\\bworld\\\"abc\\tdef\\\\ghi\\rjkl\\n123" + StringUtils.escape("中") + "\"]";
        obj = JSONValue.parse(s);
        assertEquals("hello\bworld\"abc\tdef\\ghi\rjkl\n123中", ((List) obj).get(0).toString());

        JSONParser parser = new JSONParser();
        s = "{\"name\":";
        try {
            parser.parse(s);
        } catch (ParseException pe) {
            assertEquals(ParseException.ERROR_UNEXPECTED_TOKEN, pe.getErrorType());
            assertEquals(8, pe.getPosition());
        }

        s = "{\"name\":}";
        try {
            parser.parse(s);
        } catch (ParseException pe) {
            assertEquals(ParseException.ERROR_UNEXPECTED_TOKEN, pe.getErrorType());
            assertEquals(8, pe.getPosition());
        }

        s = "{\"name";
        try {
            obj = parser.parse(s);
        } catch (ParseException pe) {
            assertEquals(ParseException.ERROR_UNEXPECTED_TOKEN, pe.getErrorType());
            assertEquals(6, pe.getPosition());
        }

        s = "[[null, 123.45, \"a\\\tb c\"}, true]";
        try {
            parser.parse(s);
        } catch (ParseException pe) {
            assertEquals(24, pe.getPosition());
            System.out.println("Error at character position: " + pe.getPosition());
            switch (pe.getErrorType()) {
                case ParseException.ERROR_UNEXPECTED_TOKEN:
                    System.out.println("Unexpected token: " + pe.getUnexpectedObject());
                    break;
                case ParseException.ERROR_UNEXPECTED_CHAR:
                    System.out.println("Unexpected character: " + pe.getUnexpectedObject());
                    break;
                case ParseException.ERROR_UNEXPECTED_EXCEPTION:
                    ((Exception) pe.getUnexpectedObject()).printStackTrace();
                    break;
            }
        }

        s = "{\"first\": 123, \"second\": [4, 5, 6], \"third\": 789}";
        ContainerFactory containerFactory = new class KeyFinder implements ContentHandler {
            private Object value;
            private boolean found = false;
            private boolean end = false;
            private String key;
            private String matchKey;

            public void setMatchKey(String matchKey) {
                this.matchKey = matchKey;
            }

            public Object getValue() {
                return value;
            }

            public boolean isEnd() {
                return end;
            }

            public void setFound(boolean found) {
                this.found = found;
            }

            public boolean isFound() {
                return found;
            }

            public void startJSON() {
                found = false;
                end = false;
            }

            public void endJSON() {
                end = true;
            }

            @Override
            public boolean primitive(Object value) {
                if (key != null) {
                    if (key.equals(matchKey)) {
                        found = true;
                        this.value = value;
                        key = null;
                        return false;
                    }
                }
                return true;
            }

            public boolean startArray() {
                return true;
            }

            public boolean startObject() {
                return true;
            }

            public boolean startObjectEntry(String key) {
                this.key = key;
                return true;
            }

            public boolean endArray() {
                return false;
            }

            public boolean endObject() {
                return true;
            }

            public boolean endObjectEntry() {
                return true;
            }
        }
        ;

        try {
            Map json = (Map) parser.parse(s, containerFactory);
            Iterator iter = json.entrySet().iterator();
            System.out.println("==iterate result==");
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                System.out.println(entry.getKey() + "=>" + entry.getValue());
            }

            System.out.println("==toJSONString()==");
            System.out.println(JSONValue.toJSONString(json));
            assertEquals("{\"first\":123,\"second\":[4,5,6],\"third\":789}", JSONValue.toJSONString(json));
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        s = "{\"first\": 123, \"second\": [{\"s1\":{\"s11\":\"v11\"}}, 4, 5, 6], \"third\": 789}";
        ContentHandler myHandler = new ContainerFactory() {
            public List createArrayContainer() {
                return new LinkedList();
            }

            public Map createObjectContainer() {
                return new LinkedHashMap();
            }

        };
        try {
            parser.parse(s, myHandler);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        ContentHandler() {

            public boolean endArray () {
                System.out.println("endArray()");
                return true;
            }

            public void endJSON () {
                System.out.println("endJSON()");
            }

            public boolean endObject () {
                System.out.println("endObject()");
                return true;
            }

            public boolean endObjectEntry () {
                System.out.println("endObjectEntry()");
                return true;
            }

            @Override
            public boolean primitive (Object value){
                System.out.println("primitive(): " + value);
                return true;
            }

            public boolean startArray () {
                System.out.println("startArray()");
                return true;
            }

            public void startJSON () {
                System.out.println("startJSON()");
            }

            public boolean startObject () {
                System.out.println("startObject()");
                return true;
            }

            public boolean startObjectEntry (String key){
                System.out.println("startObjectEntry(), key:" + key);
                return true;
            }

        }

        s = "{\"first\": 123, \"second\": [{\"k1\":{\"id\":\"id1\"}}, 4, 5, 6, {\"id\": 123}], \"third\": 789, \"id\": null}";
        parser.reset();
        KeyFinder keyFinder = new KeyFinder();
        keyFinder.setMatchKey("id");
        int i = 0;
        try {
            while (!keyFinder.isEnd()) {
                parser.parse(s, keyFinder, true);
                if (keyFinder.isFound()) {
                    i++;
                    keyFinder.setFound(false);
                    System.out.println("found id:");
                    System.out.println(keyFinder.getValue());
                    if (i == 1)
                        assertEquals("id1", keyFinder.getValue());
                    if (i == 2) {
                        assertTrue(keyFinder.getValue() instanceof Number);
                        assertEquals("123", String.valueOf(keyFinder.getValue()));
                    }
                    if (i == 3)
                        assertNull(keyFinder.getValue());
                }
            }
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    @Test
    public void testEncode() throws Exception {
        System.out.println("=======encode=======");

        JSONArray array1 = new JSONArray();
        array1.add("abc\u0010a/");
        array1.add(123);
        array1.add(222.123);
        array1.add(Boolean.TRUE);
        System.out.println("======array1==========");
        System.out.println(array1);
        System.out.println();
        assertEquals("[\"abc\\u0010a\\/\",123,222.123,true]", array1.toString());

        JSONObject obj1 = new JSONObject();
        obj1.put("name", "fang");
        obj1.put("age", 27);
        obj1.put("is_developer", Boolean.TRUE);
        obj1.put("weight", 60.21);
        obj1.put("array1", array1);
        System.out.println("======obj1 with array1===========");
        System.out.println(obj1);
        System.out.println();
        assertEquals("{\"array1\":[\"abc\\u0010a\\/\",123,222.123,true],\"weight\":60.21,\"age\":27,\"name\":\"fang\",\"is_developer\":true}", obj1.toString());

        obj1.remove("array1");
        array1.add(obj1);
        System.out.println("======array1 with obj1========");
        System.out.println(array1);
        System.out.println();
        assertEquals("[\"abc\\u0010a\\/\",123,222.123,true,{\"weight\":60.21,\"age\":27,\"name\":\"fang\",\"is_developer\":true}]", array1.toString());

        List<Serializable> list = new ArrayList<>();
        list.add("abc\u0010a/");
        list.add(123);
        list.add(222.123);
        list.add(Boolean.TRUE);
        list.add(null);
        System.out.println("======list==========");
        System.out.println(JSONArray.toJSONString(list));
        System.out.println();
        assertEquals("[\"abc\\u0010a\\/\",123,222.123,true,null]", JSONArray.toJSONString(list));

        Map<String, Object> map = new HashMap<>();
        map.put("name", "fang");
        map.put("age", 27);
        map.put("is_developer", Boolean.TRUE);
        map.put("weight", 60.21);
        map.put("array1", list);
        System.out.println("======map with list===========");
        System.out.println(map);
        System.out.println();
        assertEquals("{\"array1\":[\"abc\\u0010a\\/\",123,222.123,true,null],\"weight\":60.21,\"age\":27,\"name\":\"fang\",\"is_developer\":true}", JSONObject.toJSONString(map));

        Map<String, Object> m1 = new LinkedHashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        List<Map<String, Object>> l1 = new LinkedList<>();

        m1.put("k11", "v11");
        m1.put("k12", "v12");
        m1.put("k13", "v13");
        m2.put("k21", "v21");
        m2.put("k22", "v22");
        m2.put("k23", "v23");
        l1.add(m1);
        l1.add(m2);
        String jsonString = JSONValue.toJSONString(l1);
        System.out.println(jsonString);
        assertEquals("[{\"k11\":\"v11\",\"k12\":\"v12\",\"k13\":\"v13\"},{\"k22\":\"v22\",\"k21\":\"v21\",\"k23\":\"v23\"}]", jsonString);

        StringWriter out = new StringWriter();
        JSONValue.writeJSONString(l1, out);
        jsonString = out.toString();
        System.out.println(jsonString);
        assertEquals("[{\"k11\":\"v11\",\"k12\":\"v12\",\"k13\":\"v13\"},{\"k22\":\"v22\",\"k21\":\"v21\",\"k23\":\"v23\"}]", jsonString);

        List<Serializable> l2 = new LinkedList<>();
        Map<String, Object> m3 = new LinkedHashMap<>();
        m3.put("k31", "v3");
        m3.put("k32", 123.45);
        m3.put("k33", Boolean.FALSE);
        m3.put("k34", null);
        l2.add("vvv");
        l2.add("1.23456789123456789");
        l2.add(Boolean.TRUE);
        l2.add(null);
        m3.put("k35", l2);
        m1.put("k14", m3);
        out = new StringWriter();
        JSONValue.writeJSONString(l1, out);
        jsonString = out.toString();
        System.out.println(jsonString);
        assertEquals("[{\"k11\":\"v11\",\"k12\":\"v12\",\"k13\":\"v13\",\"k14\":{\"k31\":\"v3\",\"k32\":123.45,\"k33\":false,\"k34\":null,\"k35\":[\"vvv\",\"1.23456789123456789\",true,null]}},{\"k22\":\"v22\",\"k21\":\"v21\",\"k23\":\"v23\"}]", jsonString);
    }
}

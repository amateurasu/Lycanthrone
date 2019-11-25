package vn.elite.fundamental.java.js;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NashornDemo {

    public static void main(String[] args) {
        try {
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");

            String name = "Mahesh";

            try {
                nashorn.eval("print('" + name + "')");
                System.out.println(nashorn.eval("10 + 2"));
            } catch (ScriptException e) {
                System.out.println("Error executing script: " + e.getMessage());
            }

            Path path = Paths.get("Kotorin/src/main/resources/test.js");
            System.out.println(path.toAbsolutePath());
            nashorn.eval(Files.newBufferedReader(path, StandardCharsets.UTF_8));

            Invocable inv = (Invocable) nashorn;
            // call function from script file
            new BigDecimal("568000000000000000023");
            Object a = inv.invokeFunction("calculate", "568000000000000000023", 13.9);
            System.out.println(a);
        } catch (IOException | ScriptException | NoSuchMethodException ex) {
            Logger.getLogger(NashornDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

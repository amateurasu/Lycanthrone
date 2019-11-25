package vn.elite.fundamental.parser.antlr;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

@Slf4j
public class Main {
    public static void main(String[] args) {
        JsonLexer lexer = new JsonLexer(CharStreams.fromString("{\n  name : true}"));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        JsonParser parser = new JsonParser(tokenStream);
        JsonParser.JsonContext json = parser.json();
        for (ParseTree child : json.children) {
            log.info("child {}", child);
            log.info("text {}", child.getText());
            log.info("text {}", child.toStringTree());
        }
    }
}

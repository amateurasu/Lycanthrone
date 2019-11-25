package vn.elite.fundamental.parser.tokenizer;

import lombok.Data;

@Data
public class Token {
    private final TokenType type;
    private final Object value;

    public Token(TokenType type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" + type + ", " + value + ", " + value.getClass().getSimpleName() + "}";
    }
}

package vn.elite.fundamental.parser.tokenizer.math;

import lombok.val;
import vn.elite.fundamental.parser.tokenizer.Token;
import vn.elite.fundamental.parser.tokenizer.TokenizeException;

import java.util.ArrayList;
import java.util.List;

import static vn.elite.fundamental.parser.tokenizer.TokenType.*;

public class MathExpression {
    private List<Token> tokens;

    private MathExpression(List<Token> tokens) {
        this.tokens = tokens;
    }

    public static void main(String[] args) throws TokenizeException {
        val tokenize = tokenize("-8.9sin(45) \r\n+ 2.22x/7 - 2^6.");

        for (Token token : tokenize) {
            System.out.println(token);
        }
    }

    private static boolean isOperator(char ch) {
        switch (ch) {
            case '+':
            case '-':
            case '*':
            case '/':
            case '^':
                return true;
            default:
                return false;
        }
    }

    private static boolean isLeftParenthesis(char ch) {
        return ch == '(';
    }

    private static boolean isRightParenthesis(char ch) {
        return ch == ')';
    }

    public static List<Token> tokenize(String s) throws TokenizeException {
        var letterBuffer = new ArrayList<Character>();
        val numberBuffer = new ArrayList<Character>();
        val result = new ArrayList<Token>();

        int leftParenthesis = 0;
        int rightParenthesis = 0;
        val str = s.toCharArray(); //.replaceAll("\\s+", "")

        for (int i = 0, strLength = str.length; i < strLength; i++) {
            char c = str[i];
            if (Character.isWhitespace(c)) {
                continue;
            }

            if (Character.isDigit(c)) {
                numberBuffer.add(c);
            } else if (Character.isLetter(c)) {
                if (!numberBuffer.isEmpty()) {
                    emptyNumberBufferAsLiteral(result, numberBuffer);
                    result.add(new Token(Operator, "*"));
                }
                letterBuffer.add(c);
            } else if (isOperator(c)) {
                if (!numberBuffer.isEmpty()) {
                    emptyNumberBufferAsLiteral(result, numberBuffer);
                }
                if (!letterBuffer.isEmpty()) {
                    emptyLetterBufferAsVariables(result, letterBuffer);
                }
                result.add(new Token(Operator, c));
            } else {
                switch (c) {
                    case '(':
                        if (!letterBuffer.isEmpty()) {
                            result.add(new Token(Function, join(letterBuffer)));
                            letterBuffer = new ArrayList<>();
                        } else if (!numberBuffer.isEmpty()) {
                            emptyNumberBufferAsLiteral(result, numberBuffer);
                            result.add(new Token(Operator, "*"));
                        }
                        result.add(new Token(LeftParenthesis, c));
                        leftParenthesis++;
                        break;
                    case ')':
                        if (!letterBuffer.isEmpty()) {
                            emptyLetterBufferAsVariables(result, letterBuffer);
                        } else if (!numberBuffer.isEmpty()) {
                            emptyNumberBufferAsLiteral(result, numberBuffer);
                        }
                        result.add(new Token(RightParenthesis, c));
                        rightParenthesis++;
                        if (leftParenthesis < rightParenthesis) {
                            throw new TokenizeException("Unexpected right parenthesis token at: " + i);
                        }
                        break;
                    case '.':
                        if (numberBuffer.contains('.')) {
                            String error = "Invalid decimal format: " + join(numberBuffer) + ".";
                            if (i < strLength - 1) {
                                error += str[i + 1];
                            }
                            throw new TokenizeException(error);
                        }
                        numberBuffer.add(c);
                        break;
                }
            }
        }
        if (!numberBuffer.isEmpty()) {
            emptyNumberBufferAsLiteral(result, numberBuffer);
        }
        if (!letterBuffer.isEmpty()) {
            emptyLetterBufferAsVariables(result, letterBuffer);
        }
        if (leftParenthesis != rightParenthesis) {
            throw new TokenizeException("Unexpected token");
        }
        return result;
    }

    private static String join(ArrayList<Character> characters) {
        StringBuilder sb = new StringBuilder();
        for (char c : characters) {
            sb.append(c);
        }
        return sb.toString();
    }

    private static Number joinNumber(ArrayList<Character> characters) {
        String s = join(characters);
        if (s.contains(".")) {
            return Double.valueOf(s);
        } else {
            return Integer.valueOf(s);
        }
    }

    private static void emptyLetterBufferAsVariables(ArrayList<Token> result, ArrayList<Character> letterBuffer) {
        for (int i = 0, length = letterBuffer.size(); i < length; i++) {
            result.add(new Token(Variable, letterBuffer.get(i)));
            if (i < length - 1) { //there are more Variables left
                result.add(new Token(Operator, "*"));
            }
        }
        letterBuffer.clear();
    }

    private static void emptyNumberBufferAsLiteral(ArrayList<Token> result, ArrayList<Character> numberBuffer) {
        result.add(new Token(Literal, joinNumber(numberBuffer)));
        numberBuffer.clear();
    }
}

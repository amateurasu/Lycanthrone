package vn.elite.fundamental.parser.tokenizer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static vn.elite.fundamental.parser.tokenizer.TokenType.*;

public class Tokenizer {

    /**
     * @param s math expression to parse
     * @return list of math token
     */
    public static List<Token> tokenize(String s) throws Exception {
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(s));
        tokenizer.ordinaryChar('/');  // Treat slash as division operator.

        int leftParenthesis = 0, rightParenthesis = 0;
        List<Token> tokenBuffer = new ArrayList<>();
        while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
            int type = tokenizer.ttype;
            switch (type) {
                case StreamTokenizer.TT_NUMBER:
                    tokenBuffer.add(new Token(Literal, tokenizer.nval));
                    break;
                case StreamTokenizer.TT_WORD:
                    checkPreviousFunction(tokenBuffer);
                    tokenBuffer.add(new Token(Function, tokenizer.sval));
                    break;
                default:
                    switch (type) {
                        case '(':
                            checkPreviousLeftParenthesis(tokenBuffer);
                            tokenBuffer.add(new Token(LeftParenthesis, (char) type));
                            leftParenthesis++;
                            break;
                        case ')':
                            checkPreviousRightParenthesis(tokenBuffer);
                            tokenBuffer.add(new Token(RightParenthesis, (char) type));
                            rightParenthesis++;
                            if (leftParenthesis < rightParenthesis) {
                                throw new TokenizeException("Unexpected token");
                            }
                            break;
                        default:
                            tokenBuffer.add(new Token(Operator, (char) type));
                            break;
                    }
            }
        }

        return tokenBuffer;
    }

    private static void checkPreviousFunction(List<Token> tokenBuffer) throws TokenizeException {
        if (tokenBuffer.size() > 0) {
            TokenType previousType = tokenBuffer.get(tokenBuffer.size() - 1).getType();
            switch (previousType) {
                case Literal:
                case RightParenthesis:
                case Variable:
                    tokenBuffer.add(new Token(Operator, '*'));
                    break;
                case Function:
                case LeftParenthesis:
                    throw new TokenizeException("Unexpected token");
            }
        }
    }

    private static void checkPreviousLeftParenthesis(List<Token> tokenBuffer) throws TokenizeException {
        if (tokenBuffer.size() > 0) {
            TokenType previousType = tokenBuffer.get(tokenBuffer.size() - 1).getType();
            switch (previousType) {
                case Literal:
                case RightParenthesis:
                case Variable:
                    tokenBuffer.add(new Token(Operator, '*'));
                    break;
                case LeftParenthesis:
                    throw new TokenizeException("Unexpected token");
            }
        }
    }

    private static void checkPreviousRightParenthesis(List<Token> tokenBuffer) throws TokenizeException {
        if (tokenBuffer.size() > 0) {
            TokenType previousType = tokenBuffer.get(tokenBuffer.size() - 1).getType();
            switch (previousType) {
                case Literal:
                case Operator:
                case RightParenthesis:
                case Variable:
                    tokenBuffer.add(new Token(Operator, '*'));
                    break;
                case Function:
                    break;
                case LeftParenthesis:
                    throw new TokenizeException("Unexpected token");
            }
        }
    }

    public static void main(String[] args) {
        try {
            List<Token> tokens = tokenize("-8.9.0sin(45) + 2.22x/7 - 2^6.");
            for (Token token : tokens) {
                System.out.println(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

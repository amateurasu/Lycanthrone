package vn.elite.fundamental.parser.javacc;

import java.util.Map;

/* Generated By:JJTree: Do not edit this line. ASTXorNode.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public class ASTXorNode extends SimpleNode {
    public ASTXorNode(int id) {
        super(id);
    }

    public ASTXorNode(ExampleCompiler p, int id) {
        super(p, id);
    }

    @Override
    public AlgValue eval(Map<String, AlgValue> symbolTable) throws InterpreterException {
        AlgValue variable = getChild(0).eval(symbolTable);
        for (int i = 1; i < jjtGetNumChildren(); i++) {
            variable = xor(variable, getChild(i).eval(symbolTable));
        }

        return variable;
    }

    private AlgValue xor(AlgValue a, AlgValue b) throws InterpreterException {
        if (a == null || b == null) {
            throw new InterpreterException();
        }
        if (a.getType() == Type.BOOL && b.getType() == Type.BOOL) {
            return new AlgValue(Type.BOOL, (boolean) a.getValue() ^ (boolean) b.getValue());
        }
        if (a.getType() == Type.INT && b.getType() == Type.INT) {
            return new AlgValue(Type.INT, (int) a.getValue() ^ (int) b.getValue());
        }
        throw new InterpreterException();
    }
}
/*
 * JavaCC - OriginalChecksum=24f2cec698687a08e5980736ee215f4d (do not edit this
 * line)
 */
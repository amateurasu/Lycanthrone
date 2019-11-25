package vn.elite.fundamental.parser.javacc;

import java.util.Map;

/* Generated By:JJTree: Do not edit this line. ASTLENode.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTLENode extends SimpleNode {
    public ASTLENode(int id) {
        super(id);
    }

    public ASTLENode(ExampleCompiler p, int id) {
        super(p, id);
    }

    @Override
    public AlgValue eval(Map<String, AlgValue> symbolTable) throws InterpreterException {
        if (jjtGetNumChildren() != 2) {
            throw new IllegalStateException();
        }
        AlgValue a = getChild(0).eval(symbolTable);
        AlgValue b = getChild(1).eval(symbolTable);
        if (a.getType() != Type.INT || b.getType() != Type.INT) {
            throw new InterpreterException();
        }
        return new AlgValue(Type.BOOL, (int) a.getValue() <= (int) b.getValue());
    }
}
/* JavaCC - OriginalChecksum=2fb4e54250255ca5eea043b4fdbcd242 (do not edit this line) */
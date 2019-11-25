//package vn.elite.fundamental.parser.compiler;
//
//import com.sun.source.tree.TryTree;
//import com.sun.source.util.TreePathScanner;
//import com.sun.source.util.Trees;
//
//public class EmptyTryBlockScanner extends TreePathScanner< Object, Trees > {
//    private int numberOfEmptyTryBlocks;
//
//    @Override
//    public Object visitTry(final TryTree tree, Trees trees) {
//        if( tree.getBlock().getStatements().isEmpty() ){
//            ++numberOfEmptyTryBlocks;
//        }
//
//        return super.visitTry( tree, trees );
//    }
//
//    public int getNumberOfEmptyTryBlocks() {
//        return numberOfEmptyTryBlocks;
//    }
//}

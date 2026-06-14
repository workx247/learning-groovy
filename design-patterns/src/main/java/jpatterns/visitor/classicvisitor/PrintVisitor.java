package jpatterns.visitor.classicvisitor;

/**
 * PrintVisitor — a concrete Visitor that returns a label for the node type
 * it receives.
 *
 * Returning a String (instead of printing) makes the dispatch result
 * directly assertable in tests.
 *
 * visit(RightNode) is reached ONLY for a direct RightNode instance, never
 * for ALeaf or BLeaf — because those classes override accept() and pass
 * `this` with their own concrete static type.
 */
public class PrintVisitor implements Visitor {

    @Override
    public String visit(LeftNode node) {
        return "PrintVisitor -> LeftNode";
    }

    @Override
    public String visit(RightNode node) {
        return "PrintVisitor -> RightNode";
    }

    @Override
    public String visit(ALeaf leaf) {
        return "PrintVisitor -> ALeaf";
    }

    @Override
    public String visit(BLeaf leaf) {
        return "PrintVisitor -> BLeaf";
    }
}

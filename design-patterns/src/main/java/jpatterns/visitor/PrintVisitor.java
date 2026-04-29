package jpatterns.visitor;

/**
 * PrintVisitor — a concrete Visitor that prints which node it is visiting.
 *
 * In a real project each visit() method would perform some operation
 * specific to that node type (serialisation, rendering, compilation…).
 * Here we just print so the dispatch is visible when running Main.
 *
 * Notice visit(RightNode): it is called ONLY for a direct RightNode
 * instance, never for ALeaf or BLeaf — because those classes override
 * accept() and pass `this` with their own concrete type.
 */
public class PrintVisitor implements Visitor {

    @Override
    public void visit(LeftNode node) {
        System.out.println("  PrintVisitor → LeftNode");
    }

    @Override
    public void visit(RightNode node) {
        System.out.println("  PrintVisitor → RightNode  (intermediate, direct instance)");
    }

    @Override
    public void visit(ALeaf leaf) {
        System.out.println("  PrintVisitor → ALeaf");
    }

    @Override
    public void visit(BLeaf leaf) {
        System.out.println("  PrintVisitor → BLeaf");
    }
}

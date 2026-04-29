package jpatterns.visitor;

/**
 * LeftNode — a direct child of Node with no subclasses.
 *
 * It is the simplest case: accept() dispatches unambiguously to
 * visit(LeftNode) because there are no subclasses that could
 * introduce any ambiguity.
 */
public class LeftNode implements Element {

    @Override
    public void accept(Visitor visitor) {
        // `this` is statically typed LeftNode here, so the compiler
        // binds this call to visit(LeftNode) — not visit(Node).
        visitor.visit(this);
    }
}

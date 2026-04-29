package jpatterns.visitor;

/**
 * ALeaf — a concrete leaf that extends RightNode.
 *
 * Overriding accept() is essential here. If ALeaf inherited
 * RightNode's accept(), the `this` inside that inherited method would
 * carry compile-time type RightNode, and v.visit(this) would call
 * visit(RightNode) — silently losing ALeaf's identity.
 *
 * By overriding, `this` has compile-time type ALeaf, and the compiler
 * binds the call to visit(ALeaf).
 */
public class ALeaf extends RightNode {

    @Override
    public void accept(Visitor visitor) {
        // `this` is ALeaf here → compiler binds to visit(ALeaf).
        visitor.visit(this);
    }
}

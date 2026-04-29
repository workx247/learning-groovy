package jpatterns.visitor;

/**
 * RightNode — an intermediate node in the hierarchy.
 *
 * It implements Node directly AND serves as the parent of ALeaf and
 * BLeaf. This dual role is what makes the overloading trap interesting:
 * a variable declared as RightNode might actually hold an ALeaf or
 * BLeaf at runtime — and a naive overloaded-method approach would lose
 * that distinction entirely (see Main).
 *
 * Because RightNode itself can be instantiated and visited as its own
 * type, the Visitor interface needs a visit(RightNode) overload
 * separate from visit(ALeaf) and visit(BLeaf).
 */
public class RightNode implements Element {

    @Override
    public void accept(Visitor visitor) {
        // `this` is statically typed RightNode here.
        //
        // WATCH OUT: if ALeaf or BLeaf did NOT override accept(), they
        // would inherit this method. Inside an inherited method `this`
        // would still have compile-time type RightNode, so v.visit(this)
        // would silently dispatch to visit(RightNode) instead of
        // visit(ALeaf) or visit(BLeaf) — the subtype identity would be
        // lost. That is why every concrete subclass MUST override accept().
        visitor.visit(this);
    }
}

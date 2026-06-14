package jpatterns.visitor.classicvisitor;

/**
 * BLeaf — a concrete leaf that extends RightNode.
 *
 * Same reasoning as ALeaf: accept() must be overridden so that `this`
 * carries BLeaf's static type into the visit() call.
 */
public class BLeaf extends RightNode {

    @Override
    public String accept(Visitor visitor) {
        // `this` is BLeaf here → compiler binds to visit(BLeaf).
        return visitor.visit(this);
    }
}

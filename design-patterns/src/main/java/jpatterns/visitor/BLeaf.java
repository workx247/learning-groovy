package jpatterns.visitor;

/**
 * BLeaf — a concrete leaf that extends RightNode.
 *
 * Same reasoning as ALeaf: accept() must be overridden so that `this`
 * carries BLeaf's static type into the visit() call.
 */
public class BLeaf extends RightNode {

    @Override
    public void accept(Visitor visitor) {
        // `this` is BLeaf here → compiler binds to visit(BLeaf).
        visitor.visit(this);
    }
}

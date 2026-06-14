package jpatterns.visitor.classicvisitor;

/**
 * Visitor — declares one visit() overload for every concrete node type.
 *
 * WHY one overload per type?
 * Java resolves overloads at compile time using the static (declared)
 * type of the argument. The accept() method in each concrete class
 * calls v.visit(this), where `this` has the concrete static type, so
 * the right overload is selected. This is called "simulated double
 * dispatch": one virtual dispatch (accept() picks the right class's
 * implementation) followed by one static overload resolution
 * (visit() picks the right visitor method).
 *
 * WHY is RightNode listed even though it has subclasses?
 * RightNode can itself be instantiated. A visitor must handle it
 * separately from ALeaf and BLeaf. If a particular visitor does not
 * care about the difference, it can delegate all three to a shared
 * helper method.
 *
 * ADDING A NEW NODE TYPE?
 * Add a visit() overload here. The compiler will immediately flag
 * every existing Visitor implementation that is missing the new
 * method — a useful compile-time safety net.
 */
public interface Visitor {
    String visit(LeftNode node);
    String visit(RightNode node);
    String visit(ALeaf leaf);
    String visit(BLeaf leaf);
}

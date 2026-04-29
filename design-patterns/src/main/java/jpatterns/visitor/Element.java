package jpatterns.visitor;

/**
 * Element — the top of the element hierarchy.
 *
 * Every node in the tree must implement accept(), which is the single
 * hook the Visitor pattern relies on. The implementation in each
 * concrete class always looks the same:
 *
 *   public void accept(Visitor v) { v.visit(this); }
 *
 * The critical word is `this`. Inside a concrete class, `this` has
 * the concrete compile-time type, so Java selects the right visit()
 * overload at compile time — regardless of what declared type the
 * caller is holding.
 */
public interface Element {
    void accept(Visitor visitor);
}

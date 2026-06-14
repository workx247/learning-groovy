package jpatterns.visitor.classicvisitor;

/**
 * RootNode — the base type of the classic visitor hierarchy.
 *
 * Every node must implement accept(). The implementation in each
 * concrete class always looks the same:
 *
 *   public String accept(Visitor v) { return v.visit(this); }
 *
 * The critical word is `this`. Inside a concrete class, `this` has
 * the concrete compile-time type, so Java selects the right visit()
 * overload at compile time — regardless of what declared type the
 * caller is holding. That is the first half of double dispatch.
 */
public interface RootNode {
    String accept(Visitor visitor);
}

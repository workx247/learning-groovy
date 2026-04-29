package gpatterns.visitor.closuremap

import groovy.transform.CompileStatic

/**
 * ClosureMapVisitor — a Groovy-idiomatic visitor that uses a
 * Map<Class, Closure> as its dispatch table instead of a Visitor interface.
 *
 * ADVANTAGES over the Java Visitor pattern:
 *   - No Visitor interface required.
 *   - No accept() on the element classes — works on third-party types.
 *   - Adding a new operation means building a new map, not a new class.
 *   - The entire visitor can be defined inline at the call site using
 *     Groovy's map and closure literals — very concise.
 *
 * DISADVANTAGE:
 *   No compile-time exhaustiveness check. Missing a handler falls through
 *   to a supertype (via hierarchy walking below) or throws at runtime.
 *   Unlike the classic Visitor pattern, adding a new type to the hierarchy
 *   never produces a compile error anywhere.
 *
 * HIERARCHY WALKING:
 *   findHandler() walks the superclass chain from the runtime type upward.
 *   This means a (RightNode): { ... } entry acts as a deliberate fallback
 *   for all RightNode subtypes that have no explicit handler of their own.
 *   That can be useful or dangerous — see Main for a demo of both.
 *   Note: interface types are not checked, only the superclass chain.
 */
@CompileStatic
class ClosureMapVisitor {

    // The dispatch table — maps each Class to the Closure to invoke.
    private final Map<Class, Closure> handlers

    ClosureMapVisitor(Map<Class, Closure> handlers) {
        this.handlers = handlers
    }

    /**
     * Dispatches node to the most specific registered handler found
     * by walking the superclass chain.
     *
     * Throws IllegalArgumentException if no handler is found at any
     * level of the hierarchy.
     */
    void visit(Object node) {
        def handler = findHandler(node.class)
        if (handler == null) {
            throw new IllegalArgumentException(
                "No handler registered for '${node.class.simpleName}' " +
                'or any of its supertypes.')
        }
        handler.call(node)
    }

    /**
     * Walks up the superclass chain from cls, returning the first
     * registered handler found, or null if none matches.
     *
     * The Elvis operator (?:) returns handlers[cls] when non-null,
     * otherwise recurses to the superclass. cls.superclass is null
     * when we have walked past Object — that is the base case.
     */
    private Closure findHandler(Class cls) {
        if (cls == null) { return null }
        return handlers[cls] ?: findHandler(cls.superclass)
    }
}

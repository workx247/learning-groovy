package gpatterns.visitor.dynamicdispatch

import jpatterns.visitor.nodes.VNodeBuilder
import spock.lang.Specification

/**
 * DynamicDispatchSpec — Spock tests for Groovy's runtime overload dispatch.
 *
 * WHAT THIS DEMONSTRATES
 *
 *   In Java, overload resolution is STATIC — the compiler picks which
 *   visit() overload to call based on the DECLARED type of the argument.
 *   If you declare:
 *       RootNode node = new ALeaf();
 *       visitor.visit(node);   // Java calls visit(RootNode) — wrong!
 *   Java sees the declared type (RootNode) and binds to visit(RootNode),
 *   ignoring the fact that the actual object is an ALeaf.
 *
 *   Groovy resolves overloads DYNAMICALLY — at runtime, based on the ACTUAL
 *   type of the object. Even if `node` is typed as `def` (meaning Groovy
 *   defers all type decisions), calling visitor.visit(node) lands in the
 *   overload that matches the object's concrete type.
 *
 *   This is the essence of the "dynamic dispatch" visitor variant: no
 *   accept() method needed on the nodes, no extension module — Groovy's
 *   runtime method resolution does the double dispatch automatically.
 *
 * WHY THE TESTS USE def
 *   All node variables are declared as `def`, which forces Groovy to
 *   determine the overload at runtime rather than at compile time.
 *   Using the concrete type (e.g. ALeaf node = ...) would let even Java
 *   pick the right overload, defeating the purpose of the demo.
 *
 * NOTE: No @CompileDynamic annotation is needed here. Spock specs are
 *   compiled dynamically by default (they extend Specification, which is
 *   itself dynamic). DispatchVisitor is @CompileStatic internally, but
 *   that only affects its own method bodies — the caller (this spec)
 *   still resolves which visit() to call at runtime.
 */
class DynamicDispatchSpec extends Specification {

    // DispatchVisitor has one visit() overload per node type.
    // Groovy selects the correct one at runtime based on the argument's
    // actual class, not its declared type.
    def visitor = new DispatchVisitor()

    // ---------------------------------------------------------------------------
    // Data-driven feature — runs once per row in the where: table below.
    // ---------------------------------------------------------------------------
    def "visit() dispatches to the correct overload for #nodeType at runtime"() {
        given: "a node whose declared type is def (runtime type is the subtype)"
        // nodeFactory is a Closure from the where: table. The closure returns
        // a RootNode reference, but the underlying object is the concrete type.
        def node = nodeFactory()

        when: "visit() is called — Groovy picks the overload at runtime"
        // Java would call visit(RootNode) here because the declared type of
        // `node` is Object (def). Groovy looks at the actual class and picks
        // the most specific matching overload.
        def result = visitor.visit(node)

        then: "the overload matching the ACTUAL type was called"
        result == expected

        where: "each row exercises one concrete node type with a supertype reference"
        // VNodeBuilder returns every node as RootNode — the actual object is a subtype.
        // Spock replaces #nodeType in the feature name with the column value.
        nodeType    | nodeFactory                     || expected
        'RootNode'  | { VNodeBuilder.mkRootNode() }  || 'Visiting RootNode: RootNode()'
        'LeftNode'  | { VNodeBuilder.mkLeftNode() }  || 'Visiting LeftNode: LeftNode()'
        'RightNode' | { VNodeBuilder.mkRightNode() } || 'Visiting RightNode: RightNode()'
        'ALeaf'     | { VNodeBuilder.mkALeaf() }     || 'Visiting ALeaf: ALeaf()'
        'BLeaf'     | { VNodeBuilder.mkBLeaf() }     || 'Visiting BLeaf: BLeaf()'
    }
}

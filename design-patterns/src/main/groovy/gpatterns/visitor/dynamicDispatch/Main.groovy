package gpatterns.visitor.dynamicDispatch

import gpatterns.visitor.nodes.ALeaf
import gpatterns.visitor.nodes.BLeaf
import gpatterns.visitor.nodes.LeftNode
import gpatterns.visitor.nodes.RightNode
import groovy.transform.CompileStatic

/**
 * Demonstrates that Groovy's runtime dispatch selects the correct visit()
 * overload based on actual type, even when the declared type is a supertype.
 */
@CompileStatic
@SuppressWarnings(['PackageName',
        'EmptyClass',
        'UnnecessaryToString',
        'ConsecutiveBlankLines',
        'UnnecessaryObjectReferences'])
class Main {

    static void main(String[] args) {

        def visitor = new DispatchVisitor()

        // -------------------------------------------------------------------
        // DEMO 1 — all variables declared as def (runtime type drives dispatch)
        //
        // accept() is not defined on any of these classes in source.
        // Groovy finds NodeExtensions.accept(Xxx self, Visitor v) at runtime
        // and calls it transparently — as if it had always been there.
        // -------------------------------------------------------------------
        def root = new gpatterns.visitor.nodes.Node()
        def left  = new LeftNode()
        def right = new RightNode()
        def aLeaf = new ALeaf()
        def bLeaf = new BLeaf()
        gpatterns.visitor.nodes.Node node = new BLeaf()

        println '=== Extension-module visitor  [declared type: def] ==='
        visitor.visit(root)
        visitor.visit(left)
        visitor.visit(right)  // → NodeExtensions → visit(RightNode)
        visitor.visit(aLeaf)  // → NodeExtensions → visit(ALeaf)
        visitor.visit(bLeaf)  // → NodeExtensions → visit(BLeaf)
        visitor.visit(node)


        println()

        // -------------------------------------------------------------------
        // DEMO 2 — declared type is the INTERMEDIATE class RightNode.
        //
        // In Java, a variable declared as RightNode dispatches overloaded
        // methods to visit(RightNode) regardless of the runtime type.
        //
        // In Groovy, runtime dispatch applies to extension methods too.
        // The runtime type is ALeaf / BLeaf, so Groovy selects
        // NodeExtensions.accept(ALeaf, ...) / accept(BLeaf, ...) —
        // even though the declared type is RightNode.
        //
        // This is the key advantage of Groovy dynamic dispatch: the
        // Java `this`-trick in accept() exists purely to work around
        // Java's compile-time overload resolution. Groovy does not need it.
        // -------------------------------------------------------------------
//        RightNode aAsRight = new ALeaf()
//        RightNode bAsRight = new BLeaf()
//
//        println '=== Extension-module visitor  [declared type: RightNode] ==='
//        aAsRight.accept(visitor)   // → visit(ALeaf)  — NOT visit(RightNode)!
//        bAsRight.accept(visitor)   // → visit(BLeaf)  — NOT visit(RightNode)!
    }
}

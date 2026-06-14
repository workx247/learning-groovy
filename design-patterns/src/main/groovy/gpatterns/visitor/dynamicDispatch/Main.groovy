package gpatterns.visitor.dynamicDispatch

import gpatterns.visitor.nodes.ALeaf
import gpatterns.visitor.nodes.BLeaf
import gpatterns.visitor.nodes.LeftNode
import gpatterns.visitor.nodes.RightNode
import gpatterns.visitor.nodes.RootNode
import groovy.transform.CompileDynamic

// import groovy.transform.CompileStatic

/**
 * Demonstrates that Groovy's runtime dispatch selects the correct visit()
 * overload based on actual type, even when the declared type is a supertype.
 */
@CompileDynamic
@SuppressWarnings(['PackageName',
        'EmptyClass',
        'UnnecessaryToString',
        'ConsecutiveBlankLines',
        'UnnecessaryObjectReferences'])
class Main {

    static RootNode mkRootNode() {
        return new RootNode()
    }

    static RootNode mkLeftNode() {
        return new LeftNode()
    }

    static RootNode mkRightNode() {
        return new RightNode()
    }

    static RootNode mkALeaf() {
        return new ALeaf()
    }

    static RootNode mkBLeaf() {
        return new BLeaf()
    }

    static void main(String[] args) {

        def visitor = new DispatchVisitor()

        // -------------------------------------------------------------------
        // DEMO 1 — all variables declared as def (runtime type drives dispatch)
        //
        // accept() is not defined on any of these classes in source.
        // Groovy finds NodeExtensions.accept(Xxx self, Visitor v) at runtime
        // and calls it transparently — as if it had always been there.
        // -------------------------------------------------------------------
        def root  = mkRootNode()
        def left  = mkLeftNode()
        def right = mkRightNode()
        def aLeaf = mkALeaf()
        def bLeaf = mkBLeaf()
        RootNode node = new BLeaf()

        println '=== Extension-module visitor  [declared type: def] ==='
        println visitor.visit(root)
        println visitor.visit(left)
        println visitor.visit(right)  // → NodeExtensions → visit(RightNode)
        println visitor.visit(aLeaf)  // → NodeExtensions → visit(ALeaf)
        println visitor.visit(bLeaf)  // → NodeExtensions → visit(BLeaf)
        println visitor.visit(node)


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

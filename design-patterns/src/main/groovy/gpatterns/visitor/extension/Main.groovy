package gpatterns.visitor.extension

import gpatterns.visitor.nodes.ALeaf
import gpatterns.visitor.nodes.BLeaf
import gpatterns.visitor.nodes.LeftNode
import gpatterns.visitor.nodes.RightNode

/**
 * Main — demonstrates the extension-module visitor.
 *
 * The node classes have NO accept() in their source. NodeExtensions adds
 * it retroactively via Groovy's extension module mechanism, registered in
 * META-INF/groovy/org.codehaus.groovy.runtime.ExtensionModule.
 *
 * To the caller, node.accept(visitor) looks and feels as if accept() had
 * always been part of the node class — no wrapping, no casting, no
 * use{} block needed.
 *
 * NOTE: @CompileStatic is intentionally absent here. Extension methods are
 * a runtime mechanism — the static type checker cannot see them and would
 * reject every accept() call. Dynamic dispatch is the whole point of this
 * demo, so we let Groovy resolve accept() at runtime via the extension module.
 *
 * HOW TO RUN:
 *   mvn compile exec:java -pl design-patterns \
 *       -Dexec.mainClass=gpatterns.visitor.extension.Main
 */
@SuppressWarnings('CompileStatic')
class Main {

    static void main(String[] args) {

        def visitor = new PrintVisitor()

        // -------------------------------------------------------------------
        // DEMO 1 — all variables declared as def (runtime type drives dispatch)
        //
        // accept() is not defined on any of these classes in source.
        // Groovy finds NodeExtensions.accept(Xxx self, Visitor v) at runtime
        // and calls it transparently — as if it had always been there.
        // -------------------------------------------------------------------
        def left  = new LeftNode()
        def right = new RightNode()
        def aLeaf = new ALeaf()
        def bLeaf = new BLeaf()

        println '=== Extension-module visitor  [declared type: def] ==='
        left.accept(visitor)   // → NodeExtensions → visit(LeftNode)
        right.accept(visitor)  // → NodeExtensions → visit(RightNode)
        aLeaf.accept(visitor)  // → NodeExtensions → visit(ALeaf)
        bLeaf.accept(visitor)  // → NodeExtensions → visit(BLeaf)

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
        RightNode aAsRight = new ALeaf()
        RightNode bAsRight = new BLeaf()

        println '=== Extension-module visitor  [declared type: RightNode] ==='
        aAsRight.accept(visitor)   // → visit(ALeaf)  — NOT visit(RightNode)!
        bAsRight.accept(visitor)   // → visit(BLeaf)  — NOT visit(RightNode)!
    }
}

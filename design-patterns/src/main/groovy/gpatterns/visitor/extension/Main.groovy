package gpatterns.visitor.extension

import gpatterns.visitor.nodes.ALeaf
import gpatterns.visitor.nodes.BLeaf
import gpatterns.visitor.nodes.LeftNode
import gpatterns.visitor.nodes.RightNode
import gpatterns.visitor.nodes.RootNode
import groovy.transform.CompileDynamic

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
@CompileDynamic
@SuppressWarnings('CompileStatic')
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

        def visitor = new PrintVisitor()

        // -------------------------------------------------------------------
        // DEMO 1 — all variables declared as def (runtime type drives dispatch)
        //
        // accept() is not defined on any of these classes in source.
        // Groovy finds NodeExtensions.accept(Xxx self, Visitor v) at runtime
        // and calls it transparently — as if it had always been there.
        // -------------------------------------------------------------------
        def node  = mkRootNode()
        def left  = mkLeftNode()
        def right = mkRightNode()
        def aLeaf = mkALeaf()
        def bLeaf = mkBLeaf()

        println '--- Extension-module visitor  [declared type: def] ---'
        println node.accept(visitor)
        println left.accept(visitor)
        println right.accept(visitor)
        println aLeaf.accept(visitor)
        println bLeaf.accept(visitor)
        println()
    }
}

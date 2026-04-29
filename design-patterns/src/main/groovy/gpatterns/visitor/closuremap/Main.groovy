package gpatterns.visitor.closuremap

import gpatterns.visitor.nodes.ALeaf
import gpatterns.visitor.nodes.BLeaf
import gpatterns.visitor.nodes.LeftNode
import gpatterns.visitor.nodes.RightNode
import groovy.transform.CompileStatic

/**
 * Main — demonstrates ClosureMapVisitor against the shared node hierarchy.
 *
 * Tree structure:
 *
 *       (no shared interface)
 *       /                  \
 *   LeftNode            RightNode
 *                       /       \
 *                    ALeaf     BLeaf
 *
 * HOW TO RUN:
 *   mvn compile exec:java -pl design-patterns \
 *       -Dexec.mainClass=gpatterns.visitor.closuremap.Main
 */
@CompileStatic
@SuppressWarnings('SpaceAroundMapEntryColon')
class Main {

    static void main(String[] args) {

        // -------------------------------------------------------------------
        // DEMO 1 — a complete dispatch table, one closure per concrete type.
        //
        // (ClassName): syntax uses the Class object as the map key.
        // Groovy's runtime dispatch finds the right handler automatically.
        // -------------------------------------------------------------------
        def visitor = new ClosureMapVisitor([
            (LeftNode): { n -> println '  ClosureMapVisitor → LeftNode' },
            (RightNode): { n ->
                println '  ClosureMapVisitor → RightNode  (direct instance)'
            },
            (ALeaf): { n -> println '  ClosureMapVisitor → ALeaf' },
            (BLeaf): { n -> println '  ClosureMapVisitor → BLeaf' },
        ])

        // Groovy uses the runtime type of each variable for dispatch —
        // the declared type (def = Object) does not matter here.
        def left  = new LeftNode()
        def right = new RightNode()
        def aLeaf = new ALeaf()
        def bLeaf = new BLeaf()

        println '=== Complete dispatch table ==='
        visitor.visit(left)
        visitor.visit(right)
        visitor.visit(aLeaf)
        visitor.visit(bLeaf)

        println()

        // -------------------------------------------------------------------
        // DEMO 2 — deliberate supertype fallback.
        //
        // Only RightNode is registered; ALeaf and BLeaf have no explicit entry.
        // findHandler() walks the superclass chain and falls back to RightNode.
        //
        // TWO INTERPRETATIONS:
        //   GOOD — intentional catch-all: "treat all RightNode subtypes alike".
        //   BAD  — silent mask: a new CLeaf will fall here with no warning,
        //          no compile error, and no crash — just a subtly wrong result.
        //          Compare with Visitor: adding visit(CLeaf) to the interface
        //          would break every implementor at compile time.
        // -------------------------------------------------------------------
        def partialVisitor = new ClosureMapVisitor([
            (LeftNode): { n -> println '  partial → LeftNode' },
            (RightNode): { n ->
                def type = n.class.simpleName
                println "  partial → RightNode fallback (runtime type: ${type})"
            },
        ])

        println '=== Supertype fallback (RightNode catches whole subtree) ==='
        partialVisitor.visit(left)
        partialVisitor.visit(right)
        partialVisitor.visit(aLeaf)    // falls back to RightNode handler
        partialVisitor.visit(bLeaf)    // falls back to RightNode handler

        println()

        // -------------------------------------------------------------------
        // DEMO 3 — no handler found anywhere → loud runtime exception.
        //
        // At least the failure is explicit rather than silent. But it happens
        // at runtime, not at compile time.
        // -------------------------------------------------------------------
        println '=== Missing handler → runtime exception ==='
        def incompleteVisitor = new ClosureMapVisitor([
            (ALeaf): { n -> println '  incomplete → ALeaf' },
        ])

        try {
            incompleteVisitor.visit(new LeftNode())
        } catch (IllegalArgumentException e) {
            println "  Caught: ${e.message}"
        }
    }
}

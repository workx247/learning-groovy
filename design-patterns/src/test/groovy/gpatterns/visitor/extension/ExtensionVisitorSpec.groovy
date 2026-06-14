package gpatterns.visitor.extension

import groovy.transform.CompileDynamic
import jpatterns.visitor.nodes.VNodeBuilder
import spock.lang.Specification

/**
 * ExtensionVisitorSpec — Spock tests for the extension-module visitor.
 *
 * This spec verifies two things at once:
 *
 *   1. The extension module (NodeExtensions) was correctly registered in
 *      META-INF/groovy/org.codehaus.groovy.runtime.ExtensionModule and is
 *      picked up at runtime, because accept() is not defined in any node
 *      class's source code.
 *
 *   2. PrintVisitor.visit() is called with the correct concrete type,
 *      producing the expected string for each of the five node types.
 *
 * WHY @CompileDynamic?
 *   Extension methods are a runtime mechanism. The static type checker
 *   cannot see accept() on the node types, so it would reject every call
 *   with "No such method" at compile time. @CompileDynamic tells the Groovy
 *   compiler to skip static analysis and let the runtime Groovy MOP
 *   (Meta-Object Protocol) resolve accept() via the extension module.
 *
 * HOW SPOCK WORKS (quick primer):
 *   - A spec class extends Specification instead of using @Test annotations.
 *   - Test methods are called "features" and are named as plain strings.
 *   - Each feature is divided into labelled blocks:
 *       given:  — set up preconditions
 *       when:   — perform the action under test
 *       then:   — assert the outcome (no assert keyword needed)
 *   - The where: block at the end drives the feature with multiple data rows,
 *     turning one feature method into N independent test cases.
 *   - #nodeType in the feature name is replaced by the actual column value
 *     in each iteration, so each run gets a descriptive name in the report.
 */
@CompileDynamic
class ExtensionVisitorSpec extends Specification {

    // PrintVisitor is the concrete visitor under test.
    // Declared at field level so it is shared across all where: iterations.
    def visitor = new PrintVisitor()

    // ---------------------------------------------------------------------------
    // Data-driven feature — runs once per row in the where: table below.
    // ---------------------------------------------------------------------------
    def "accept() dispatches to the correct PrintVisitor overload for #nodeType"() {
        given: "a node created via VNodeBuilder (declared type is RootNode)"
        // nodeFactory is a closure from the where: table; calling it here
        // produces the concrete node for this iteration.
        def node = nodeFactory()

        when: "the extension-module accept() is called on the node"
        // accept() does not exist in the node's source — it is injected at
        // runtime by NodeExtensions via the Groovy MOP.
        def result = node.accept(visitor)

        then: "PrintVisitor returns the string for the node's ACTUAL type"
        // Spock evaluates every statement in then: as a boolean assertion.
        // No assertEquals() needed — == is enough.
        result == expected

        where: "each row exercises one concrete node type"
        // nodeFactory is a Closure so nodes are created fresh each iteration.
        // #nodeType appears in the feature name; || separates inputs from expected.
        nodeType    | nodeFactory                     || expected
        'RootNode'  | { VNodeBuilder.mkRootNode() }  || 'PrintVisitor -> RootNode'
        'LeftNode'  | { VNodeBuilder.mkLeftNode() }  || 'PrintVisitor -> LeftNode'
        'RightNode' | { VNodeBuilder.mkRightNode() } || 'PrintVisitor -> RightNode'
        'ALeaf'     | { VNodeBuilder.mkALeaf() }     || 'PrintVisitor -> ALeaf'
        'BLeaf'     | { VNodeBuilder.mkBLeaf() }     || 'PrintVisitor -> BLeaf'
    }
}

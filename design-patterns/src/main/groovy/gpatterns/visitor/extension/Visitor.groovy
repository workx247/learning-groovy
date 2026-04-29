package gpatterns.visitor.extension

import gpatterns.visitor.nodes.ALeaf
import gpatterns.visitor.nodes.BLeaf
import gpatterns.visitor.nodes.LeftNode
import gpatterns.visitor.nodes.RightNode
import groovy.transform.CompileStatic

/**
 * Visitor — the classic GoF visitor interface.
 *
 * The node classes themselves have no accept() in their source code.
 * Callers invoke accept() via the extension method added by
 * NodeExtensions, which Groovy wires up transparently at runtime.
 */
@CompileStatic
interface Visitor {
    void visit(LeftNode node)
    void visit(RightNode node)
    void visit(ALeaf leaf)
    void visit(BLeaf leaf)
}

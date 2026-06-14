package gpatterns.visitor.extension

import gpatterns.visitor.nodes.ALeaf
import gpatterns.visitor.nodes.BLeaf
import gpatterns.visitor.nodes.LeftNode
import gpatterns.visitor.nodes.RightNode
import gpatterns.visitor.nodes.RootNode
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
    String visit(RootNode node)
    String visit(LeftNode node)
    String visit(RightNode node)
    String visit(ALeaf leaf)
    String visit(BLeaf leaf)
}

package gpatterns.visitor.extension

import gpatterns.visitor.nodes.ALeaf
import gpatterns.visitor.nodes.BLeaf
import gpatterns.visitor.nodes.LeftNode
import gpatterns.visitor.nodes.RightNode
import groovy.transform.CompileStatic

/**
 * PrintVisitor — a concrete Visitor that prints the type it receives.
 *
 * visit(RightNode) is called only for a direct RightNode instance.
 * ALeaf and BLeaf have their own extension methods that pass `self`
 * with their concrete static type, so they dispatch to visit(ALeaf)
 * and visit(BLeaf) respectively — never to visit(RightNode).
 */
@CompileStatic
class PrintVisitor implements Visitor {

    @Override
    void visit(LeftNode node) {
        println '  PrintVisitor → LeftNode'
    }

    @Override
    void visit(RightNode node) {
        println '  PrintVisitor → RightNode  (direct instance)'
    }

    @Override
    void visit(ALeaf leaf) {
        println '  PrintVisitor → ALeaf'
    }

    @Override
    void visit(BLeaf leaf) {
        println '  PrintVisitor → BLeaf'
    }
}

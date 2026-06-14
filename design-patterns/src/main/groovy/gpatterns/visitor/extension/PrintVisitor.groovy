package gpatterns.visitor.extension

import gpatterns.visitor.nodes.ALeaf
import gpatterns.visitor.nodes.BLeaf
import gpatterns.visitor.nodes.LeftNode
import gpatterns.visitor.nodes.RightNode
import gpatterns.visitor.nodes.RootNode
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
    String visit(RootNode node) {
        return '  PrintVisitor -> RootNode'
    }

    @Override
    String visit(LeftNode node) {
        return '  PrintVisitor -> LeftNode'
    }

    @Override
    String visit(RightNode node) {
        return '  PrintVisitor -> RightNode'
    }

    @Override
    String visit(ALeaf leaf) {
        return '  PrintVisitor -> ALeaf'
    }

    @Override
    String visit(BLeaf leaf) {
        return '  PrintVisitor -> BLeaf'
    }
}

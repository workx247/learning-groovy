package gpatterns.visitor.dynamicdispatch

import gpatterns.visitor.extension.Visitor
import jpatterns.visitor.nodes.RootNode
import jpatterns.visitor.nodes.ALeaf
import jpatterns.visitor.nodes.BLeaf
import jpatterns.visitor.nodes.LeftNode
import jpatterns.visitor.nodes.RightNode
import groovy.transform.CompileStatic

/**
 * Concrete visitor that relies on Groovy's runtime method dispatch to call
 * the correct visit() overload based on the actual type of the argument,
 * even when that argument is declared as a supertype.
 */
@CompileStatic
class DispatchVisitor implements Visitor {

    @Override
    String visit(RootNode element) {
        return 'Visiting RootNode: ' + element
    }

    @Override
    String visit(LeftNode element) {
        return 'Visiting LeftNode: ' + element
    }

    @Override
    String visit(RightNode element) {
        return 'Visiting RightNode: ' + element
    }

    @Override
    String visit(ALeaf element) {
        return 'Visiting ALeaf: ' + element
    }

    @Override
    String visit(BLeaf element) {
        return 'Visiting BLeaf: ' + element
    }

}

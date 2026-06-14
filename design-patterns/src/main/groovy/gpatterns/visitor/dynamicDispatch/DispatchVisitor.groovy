package gpatterns.visitor.dynamicDispatch

import gpatterns.visitor.extension.Visitor
import gpatterns.visitor.nodes.RootNode
import gpatterns.visitor.nodes.ALeaf
import gpatterns.visitor.nodes.BLeaf
import gpatterns.visitor.nodes.LeftNode
import gpatterns.visitor.nodes.RightNode
import groovy.transform.CompileStatic

/**
 * Concrete visitor that relies on Groovy's runtime method dispatch to call
 * the correct visit() overload based on the actual type of the argument,
 * even when that argument is declared as a supertype.
 */
@CompileStatic
@SuppressWarnings(['PackageName', 'UnnecessaryToString'])
class DispatchVisitor implements Visitor {

    @Override
    String visit(RootNode element) {
        return 'Visiting RootNode ' + element.toString()
    }

    @Override
    String visit(LeftNode element) {
        return 'Visiting LeftNode ' + element.toString()
    }

    @Override
    String visit(RightNode element) {
        return 'Visiting RightNode ' + element.toString()
    }

    @Override
    String visit(ALeaf element) {
        return 'Visiting ALeaf ' + element.toString()
    }

    @Override
    String visit(BLeaf element) {
        return 'Visiting BLeaf ' + element.toString()
    }

}

package gpatterns.visitor.dynamicDispatch

// import gpatterns.visitor.extension.Visitor
import gpatterns.visitor.nodes.Node
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
class DispatchVisitor { // implements Visitor {

    static void visit(Node element) {
        println 'Visiting Node' + element.toString()
    }

    static void visit(LeftNode element) {
        println 'Visiting LeftNode' + element.toString()
    }

    static void visit(RightNode element) {
        println 'Visiting RightNode' + element.toString()
    }

    static void visit(ALeaf element) {
        println 'Visiting ALeaf' + element.toString()
    }

//    static void visit(BLeaf element) {
//        println 'Visiting BLeaf' + element.toString()
//    }

}

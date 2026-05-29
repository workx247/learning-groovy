package gpatterns.visitor.nodes

import groovy.transform.CompileStatic

/**
 * Marker interface for all nodes in the visitor-pattern example hierarchy.
 * LeftNode, RightNode, ALeaf, and BLeaf all implement this interface.
 */
@CompileStatic
sealed class Node permits LeftNode, RightNode {

}

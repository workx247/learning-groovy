package gpatterns.visitor.nodes

import groovy.transform.Canonical
import groovy.transform.CompileStatic

/**
 * BLeaf — concrete leaf that extends RightNode.
 * No accept() — third-party simulation.
 */
@CompileStatic
@Canonical
final class BLeaf extends RightNode { }

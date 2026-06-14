package gpatterns.visitor.nodes

import groovy.transform.Canonical
import groovy.transform.CompileStatic

/**
 * ALeaf — concrete leaf that extends RightNode.
 * No accept() — third-party simulation.
 */
@CompileStatic
@Canonical
final class ALeaf extends RightNode { }

package gpatterns.visitor.nodes

import groovy.transform.CompileStatic

/**
 * RightNode — an intermediate node. Direct parent of ALeaf and BLeaf.
 *
 * No accept() — third-party simulation. The interesting challenge for
 * any visitor approach: RightNode must be handleable both as a direct
 * instance AND as the common supertype of ALeaf and BLeaf.
 */
@CompileStatic
class RightNode { }

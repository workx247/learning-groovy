package gpatterns.visitor.nodes

import groovy.transform.CompileStatic

/**
 * LeftNode — a leaf node that is a direct child of the hierarchy root.
 *
 * Deliberately has NO accept() method: it simulates a class from a
 * third-party library that you cannot modify. Both visitor approaches
 * (closuremap and extension) must work around this constraint.
 */
@CompileStatic
class LeftNode { }

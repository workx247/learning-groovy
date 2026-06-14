package gpatterns.visitor.extension

import groovy.transform.CompileStatic
import jpatterns.visitor.nodes.ALeaf
import jpatterns.visitor.nodes.BLeaf
import jpatterns.visitor.nodes.LeftNode
import jpatterns.visitor.nodes.RightNode
import jpatterns.visitor.nodes.RootNode

/**
 * NodeExtensions — retroactively adds accept(Visitor) to each node class
 * via Groovy's extension module mechanism.
 *
 * HOW EXTENSION MODULES WORK:
 *   Groovy scans the classpath at startup for descriptor files at:
 *     META-INF/groovy/org.codehaus.groovy.runtime.ExtensionModule
 *   Each descriptor names one or more extension classes. Groovy then
 *   treats every static method in those classes as an instance method on
 *   the type of the first parameter (`self`).
 *
 *   So this method:
 *     static void accept(LeftNode self, Visitor v) { ... }
 *   becomes callable as:
 *     myLeftNode.accept(v)
 *
 *   The original class is never modified. Groovy's runtime resolves the
 *   call by checking registered extension methods before throwing
 *   MissingMethodException. This is the "blessed" Groovy alternative to
 *   ad-hoc monkey-patching via metaClass.
 *
 * WHY EACH CONCRETE CLASS NEEDS ITS OWN METHOD:
 *   Groovy's runtime dispatch uses the actual runtime type of `self` to
 *   select the right overload. When accept(ALeaf self, ...) runs, `self`
 *   is statically typed ALeaf inside the method body, so visitor.visit(self)
 *   correctly resolves to visit(ALeaf).
 *
 *   If we only defined accept(RightNode self, ...) and relied on
 *   inheritance, an ALeaf instance would call that method — but `self`
 *   inside would be typed RightNode, and visitor.visit(self) would call
 *   visit(RightNode). That is the same subtype-erasure bug as in Java:
 *   the intermediate class silently swallows the concrete type.
 *
 *   One method per concrete class eliminates the ambiguity completely.
 */
@CompileStatic
class NodeExtensions {

    static String accept(RootNode self, Visitor visitor) {
        // `self` is statically typed LeftNode → resolves to visit(LeftNode).
        return visitor.visit(self)
    }

    static String accept(LeftNode self, Visitor visitor) {
        // `self` is statically typed LeftNode → resolves to visit(LeftNode).
        return visitor.visit(self)
    }

    static String accept(RightNode self, Visitor visitor) {
        // Reached only for direct RightNode instances (not ALeaf/BLeaf),
        // because those two have their own more-specific overloads below.
        return visitor.visit(self)
    }

    static String accept(ALeaf self, Visitor visitor) {
        // `self` is statically typed ALeaf → resolves to visit(ALeaf).
        return visitor.visit(self)
    }

    static String accept(BLeaf self, Visitor visitor) {
        // `self` is statically typed BLeaf → resolves to visit(BLeaf).
        return visitor.visit(self)
    }
}

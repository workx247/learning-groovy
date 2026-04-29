package jpatterns.visitor;

/**
 * Main — demonstrates why plain method overloading breaks for a
 * hierarchy with intermediate types, and how the Visitor pattern
 * fixes it.
 *
 * Tree structure:
 *
 *          Node  (interface)
 *         /    \
 *    LeftNode  RightNode  (intermediate — also a direct instance)
 *              /       \
 *           ALeaf     BLeaf
 *
 * Run with:
 *   mvn compile exec:java -pl design-patterns \
 *       -Dexec.mainClass=jpatterns.visitor.Main
 */
public class Main {

    // -------------------------------------------------------------------------
    // PART 1 — Naive approach: plain overloaded static methods.
    //
    // Java resolves overloads at COMPILE TIME using the DECLARED type of
    // the argument, not the runtime type. Consequences:
    //
    //   Node ref = new ALeaf();
    //   naiveDescribe(ref);       // → naiveDescribe(Node) — wrong!
    //
    //   RightNode rref = new ALeaf();
    //   naiveDescribe(rref);      // → naiveDescribe(RightNode) — still wrong
    //
    // The intermediate class RightNode makes this worse: even when the
    // declared type is narrowed to RightNode, ALeaf and BLeaf are still
    // indistinguishable.
    // -------------------------------------------------------------------------

    static void naiveDescribe(Element n) {
        System.out.println("  overload(Node)       — actual runtime type: "
                + n.getClass().getSimpleName());
    }

    static void naiveDescribe(RightNode n) {
        System.out.println("  overload(RightNode)  — actual runtime type: "
                + n.getClass().getSimpleName());
    }

    static void naiveDescribe(ALeaf n) {
        System.out.println("  overload(ALeaf)      — actual runtime type: "
                + n.getClass().getSimpleName());
    }

    static void naiveDescribe(BLeaf n) {
        System.out.println("  overload(BLeaf)      — actual runtime type: "
                + n.getClass().getSimpleName());
    }

    // -------------------------------------------------------------------------

    public static void main(String[] args) {

        // All variables declared at the TOP of the hierarchy (Node).
        // This is the realistic scenario: a method receives a Node and
        // does not know the concrete type at compile time.
        Element left  = new LeftNode();
        Element right = new RightNode();
        Element aLeaf = new ALeaf();
        Element bLeaf = new BLeaf();

        // ---------------------------------------------------------------------
        // DEMO 1a — naive overloading, all declared as Node.
        //
        // Expected (wrong) output: all four print "overload(Node)" because
        // the compiler only sees the declared type Node.
        // ---------------------------------------------------------------------
        System.out.println("=== Naive overloading  [declared type: Node] ===");
        naiveDescribe(left);    // → overload(Node)
        naiveDescribe(right);   // → overload(Node)
        naiveDescribe(aLeaf);   // → overload(Node)
        naiveDescribe(bLeaf);   // → overload(Node)

        System.out.println();

        // ---------------------------------------------------------------------
        // DEMO 1b — narrow the declared type to RightNode.
        //
        // Better, but ALeaf and BLeaf are still indistinguishable:
        // the compiler sees RightNode, picks overload(RightNode) for both.
        // ---------------------------------------------------------------------
        RightNode aAsRight = new ALeaf();
        RightNode bAsRight = new BLeaf();

        System.out.println("=== Naive overloading  [declared type: RightNode] ===");
        naiveDescribe(aAsRight);   // → overload(RightNode) — should be ALeaf
        naiveDescribe(bAsRight);   // → overload(RightNode) — should be BLeaf

        System.out.println();

        // ---------------------------------------------------------------------
        // DEMO 2 — Visitor pattern, all still declared as Node.
        //
        // accept() in each concrete class calls v.visit(this), where `this`
        // has the CONCRETE compile-time type. The correct visit() overload is
        // therefore always selected — regardless of the declared type of the
        // variable the caller holds.
        //
        // This is "simulated double dispatch":
        //   1. Virtual dispatch on accept() → selects the right class's method.
        //   2. Static overload resolution on visit(this) → selects the right
        //      Visitor method, because `this` is typed concretely.
        // ---------------------------------------------------------------------
        System.out.println("=== Visitor pattern    [declared type: Node] ===");
        Visitor printer = new PrintVisitor();

        // remember - they all have static type 'Element':
        left.accept(printer);    // → visit(LeftNode)
        right.accept(printer);   // → visit(RightNode)
        aLeaf.accept(printer);   // → visit(ALeaf)
        bLeaf.accept(printer);   // → visit(BLeaf)
    }
}

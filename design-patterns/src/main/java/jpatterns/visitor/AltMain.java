package jpatterns.visitor;

/**
 * AltMain — same tree, dispatched using Java 21 switch pattern matching
 * (JEP 441, finalized in Java 21) instead of the Visitor pattern.
 *
 * MOTIVATION
 * The classic Visitor requires every element class to implement accept()
 * (the "Element" side of the contract). If the class hierarchy belongs to
 * a library you cannot modify, that is simply not an option. Switch pattern
 * matching dispatches on runtime type without touching the original classes.
 *
 * WHAT THE SWITCH CAN ENFORCE AT COMPILE TIME
 *   Ordering — more specific types (ALeaf, BLeaf) must appear before their
 *   supertype (RightNode). Placing RightNode first would make the ALeaf/BLeaf
 *   arms unreachable; the compiler raises a "dominated case label" error.
 *
 *   Exhaustiveness for SEALED hierarchies — if Element were declared
 *   `sealed permits LeftNode, RightNode`, the compiler could enumerate all
 *   subtypes and verify the arms are complete; no `default` would be needed.
 *
 * WHAT THE SWITCH CANNOT ENFORCE (the gap vs. Visitor)
 *   Exhaustiveness for non-sealed hierarchies — because Element is not
 *   sealed, a `default` arm is required, and a new subtype added to the
 *   hierarchy falls there silently with no compile error.
 *   With Visitor, adding a visit() overload to the Visitor interface
 *   immediately breaks every implementor — a compile error forces you to act.
 *   See the "silent trap" demo in main().
 *
 * HOW TO RUN
 *   mvn compile exec:java -pl design-patterns \
 *       -Dexec.mainClass=jpatterns.visitor.AltMain
 */
public class AltMain {

    // Simulates a new subtype added to the hierarchy by someone who forgot
    // to update the switch statement — used in the silent-trap demo below.
    static class CLeaf extends RightNode { }

    /**
     * Dispatches on the runtime type of node using switch pattern matching.
     * Works without accept() — no changes to the original classes needed.
     *
     * Three things to notice:
     *
     *   1. ORDERING: ALeaf and BLeaf must precede RightNode because they are
     *      subtypes of it. Putting RightNode first is a compile error.
     *
     *   2. WHEN GUARD: a boolean condition appended to any case arm with `when`.
     *      Here it separates a direct RightNode instance from unrecognised
     *      subtypes. Any boolean expression is valid — field checks, method
     *      calls, comparisons.
     *
     *   3. DEFAULT: required because Element is not sealed. The compiler cannot
     *      prove the four explicit arms cover every possible subtype.
     */
    static String describe(Object node) {
        return switch (node) {

            // More specific subtypes first — compiler enforces this ordering.
            case ALeaf a -> "ALeaf  (extends RightNode)";
            case BLeaf b -> "BLeaf  (extends RightNode)";

            // `when` guard: matches only when the runtime class is exactly
            // RightNode, not any subclass of it.
            case RightNode r when r.getClass() == RightNode.class
                             -> "RightNode  (direct instance)";

            // Catches any RightNode subtype the switch does not explicitly list
            // (e.g. the CLeaf we add below). No compile error, no warning.
            case RightNode r -> "Unrecognised RightNode subtype: "
                                    + r.getClass().getSimpleName();

            case LeftNode l  -> "LeftNode";

            default          -> "Unknown type: " + node.getClass().getSimpleName();
        };
    }

    public static void main(String[] args) {

        // All declared as Element — same realistic scenario as Main.
        Element left  = new LeftNode();
        Element right = new RightNode();
        Element aLeaf = new ALeaf();
        Element bLeaf = new BLeaf();

        System.out.println("=== Switch pattern matching  [declared type: Element] ===");
        System.out.println(describe(left));    // → LeftNode
        System.out.println(describe(right));   // → RightNode (direct instance)
        System.out.println(describe(aLeaf));   // → ALeaf
        System.out.println(describe(bLeaf));   // → BLeaf

        System.out.println();

        // -----------------------------------------------------------------------
        // THE SILENT TRAP
        //
        // CLeaf extends RightNode but has no case arm in describe().
        //
        // With Visitor: adding a new type means adding visit(CLeaf) to the
        // Visitor interface, which immediately causes a compile error in every
        // implementor — you are forced to handle it.
        //
        // With switch: CLeaf silently falls through to `case RightNode r`
        // (not even `default` — it IS a RightNode). No compile error, no
        // warning, no crash. The program runs and silently gives the wrong answer.
        // A test would catch it; the compiler won't.
        // -----------------------------------------------------------------------
        System.out.println("=== Silent trap: CLeaf is not in the switch ===");
        Element cLeaf = new CLeaf();
        System.out.println(describe(cLeaf));
        // → Unrecognised RightNode subtype: CLeaf
    }
}

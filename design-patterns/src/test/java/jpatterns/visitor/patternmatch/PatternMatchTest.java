package jpatterns.visitor.patternmatch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatternMatchTest {

    @Test
    void switchDispatchesCorrectly() {
        // All declared as RootNode — same scenario as VisitorTest.
        // The switch matches on runtime type, not declared type.
        jpatterns.visitor.classicvisitor.RootNode left  = new jpatterns.visitor.classicvisitor.LeftNode();
        jpatterns.visitor.classicvisitor.RootNode right = new jpatterns.visitor.classicvisitor.RightNode();
        jpatterns.visitor.classicvisitor.RootNode aLeaf = new jpatterns.visitor.classicvisitor.ALeaf();
        jpatterns.visitor.classicvisitor.RootNode bLeaf = new jpatterns.visitor.classicvisitor.BLeaf();

        assertEquals("LeftNode",                   AltMain.describe(left));
        assertEquals("RightNode  (direct instance)", AltMain.describe(right));
        assertEquals("ALeaf  (extends RightNode)", AltMain.describe(aLeaf));
        assertEquals("BLeaf  (extends RightNode)", AltMain.describe(bLeaf));
    }

    @Test
    void unknownSubtypeFallsThroughSilently() {
        // CLeaf extends RightNode but has no case arm in describe().
        //
        // With the classic Visitor, adding visit(CLeaf) to the Visitor
        // interface would force a compile error in every implementor.
        //
        // With switch, CLeaf silently matches `case RightNode r` —
        // no compile error, no warning. Only a test like this catches it.
        jpatterns.visitor.classicvisitor.RootNode cLeaf = new AltMain.CLeaf();
        assertEquals(
            "Unrecognised RightNode subtype: CLeaf",
            AltMain.describe(cLeaf)
        );
    }
}

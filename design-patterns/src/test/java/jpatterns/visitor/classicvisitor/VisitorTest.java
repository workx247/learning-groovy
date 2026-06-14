package jpatterns.visitor.classicvisitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VisitorTest {

    // All variables declared as RootNode — that is the realistic scenario.
    // The whole point of the Visitor pattern is that accept() dispatches
    // to the right visit() overload DESPITE the declared type being the
    // base interface.

    private final Visitor printer = new PrintVisitor();

    @Test
    void leftNodeDispatchesToVisitLeftNode() {
        RootNode node = new LeftNode();
        assertEquals("PrintVisitor -> LeftNode", node.accept(printer));
    }

    @Test
    void rightNodeDispatchesToVisitRightNode() {
        RootNode node = new RightNode();
        assertEquals("PrintVisitor -> RightNode", node.accept(printer));
    }

    @Test
    void aLeafDispatchesToVisitALeaf() {
        RootNode node = new ALeaf();
        assertEquals("PrintVisitor -> ALeaf", node.accept(printer));
    }

    @Test
    void bLeafDispatchesToVisitBLeaf() {
        RootNode node = new BLeaf();
        assertEquals("PrintVisitor -> BLeaf", node.accept(printer));
    }

    // With Mockito we verify WHICH visit() overload was called, without
    // relying on PrintVisitor at all. This tests the dispatch mechanism
    // itself rather than a specific visitor implementation.

    @Test
    void aLeafAcceptCallsVisitALeaf() {
        Visitor mock = mock(Visitor.class);
        RootNode node = new ALeaf();
        node.accept(mock);
        verify(mock).visit(any(ALeaf.class));
        verifyNoMoreInteractions(mock);
    }

    @Test
    void aLeafDoesNotFallThroughToVisitRightNode() {
        // If ALeaf forgot to override accept(), it would inherit
        // RightNode's implementation where `this` is typed RightNode,
        // causing visit(RightNode) to be called instead. This test
        // would catch that bug.
        Visitor mock = mock(Visitor.class);
        RootNode node = new ALeaf();
        node.accept(mock);
        verify(mock, never()).visit(any(RightNode.class));
    }
}

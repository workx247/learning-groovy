package jpatterns.visitor.nodes;

public class VNodeBuilder {

    public static RootNode mkRootNode() {
        return new RootNode();
    }

    public static RootNode mkLeftNode() {
        return new LeftNode();
    }

    public static RootNode mkRightNode() {
        return new RightNode();
    }

    public static RootNode mkALeaf() {
        return new ALeaf();
    }

    public static RootNode mkBLeaf() {
        return new BLeaf();
    }
}

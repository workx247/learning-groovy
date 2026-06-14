package jpatterns.visitor;

public class Main {

    public static void main(String[] args) {

        Element left  = new LeftNode();
        Element right = new RightNode();
        Element aLeaf = new ALeaf();
        Element bLeaf = new BLeaf();

        System.out.println("=== Visitor pattern    [declared type: Node] ===");
        Visitor printer = new PrintVisitor();

        // remember - they all have static type 'Element':
        left.accept(printer);
        right.accept(printer);
        aLeaf.accept(printer);
        bLeaf.accept(printer);
    }

}

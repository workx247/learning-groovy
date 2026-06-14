package jpatterns.visitor.javaswitch;

import jpatterns.visitor.nodes.ALeaf;
import jpatterns.visitor.nodes.BLeaf;
import jpatterns.visitor.nodes.RootNode;
import jpatterns.visitor.nodes.LeftNode;
import jpatterns.visitor.nodes.RightNode;

public class Main {


    static String describe(RootNode n) {
        switch (n) {
            case LeftNode left -> {return "LeftNode: " + left.toString();}
            case RightNode right -> {
                switch (right) {
                    case ALeaf a -> {return "ALeaf: " + a.toString();}
                    case BLeaf b -> {return "BLeaf: " + b.toString();}
                    case RightNode r -> {return "RightNode: " + r.toString();}
                }
            }
            case RootNode root -> {return "RootNode: " + root.toString();}
            // If you miss ANY case, compilation fails!
        }
    }

    public static void main(String[] args) {
        RootNode root  = new RootNode();
        RootNode left  = new LeftNode();
        RootNode right = new RightNode();
        RootNode aLeaf = new ALeaf();
        RootNode bLeaf = new BLeaf();
        System.out.println(describe(root));
        System.out.println(describe(left));
        System.out.println(describe(right));
        System.out.println(describe(aLeaf));
        System.out.println(describe(bLeaf));
    }

}

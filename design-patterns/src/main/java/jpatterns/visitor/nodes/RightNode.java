package jpatterns.visitor.nodes;

import lombok.ToString;

@ToString
public sealed class RightNode extends RootNode permits ALeaf, jpatterns.visitor.nodes.BLeaf { }

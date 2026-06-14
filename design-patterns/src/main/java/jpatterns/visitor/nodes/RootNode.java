package jpatterns.visitor.nodes;

import lombok.ToString;

// @ToString generates toString() returning "ClassName(field=val, ...)"
// For a class with no fields this produces "RootNode()" — the same
// output as Groovy's @Canonical, which we used before migrating here.
@ToString
public sealed class RootNode permits jpatterns.visitor.nodes.LeftNode, jpatterns.visitor.nodes.RightNode { }

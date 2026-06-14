# Visitor Pattern

The Visitor pattern lets you add new operations to a class hierarchy
**without modifying the classes themselves**. The operation (the "visitor")
lives in its own class and is passed to each node to do its work.

This project contains four implementations of the same idea, each teaching
a different lesson about Java and Groovy dispatch:

| # | Package | Language | What it teaches |
|---|---------|----------|-----------------|
| 1 | `jpatterns.visitor` | Java | Classic Visitor — double dispatch via `accept()` |
| 2 | `jpatterns.visitor.AltMain` | Java 21 | Switch pattern matching as a Visitor alternative |
| 3 | `jpatterns.visitor.javaswitch` | Java 21 | Exhaustive switch on a **sealed** hierarchy |
| 4 | `gpatterns.visitor.extension` | Groovy | Visitor via extension modules (no `accept()` in source) |
| 5 | `gpatterns.visitor.dynamicdispatch` | Groovy | Visitor via Groovy's runtime method dispatch |

---

## The Node Hierarchy

All implementations share the same tree structure:

```
RootNode  (sealed)
├── LeftNode          (final leaf)
└── RightNode  (sealed)
    ├── ALeaf         (final leaf)
    └── BLeaf         (final leaf)
```

The hierarchy is **sealed**: `RootNode` only permits `LeftNode` and
`RightNode`; `RightNode` only permits `ALeaf` and `BLeaf`. This means
the compiler knows every possible subtype — which becomes important for
exhaustiveness checking.

The Groovy node classes (`gpatterns.visitor.nodes`) deliberately have
**no `accept()` method** in their source. They simulate classes from a
third-party library you cannot modify. The Groovy visitor approaches must
work around this constraint.

---

## 1. Classic Java Visitor (`jpatterns.visitor`)

**Files:**
- `Element.java` — interface every node implements; declares `accept()`
- `Visitor.java` — interface every visitor implements; one `visit()` per concrete type
- `LeftNode.java`, `RightNode.java`, `ALeaf.java`, `BLeaf.java` — the tree
- `PrintVisitor.java` — a concrete visitor that prints which type it received
- `Main.java` — runs the demo

### The core idea: double dispatch

Java resolves overloaded methods at **compile time**, based on the
*declared* (static) type of the argument — not the runtime type. So if
you have:

```java
Element node = new ALeaf();  // declared type is Element
printer.visit(node);          // compiler picks visit(Element) — wrong!
```

The Visitor pattern works around this with a two-step trick:

**Step 1** — the caller invokes `accept()` on the node:
```java
node.accept(printer);
```
`accept()` is a virtual method, so Java dispatches to the *runtime* class
(`ALeaf.accept()`). That is the first dispatch.

**Step 2** — inside `ALeaf.accept()`, `this` has the compile-time type
`ALeaf`, so calling `visitor.visit(this)` lets the compiler pick the
correct overload:
```java
// Inside ALeaf:
public void accept(Visitor visitor) {
    visitor.visit(this);  // 'this' is statically typed ALeaf here
}
```
That is the second dispatch (static overload resolution). Together they
are called **double dispatch**.

### Why every subclass must override `accept()`

`ALeaf` extends `RightNode`. If `ALeaf` did *not* override `accept()`,
it would inherit `RightNode`'s implementation. Inside that inherited
method, `this` would carry the compile-time type `RightNode`, not `ALeaf`.
The call `visitor.visit(this)` would silently dispatch to
`visit(RightNode)`, losing ALeaf's identity with no error or warning.

Overriding `accept()` in every concrete class is not boilerplate — it is
the essential mechanism.

### What you gain: compile-time safety

The `Visitor` interface declares one `visit()` per concrete type:
```java
public interface Visitor {
    void visit(LeftNode node);
    void visit(RightNode node);
    void visit(ALeaf leaf);
    void visit(BLeaf leaf);
}
```
Add a new node type → add `visit(NewType)` to the interface → the
compiler immediately flags every existing `Visitor` implementation that
hasn't been updated. You cannot accidentally forget to handle a new type.

### How to run

```bash
mvn compile exec:java -pl design-patterns \
    -Dexec.mainClass=jpatterns.visitor.Main
```

Expected output:
```
=== Visitor pattern    [declared type: Node] ===
  PrintVisitor → LeftNode
  PrintVisitor → RightNode  (intermediate, direct instance)
  PrintVisitor → ALeaf
  PrintVisitor → BLeaf
```

---

## 2. Java 21 Switch Pattern Matching (`jpatterns.visitor.AltMain`)

Java 21 (JEP 441) added pattern matching in switch expressions. This lets
you dispatch on runtime type *without* needing `accept()` in the node
classes — useful when the hierarchy belongs to code you cannot modify.

```java
static String describe(Object node) {
    return switch (node) {
        case ALeaf a    -> "ALeaf";
        case BLeaf b    -> "BLeaf";
        case RightNode r when r.getClass() == RightNode.class
                        -> "RightNode (direct instance)";
        case RightNode r -> "Unrecognised RightNode subtype";
        case LeftNode l  -> "LeftNode";
        default          -> "Unknown: " + node.getClass().getSimpleName();
    };
}
```

Two things to notice:

**Ordering matters.** More specific types (`ALeaf`, `BLeaf`) must appear
*before* their supertype (`RightNode`). Placing `RightNode` first would
make the `ALeaf`/`BLeaf` arms unreachable — the compiler raises a
"dominated case label" error.

**`when` guards.** A boolean condition appended to any case arm. Here it
distinguishes a direct `RightNode` instance from its subtypes.

### The silent trap

Because `Element` is not sealed, a `default` arm is required, and a new
subtype falls there *silently*. `AltMain` demonstrates this with an inner
class `CLeaf extends RightNode`: the switch handles it as
"Unrecognised RightNode subtype" with no error and no warning.

With the classic Visitor, adding `CLeaf` forces you to add
`visit(CLeaf)` to the `Visitor` interface, which *immediately breaks
compilation* in every existing visitor — the compiler forces you to act.

**Rule of thumb:**
- Use **switch pattern matching** when you own the switch but not the
  class hierarchy (third-party types, no `accept()`).
- Use **classic Visitor** when you own the hierarchy and want the
  compiler to enforce completeness as the hierarchy grows.

### How to run

```bash
mvn compile exec:java -pl design-patterns \
    -Dexec.mainClass=jpatterns.visitor.AltMain
```

---

## 3. Sealed Hierarchy + Switch (`jpatterns.visitor.javaswitch`)

This variant uses a **sealed** hierarchy (`jpatterns.visitor.nodes`):

```java
public sealed class RootNode permits LeftNode, RightNode {}
public sealed class RightNode extends RootNode permits ALeaf, BLeaf {}
public final class LeftNode  extends RootNode {}
public final class ALeaf     extends RightNode {}
public final class BLeaf     extends RightNode {}
```

With a sealed hierarchy the compiler knows every permitted subtype. A
switch over a sealed type is **exhaustive without a `default` arm** — if
you leave out a case, compilation fails. This closes the silent-trap gap
from the previous example:

```java
static String describe(RootNode n) {
    switch (n) {
        case LeftNode  left  -> { return "LeftNode";  }
        case RightNode right -> {
            switch (right) {
                case ALeaf a     -> { return "ALeaf";      }
                case BLeaf b     -> { return "BLeaf";      }
                case RightNode r -> { return "RightNode";  }
            }
        }
        case RootNode root -> { return "RootNode"; }
        // If you miss ANY case, compilation fails.
    }
}
```

The comment at the bottom is the key point: unlike a non-sealed switch,
you cannot silently miss a type here.

### How to run

```bash
mvn compile exec:java -pl design-patterns \
    -Dexec.mainClass=jpatterns.visitor.javaswitch.Main
```

---

## 4. Groovy: Extension Modules (`gpatterns.visitor.extension`)

**Files:**
- `Visitor.groovy` — same interface concept as Java
- `NodeExtensions.groovy` — retroactively adds `accept()` to every node
- `PrintVisitor.groovy` — a concrete visitor
- `Main.groovy` — runs the demo

The Groovy node classes have no `accept()` in their source. Extension
modules let you add methods to existing classes without modifying them.

### How extension modules work

Groovy scans the classpath at startup for a descriptor at:
```
META-INF/groovy/org.codehaus.groovy.runtime.ExtensionModule
```

That file names one or more extension classes. Every **static method** in
those classes becomes callable as an **instance method** on the type of its
first parameter (`self`):

```groovy
// In NodeExtensions — a static method with a 'self' first parameter:
static String accept(ALeaf self, Visitor visitor) {
    return visitor.visit(self)  // 'self' is statically typed ALeaf
}
```

This becomes callable as:
```groovy
myALeaf.accept(visitor)  // no changes to ALeaf needed
```

### Why one method per concrete class is still required

This is the same lesson as the Java `accept()` override, applied to
extension methods.

If you only defined `accept(RightNode self, ...)` and relied on
inheritance, an `ALeaf` instance would still call that method — but
inside it, `self` would be typed `RightNode`, so `visitor.visit(self)`
would dispatch to `visit(RightNode)`. The concrete subtype identity would
be lost, silently.

One extension method per concrete class solves this: when `accept(ALeaf
self, ...)` runs, `self` is statically typed `ALeaf`, and
`visitor.visit(self)` correctly picks `visit(ALeaf)`.

### How to run

```bash
mvn compile exec:java -pl design-patterns \
    -Dexec.mainClass=gpatterns.visitor.extension.Main
```

---

## 5. Groovy: Dynamic Dispatch (`gpatterns.visitor.dynamicdispatch`)

**Files:**
- `DispatchVisitor.groovy` — concrete visitor with one `visit()` per type
- `Main.groovy` — runs the demo

### The key difference from Java

Groovy resolves overloaded methods at **runtime**, based on the actual
type of the argument. Java resolves them at **compile time**, based on the
declared (static) type.

This means that in Groovy you can call:
```groovy
RootNode node = new ALeaf()     // declared type is RootNode
visitor.visit(node)              // Groovy picks visit(ALeaf) at runtime!
```

Java would pick `visit(RootNode)` here (the only overload that matches
the declared type). Groovy ignores the declared type and dispatches on
the runtime type — `ALeaf` — so it picks `visit(ALeaf)` correctly.

**This is why the classic Visitor's `accept()` trick is unnecessary in
Groovy.** The `this`-trick in `accept()` exists purely to tell Java's
compile-time overload resolution the concrete type. Groovy's runtime
dispatch already does that automatically.

### @CompileDynamic vs @CompileStatic

`Main.groovy` is annotated `@CompileDynamic`. This is required because
Groovy's static type checker (`@CompileStatic`) verifies overloads at
compile time — the same way Java does — and would fail to resolve
`visitor.visit(node)` when `node` is declared as `RootNode`.

`@CompileDynamic` (which is also the default when no annotation is
present) defers dispatch to runtime and lets Groovy's MOP (Meta-Object
Protocol) find the right overload.

`DispatchVisitor` itself is `@CompileStatic` because its `visit()` methods
have fully specific parameter types — no ambiguity for the static checker.

### How to run

```bash
mvn compile exec:java -pl design-patterns \
    -Dexec.mainClass=gpatterns.visitor.dynamicdispatch.Main
```

---

## Summary: What Each Approach Costs and Buys

| | Classic Java Visitor | Java switch (non-sealed) | Java switch (sealed) | Groovy extension | Groovy dynamic dispatch |
|---|---|---|---|---|---|
| Needs `accept()` in node? | Yes | No | No | No | No |
| Compile-time completeness? | Yes | No (silent default) | Yes | Partial | No |
| Handles third-party types? | No | Yes | Yes | Yes | Yes |
| Needs `@CompileDynamic`? | — | — | — | Caller only | Caller only |

### The one recurring lesson

Across every implementation, the same trap appears in a different costume:
**the intermediate class (`RightNode`) silently swallowing the concrete
subtype (`ALeaf`, `BLeaf`) when dispatch is done on the wrong type**.

- **Java Visitor** — fixed by overriding `accept()` in every concrete class.
- **Java switch** — fixed by listing more-specific types before supertypes.
- **Groovy extensions** — fixed by one extension method per concrete class.
- **Groovy dynamic dispatch** — avoided entirely, because runtime dispatch
  never loses the concrete type.

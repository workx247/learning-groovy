package patterns

import groovy.transform.CompileDynamic

/**
 * HelloPattern.groovy — starter file for the design-patterns module.
 *
 * Demonstrates the Singleton creational pattern using Groovy's built-in
 * @Singleton AST transformation.
 *
 * WHY @Singleton?
 * In Java you write a private constructor + static field + getInstance()
 * by hand (~10 lines of boilerplate). Groovy's @Singleton annotation
 * generates all of that at compile time via an AST transformation.
 *
 * WHAT IS AN AST TRANSFORMATION?
 * Groovy compiles source to an AST (Abstract Syntax Tree) before generating
 * bytecode. Transformations let annotations inject or rewrite nodes in that
 * tree — compile-time metaprogramming. @Singleton, @Immutable, @ToString
 * all work this way.
 *
 * HOW TO RUN from terminal:
 *   mvn compile exec:java -pl design-patterns \
 *       -Dexec.mainClass=patterns.HelloPattern
 *
 * Or use the green ▶ button in IntelliJ.
 */
// Generates: private constructor + static `instance` field + getInstance():
@Singleton
@CompileDynamic
class HelloPattern {

    /**
     * Returns a greeting string.
     *
     * ${name} is Groovy's GString interpolation — it evaluates the
     * expression at runtime, similar to Java's String.format() but
     * more concise.
     */
    String greet(String name) {
        return "Hello, ${name}! Welcome to Design Patterns in Groovy."
    }

    /**
     * Entry point.
     *
     * Access the singleton via `.instance` (the generated property).
     * Calling `new HelloPattern()` would throw a RuntimeException because
     * @Singleton makes the constructor private.
     */
    static void main(String[] args) {
        println HelloPattern.instance.greet('World')
    }
}

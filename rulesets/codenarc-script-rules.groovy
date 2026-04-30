/*
 * codenarc-script-rules.groovy
 *
 * CodeNarc ruleset for the `scripts` module.
 * Extends the shared rules with relaxations that are appropriate for
 * standalone CLI scripts but would be too permissive for library code:
 *
 *   - ClassJavadoc      : scripts are self-contained; a Javadoc header adds
 *                         no value when the class IS the script.
 *   - LineLength        : picocli annotations and long format strings make
 *                         80-char lines impractical in CLI code.
 *   - SystemErrPrint    : scripts write diagnostics to stderr by design.
 *   - CatchException    : scripts often need a top-level catch-all to print a
 *                         friendly error instead of a stack trace.
 *   - IfStatementBraces : single-expression guard returns (e.g. early return
 *                         from a short helper) are idiomatic and readable
 *                         without braces in script code.
 *   - IfStatementCouldBeTernary : multi-branch early-return chains (like a
 *                         fileTypeChar dispatcher) are clearer as if/return
 *                         than as a nested ternary.
 *   - SystemExit        : scripts are entry points; calling System.exit() is
 *                         the correct way to propagate an exit code to the shell.
 *
 * Full rule index: https://codenarc.org/codenarc-rule-index.html
 *
 * STRATEGY: blacklist (same as codenarc-rules.groovy)
 * All rules from each included category are ACTIVE by default.
 * Rules that do not apply are disabled inline.
 *
 * TO DISABLE A RULE:    add it inside the ruleset() closure: RuleName { enabled = false }
 * TO CONFIGURE A RULE:  same place:                          RuleName { someProperty = value }
 */

ruleset {

    description 'CodeNarc rules for the scripts module'

    // Skipped categories:
    //   grails.xml  — not a Grails project
    //   jenkins.xml — not a Jenkins pipeline project
    //   jdbc.xml    — not using JDBC directly; add if that changes

    ruleset('rulesets/basic.xml')

    ruleset('rulesets/braces.xml') {

        IfStatementBraces {
            // Single-expression guard returns are idiomatic and readable in
            // script helpers without braces; enforcing them adds visual noise.
            enabled = false
        }

    }

    ruleset('rulesets/comments.xml') {

        ClassJavadoc {
            // The class IS the script entry point; a Javadoc header duplicates
            // the @Command description annotation and adds no value.
            enabled = false
        }

    }

    ruleset('rulesets/concurrency.xml')

    ruleset('rulesets/convention.xml') {

        NoDef {
            // `def` is a first-class Groovy feature, not a shortcut to avoid.
            // Disabling lets you write idiomatic dynamic Groovy freely.
            // Re-enable if you want to practise fully-typed / @CompileStatic code.
            enabled = false
        }
        VariableTypeRequired {
            // `def x = 42` is idiomatic Groovy; explicit types are optional.
            enabled = false
        }
        FieldTypeRequired {
            // Same reasoning as VariableTypeRequired.
            enabled = false
        }
        MethodReturnTypeRequired {
            // Groovy infers return types; requiring them adds noise for learners.
            enabled = false
        }
        PublicMethodsBeforeNonPublicMethods {
            // Method ordering is a style preference, not a correctness issue.
            enabled = false
        }
        StaticMethodsBeforeInstanceMethods {
            // Same reasoning as PublicMethodsBeforeNonPublicMethods.
            enabled = false
        }
        IfStatementCouldBeTernary {
            // Multi-branch early-return chains (e.g. a type-char dispatcher)
            // are clearer as sequential if/return than as nested ternaries.
            enabled = false
        }

    }

    ruleset('rulesets/design.xml')

    ruleset('rulesets/dry.xml') {

        DuplicateNumberLiteral {
            // Flags using `0` twice, `1` twice, etc. The signal-to-noise ratio
            // is very low — common literals appear everywhere for unrelated reasons.
            enabled = false
        }
        DuplicateStringLiteral {
            // Flags the same string value appearing more than once.
            enabled = false
        }
        DuplicateListLiteral {
            // Flags `[]` or `[1, 2]` used in two places.
            enabled = false
        }
        DuplicateMapLiteral {
            // Flags `[:]` or `[a: 1]` used in two places.
            enabled = false
        }

    }

    ruleset('rulesets/enhanced.xml')

    ruleset('rulesets/exceptions.xml') {

        CatchException {
            // Scripts need a top-level catch-all to print a friendly error
            // message to stderr instead of dumping a raw stack trace.
            enabled = false
        }

    }

    ruleset('rulesets/formatting.xml') {

        LineLength {
            // picocli annotations, format strings, and error messages make
            // 80-char lines impractical in CLI script code. Disabled entirely
            // rather than bumped to a higher limit, since scripts are not
            // library code and line length is not a readability concern here.
            enabled = false
        }
        ClassStartsWithBlankLine {
            // Subjective style preference; conflicts with many formatter defaults.
            enabled = false
        }
        ClassEndsWithBlankLine {
            // Same reasoning as ClassStartsWithBlankLine.
            enabled = false
        }
        BlockStartsWithBlankLine {
            // Same reasoning as ClassStartsWithBlankLine.
            enabled = false
        }
        BlockEndsWithBlankLine {
            // Same reasoning as ClassStartsWithBlankLine.
            enabled = false
        }

    }

    ruleset('rulesets/generic.xml') {

        // All generic rules require project-specific configuration to do anything
        // useful (e.g. a regex to forbid, or a class name to disallow).
        // Enable and configure them when you have a concrete need.
        StatelessClass          { enabled = false }  // needs: which classes must be stateless
        IllegalClassMember      { enabled = false }  // needs: which member pattern to forbid
        IllegalPackageReference { enabled = false }  // needs: which package to forbid
        IllegalRegex            { enabled = false }  // needs: the regex to forbid
        IllegalString           { enabled = false }  // needs: the string to forbid
        IllegalSubclass         { enabled = false }  // needs: which superclass to forbid
        RequiredRegex           { enabled = false }  // needs: the regex that must be present
        RequiredString          { enabled = false }  // needs: the string that must be present

    }

    ruleset('rulesets/groovyism.xml')

    ruleset('rulesets/imports.xml')

    ruleset('rulesets/junit.xml')       // silent until you write JUnit tests

    ruleset('rulesets/logging.xml') {

        Println {
            // `println` is the normal way to produce output in a Groovy learning
            // project and in scripts. A production app should use a proper logger,
            // but flagging every println here would be pure noise.
            enabled = false
        }
        SystemErrPrint {
            // Scripts write diagnostics to stderr by design; this is the correct
            // pattern for CLI tools (stderr for errors, stdout for output).
            enabled = false
        }

    }

    ruleset('rulesets/naming.xml')

    ruleset('rulesets/security.xml') {

        SystemExit {
            // Scripts are entry points; System.exit() is the correct mechanism
            // for propagating an exit code to the calling shell.
            enabled = false
        }

    }

    ruleset('rulesets/serialization.xml')

    ruleset('rulesets/size.xml')

    ruleset('rulesets/unnecessary.xml') {

        UnnecessaryReturnKeyword {
            // Explicit `return` is redundant in Groovy (last expression is
            // returned implicitly), but it aids readability — especially when
            // learning — by making the intent unmistakable.
            enabled = false
        }

    }

    ruleset('rulesets/unused.xml')

}

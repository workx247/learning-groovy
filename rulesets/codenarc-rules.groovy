/*
 * codenarc-rules.groovy
 *
 * Shared CodeNarc ruleset for all modules in this project.
 * Written in the Groovy DSL format (alternative to XML).
 *
 * STRATEGY: blacklist
 * All rules from each included category are ACTIVE by default.
 * Rules that do not apply or are too noisy are disabled inline within the
 * ruleset() call that loaded them — this is the correct DSL form; a
 * top-level `RuleName { enabled = false }` block does NOT reliably
 * configure rules that came from an included file.
 *
 * Full rule index: https://codenarc.org/codenarc-rule-index.html
 *
 * PRIORITY LEVELS:
 *   1 = errors    — likely bugs or very bad practice
 *   2 = warnings  — code smell or non-idiomatic Groovy
 *   3 = info      — style suggestions
 *
 * TO DISABLE A RULE:    add it inside the ruleset() closure: RuleName { enabled = false }
 * TO CONFIGURE A RULE:  same place:                          RuleName { someProperty = value }
 * TO ADD A CATEGORY:    ruleset('rulesets/jdbc.xml')
 */

ruleset {

    description 'Shared CodeNarc rules for learning-groovy'

    // Skipped categories:
    //   grails.xml  — not a Grails project
    //   jenkins.xml — not a Jenkins pipeline project
    //   jdbc.xml    — not using JDBC directly; add if that changes

    ruleset('rulesets/basic.xml')

    ruleset('rulesets/braces.xml')

    ruleset('rulesets/comments.xml')

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

    ruleset('rulesets/exceptions.xml')

    ruleset('rulesets/formatting.xml') {

        LineLength {
            // 80 chars (the default) is strict for modern screens.
            // 120 is a common modern limit that still discourages very long lines.
            length = 80
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

    }

    ruleset('rulesets/naming.xml')

    ruleset('rulesets/security.xml')

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

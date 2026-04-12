---
Build and lint the whole project:
mvn verify
Compiles all modules and runs CodeNarc on each one that has sources.

Build only (no linting):
mvn compile

Lint a single module independently:
mvn verify -pl design-patterns
mvn verify -pl scripts

Build a single module independently:
mvn compile -pl design-patterns
mvn compile -pl scripts

Skip linting when you just want a fast build:
mvn verify -pl design-patterns -Dskip.codenarc=true
Note: the -Dskip flag above won’t work out of the box since our script doesn’t read that property yet — but if you want that feature I can add a System.getProperty check to the script,
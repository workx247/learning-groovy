#!/usr/bin/env groovy

// When run directly with `groovy Scan.groovy`, Grape resolves picocli at
// runtime. When compiled via Maven, the dependency comes from pom.xml instead.
@Grab('info.picocli:picocli:4.7.6')

import groovy.transform.CompileStatic
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Callable
import java.util.stream.Stream

// @CompileStatic turns off Groovy's dynamic dispatch and enforces Java-style
// static typing. This catches type errors at compile time instead of runtime,
// and also means we must use ProcessBuilder for subprocesses (the dynamic
// `['cmd'].execute()` GDK extension is not statically resolvable).
@CompileStatic
@Command(
    name = 'scan',
    mixinStandardHelpOptions = true,
    description = 'Scan pages one by one and compose them into a multi-page PDF.'
)
class Scan implements Callable<Integer> {

    // picocli reads this annotation and wires a --output-dir flag whose string
    // value is automatically converted to a java.nio.file.Path (built-in
    // picocli converter). The default is the directory where you launch the
    // script.
    @Option(
        names = ['--output-dir'],
        paramLabel = 'DIR',
        description = 'Directory to write the final PDF. Default: current directory.'
    )
    Path outputDir = Paths.get('.').toAbsolutePath().normalize()

    final int resolution = 150

    // A single BufferedReader wraps System.in for the full lifecycle of the
    // command. Sharing one reader across all prompts prevents bytes being
    // silently consumed and lost between calls.
    private final BufferedReader stdin =
            new BufferedReader(new InputStreamReader(System.in))

    // Filled by scanLoop() in scan order; consumed by composePdf().
    // Keeping it as a field avoids passing it between every helper method.
    private final List<Path> scannedPages = []

    // -------------------------------------------------------------------------
    // Entry point — called by picocli after it has injected all options
    // -------------------------------------------------------------------------

    @Override
    Integer call() throws IOException, InterruptedException {
        if (!checkPrerequisites()) {
            return 1
        }

        // Create a fresh temporary directory for this session's JPEG files.
        // The OS chooses the name (e.g. /tmp/scan-1234567890); we never hard-
        // code /tmp so the script also works on macOS and Windows.
        Path tmpDir = Files.createTempDirectory('scan-')
        println("Temporary working directory: ${tmpDir}")

        // Ask for the final PDF filename up front so an accidental typo can be
        // caught before any scanning work is done.
        print('Enter the output PDF filename (without .pdf extension): ')
        System.out.flush()
        String rawName = stdin.readLine()
        String pdfName = rawName != null ? rawName.trim() : ''
        if (pdfName.empty) {
            System.err.println('scan: no filename provided — aborting.')
            return 1
        }

        Path outputPdf = outputDir.resolve("${pdfName}.pdf")

        // The finally block runs whether the scan loop exits normally, via
        // abort, or because an unexpected exception escaped. This guarantees
        // the cleanup prompt is always offered.
        try {
            boolean compose = scanLoop(tmpDir)
            if (compose) {
                composePdf(outputPdf)
            }
        } finally {
            cleanup(tmpDir)
        }

        return 0
    }

    // -------------------------------------------------------------------------
    // Check that the external tools we shell out to are actually on PATH
    // -------------------------------------------------------------------------

    private static boolean checkPrerequisites() throws IOException, InterruptedException {
        // `which` exits 0 if the tool is found, non-zero otherwise.
        // We redirect both stdout and stderr to DISCARD because we only care
        // about the exit code, not the output.
        for (String tool : ['scanimage', 'gm']) {
            ProcessBuilder pb = new ProcessBuilder('which', tool)
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD)
            pb.redirectError(ProcessBuilder.Redirect.DISCARD)
            int exitCode = pb.start().waitFor()
            if (exitCode != 0) {
                System.err.println(
                    "scan: required tool '${tool}' not found on PATH. " +
                    'Install it and try again.'
                )
                return false
            }
        }
        return true
    }

    // -------------------------------------------------------------------------
    // Interactive loop — scan pages until the user types 'done' or 'abort'
    // -------------------------------------------------------------------------

    // Returns true when the caller should proceed to compose a PDF,
    // false when the user aborted and no PDF should be created.
    private boolean scanLoop(Path tmpDir) throws IOException, InterruptedException {
        int pageNum = 1

        for (;;) {
            // We use print() (not println) so the cursor stays on the same
            // line and the user types their answer right after the prompt.
            print(
                "Page ${pageNum}: press Enter to scan, " +
                "type 'done' to compose PDF, or 'abort' to quit: "
            )
            System.out.flush()

            String input = stdin.readLine()
            // readLine() returns null at EOF (Ctrl-D / end of piped input).
            // Treat that the same as 'abort' so the script exits cleanly.
            if (input == null) {
                println()
                return false
            }

            String trimmed = input.trim().toLowerCase(Locale.ROOT)

            if (trimmed == 'abort') {
                println('Aborting. No PDF will be created.')
                return false
            }

            if (trimmed == 'done') {
                // Guard: composing with zero pages makes no sense.
                if (scannedPages.empty) {
                    println("No pages scanned yet — scan at least one page first, or type 'abort'.")
                    continue
                }
                return true
            }

            // Any other input — including a bare Enter — means "scan this page".
            Path pageFile = tmpDir.resolve("p${pageNum}.jpg")
            boolean ok = runScan(pageFile)
            if (ok) {
                scannedPages.add(pageFile)
                println("  Page ${pageNum} scanned → ${pageFile.fileName}")
                pageNum++
            } else {
                // Non-fatal: let the user retry the same page number.
                System.err.println("scan: scanimage failed for page ${pageNum}. Check the scanner and try again.")
            }
        }
    }

    // -------------------------------------------------------------------------
    // Run scanimage for one page, writing output to a JPEG file
    // -------------------------------------------------------------------------

    private boolean runScan(Path outputFile) throws IOException, InterruptedException {
        // scanimage writes the raw image bytes to stdout, so we redirect
        // stdout to the target file via ProcessBuilder. This is the correct
        // @CompileStatic approach — the OS-level redirect handles the byte
        // transfer without us touching any Java I/O streams.
        //
        // Options:
        //   -x 210    page width in mm  (A4)
        //   -y 297    page height in mm (A4)
        //   --format=jpeg   output format (note: the shell command had a typo
        //                   '--format=jped'; corrected here to 'jpeg')
        ProcessBuilder pb = new ProcessBuilder(
            'scanimage',
            '-x', '210',
            '-y', '297',
            "--resolution=${resolution}",
            '--format=jpeg'
        )
        pb.redirectOutput(outputFile.toFile())
        // Discard scanner-driver chatter on stderr to keep the console clean.
        pb.redirectError(ProcessBuilder.Redirect.DISCARD)

        return pb.start().waitFor() == 0
    }

    // -------------------------------------------------------------------------
    // Compose all scanned JPEGs into a single PDF with GraphicsMagick
    // -------------------------------------------------------------------------

    private void composePdf(Path outputPdf) throws IOException, InterruptedException {
        // Build the command list manually (not with collect{}) because explicit
        // for-loops are easier for @CompileStatic to type-check than closures.
        List<String> cmd = ['gm', 'convert']
        for (Path page : scannedPages) {
            cmd.add(page.toString())
        }
        cmd.add(outputPdf.toString())

        println("Composing ${scannedPages.size()} page(s) into: ${outputPdf}")

        // inheritIO() lets gm's output flow through to the terminal so the user
        // sees any GraphicsMagick progress messages or error details directly.
        ProcessBuilder pb = new ProcessBuilder(cmd)
        pb.inheritIO()
        int exitCode = pb.start().waitFor()

        if (exitCode != 0) {
            System.err.println("scan: gm convert failed with exit code ${exitCode}.")
        } else {
            println("PDF written to: ${outputPdf}")
        }
    }

    // -------------------------------------------------------------------------
    // Offer to delete temporary JPEG files after the session
    // -------------------------------------------------------------------------

    private void cleanup(Path tmpDir) {
        print('Delete temporary scan files? [y/N]: ')
        System.out.flush()

        String answer
        try {
            String line = stdin.readLine()
            answer = line != null ? line.trim().toLowerCase(Locale.ROOT) : 'n'
        } catch (Exception ignored) {
            // If stdin is broken (e.g. the script was killed mid-prompt),
            // default to keeping the files rather than silently deleting them.
            answer = 'n'
        }

        if (answer == 'y') {
            // Files.walk returns a depth-first Stream<Path>. Reversing the
            // sort order ensures children are deleted before their parent
            // directory — the standard NIO idiom for recursive deletion.
            try (Stream<Path> stream = Files.walk(tmpDir)) {
                stream.sorted(Comparator.reverseOrder())
                      .forEach { Path p -> Files.deleteIfExists(p) }
            } catch (Exception e) {
                System.err.println("scan: could not fully remove temp directory: ${e.message}")
            }
            println('Temporary files deleted.')
        } else {
            println("Temporary files kept in: ${tmpDir}")
        }
    }

    // -------------------------------------------------------------------------
    // Main — picocli entry point
    // -------------------------------------------------------------------------

    static void main(String[] args) {
        // CommandLine parses args, injects them into a Scan instance, calls
        // Scan.call(), and returns the integer exit code. System.exit()
        // propagates that code to the shell (0 = success, non-zero = error).
        System.exit(new CommandLine(new Scan()).execute(args))
    }
}

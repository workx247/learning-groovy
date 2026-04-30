#!/usr/bin/env groovy
@Grab('info.picocli:picocli:4.7.6')

import groovy.transform.CompileStatic
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.PosixFileAttributes
import java.nio.file.attribute.PosixFilePermissions
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable

@CompileStatic
@Command(
    name = 'ls',
    mixinStandardHelpOptions = true,
    description = 'A small ls clone supporting -l and -h.'
)
class Ls implements Callable<Integer> {

    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm')

    @Option(names = '-l', description = 'Use a long listing format.')
    boolean longFormat

    @Option(names = '-h',
            description = 'With -l, print sizes in human readable format.')
    boolean humanReadable

    @Option(names = '-a', description = 'With -l, print hidden files as well.')
    boolean printHidden

    @Parameters(
        arity = '0..*',
        paramLabel = 'PATH',
        description = 'Files or directories to list. Defaults to current directory.'
    )
    List<Path> paths = []

    @Override
    Integer call() {
        if (humanReadable && !longFormat) {
            System.err.println("ls: option '-h' only makes sense together with '-l'")
            return 2
        }

        List<Path> targets = paths ?: [Paths.get('.')]

        boolean multiple = targets.size() > 1
        boolean hadError = false

        for (int i = 0; i < targets.size(); i++) {
            Path target = targets[i]

            if (multiple) {
                if (i > 0) {
                    println()
                }
                println("${target}:")
            }

            try {
                if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                    System.err.println("ls: cannot access '${target}': No such file or directory")
                    hadError = true
                    continue
                }

                if (Files.isDirectory(target, LinkOption.NOFOLLOW_LINKS)) {
                    listDirectory(target)
                } else {
                    printEntry(target, target.fileName ?: target)
                }
            } catch (Exception e) {
                System.err.println("ls: cannot access '${target}': ${e.message}")
                hadError = true
            }
        }

        return hadError ? 1 : 0
    }

    private void listDirectory(Path dir) throws IOException {
        List<Path> entries = []

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (printHidden || !entry.fileName.toString().startsWith('.')) {
                    entries.add(entry)
                }
            }
        }

        entries.sort { Path a, Path b ->
            a.fileName.toString() <=> b.fileName.toString()
        }

        for (Path entry : entries) {
            printEntry(entry, entry.fileName)
        }
    }

    private void printEntry(Path path, Path displayName) {
        if (!longFormat) {
            println(displayName.toString())
            return
        }

        BasicFileAttributes attrs =
            Files.readAttributes(path, BasicFileAttributes, LinkOption.NOFOLLOW_LINKS)

        String typeChar = fileTypeChar(attrs)
        String perms = permissionString(path)
        String sizeText = humanReadable ? humanSize(attrs.size()) : String.valueOf(attrs.size())
        String modified = TS_FMT.format(
            LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault())
        )

        String name = displayName
        if (Files.isSymbolicLink(path)) {
            try {
                name += " -> ${Files.readSymbolicLink(path)}"
            } catch (Exception ignored) {
            }
        }

        println(String.format('%s%s %10s %s %s', typeChar, perms, sizeText, modified, name))
    }

    private static String fileTypeChar(BasicFileAttributes attrs) {
        if (attrs.directory) return 'd'
        if (attrs.symbolicLink) return 'l'
        if (attrs.regularFile) return '-'

        return '?'
    }

    private static String permissionString(Path path) {
        try {
            PosixFileAttributes attrs =
                Files.readAttributes(path, PosixFileAttributes, LinkOption.NOFOLLOW_LINKS)
            return PosixFilePermissions.toString(attrs.permissions())
        } catch (UnsupportedOperationException ignored) {
            return '---------'
        }
    }

    private static String humanSize(long bytes) {
        if (bytes < 1024) return "${bytes}B"

        final String[] units = ['K', 'M', 'G', 'T', 'P', 'E']
        BigDecimal value = (BigDecimal) bytes
        int unit = -1

        while (value >= 1024 && unit < units.length - 1) {
            value /= 1024
            unit++
        }

        return value >= 10
            ? String.format('%.0f%s', value, units[unit])
            : String.format('%.1f%s', value, units[unit])
    }

    static void main(String[] args) {
        System.exit(new CommandLine(new Ls()).execute(args))
    }
}

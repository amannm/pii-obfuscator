package systems.cauldron.utility;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileObfuscator {

    private final Path source;
    private final String delimiter;

    public FileObfuscator(Path source, String delimiter) {
        this.source = source;
        this.delimiter = delimiter;
    }

    public void scan(RecordKeyScanner scanner) throws IOException {
        Files.lines(source, StandardCharsets.UTF_8)
                .map(line -> line.split(delimiter, -1))
                .forEach(scanner::scan);
    }

    public void rewrite(RecordKeyRewriter rewriter, Path destination) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(destination, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            Files.lines(source, StandardCharsets.UTF_8)
                    .map(line -> line.split(delimiter, -1))
                    .peek(rewriter::rewrite)
                    .map(fields -> String.join(delimiter, fields))
                    .forEach(str -> {
                        try {
                            writer.write(str);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}

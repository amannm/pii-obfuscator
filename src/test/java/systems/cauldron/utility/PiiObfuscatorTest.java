package systems.cauldron.utility;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PiiObfuscatorTest {

    @BeforeClass
    public static void setup() {
    }

    @AfterClass
    public static void cleanup() {
    }

    @Test
    public void basicTest() throws IOException {
        Path testFile = Paths.get("src", "test", "resources", "test.csv");

        FileObfuscator obfuscator = new FileObfuscator(testFile, ",");

        RecordKeyScanner<TestKeyType> scanner = RecordKeyScanner.createBuilder(TestKeyType.class)
                .withKeyColumn(1, TestKeyType.CUSTOMER)
                .withKeyColumn(3, TestKeyType.ACCOUNT)
                .withKeyColumn(5, TestKeyType.CUSTOMER)
                .build();

        obfuscator.scan(scanner);

        Map<String, String> customerKeymap = mapKeysToUuid(scanner.getResults(TestKeyType.CUSTOMER));
        Map<String, String> accountKeymap = mapKeysToUuid(scanner.getResults(TestKeyType.ACCOUNT));

        RecordKeyRewriter rewriter = RecordKeyRewriter.createBuilder()
                .withKeyColumn(1, customerKeymap)
                .withKeyColumn(3, accountKeymap)
                .withKeyColumn(5, customerKeymap)
                .build();

        Path obfuscatedFile = null;
        List<String[]> obfuscatedLines;
        try {
            obfuscatedFile = Files.createTempFile(null, null);
            obfuscator.rewrite(rewriter, obfuscatedFile);
            obfuscatedLines = readLines(obfuscatedFile);
        } finally {
            if (obfuscatedFile != null) {
                Files.delete(obfuscatedFile);
            }
        }

        List<String[]> originalLines = readLines(testFile);

        assertNotEquals(originalLines.get(0)[1], obfuscatedLines.get(0)[1]);
        assertNotEquals(originalLines.get(0)[3], obfuscatedLines.get(0)[3]);
        assertNotEquals(originalLines.get(0)[5], obfuscatedLines.get(0)[5]);

        assertNotEquals(originalLines.get(1)[1], obfuscatedLines.get(1)[1]);
        assertNotEquals(originalLines.get(1)[3], obfuscatedLines.get(1)[3]);
        assertNotEquals(originalLines.get(1)[5], obfuscatedLines.get(1)[5]);

        assertEquals(obfuscatedLines.get(0)[1], obfuscatedLines.get(1)[5]);
        assertEquals(obfuscatedLines.get(0)[5], obfuscatedLines.get(1)[1]);

    }

    private static Map<String, String> mapKeysToUuid(Set<String> keys) {
        return keys.stream().collect(
                Collectors.toMap(
                        k -> k,
                        k -> UUID.randomUUID().toString().replace("-", "")
                )
        );
    }

    private static List<String[]> readLines(Path path) throws IOException {
        return Files.lines(path, StandardCharsets.UTF_8)
                .map(line -> line.split(",", -1))
                .collect(Collectors.toList());

    }
}
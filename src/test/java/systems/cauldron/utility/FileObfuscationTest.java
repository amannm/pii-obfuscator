package systems.cauldron.utility;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FileObfuscationTest {

    @BeforeClass
    public static void setup() {
    }

    @AfterClass
    public static void cleanup() {
    }

    @Test
    public void all_pii_must_be_obfuscated() throws IOException {

        Path originalFile = Paths.get("src", "test", "resources", "test.csv");

        Map<Integer, TestKeyType> layout = new HashMap<>();
        layout.put(1, TestKeyType.CUSTOMER);
        layout.put(3, TestKeyType.ACCOUNT);
        layout.put(5, TestKeyType.CUSTOMER);

        Function<String, String> fileLocalUuidMapper = k -> UUID.randomUUID().toString().replace("-", "");
        Map<TestKeyType, Function<String, String>> typeMappers = Arrays.stream(TestKeyType.values()).collect(Collectors.toMap(t -> t, t -> fileLocalUuidMapper));


        List<String[]> obfuscatedLines = scanAndObfuscate(originalFile, layout, typeMappers);

        List<String[]> originalLines = readLines(originalFile);

        assertNotEquals(originalLines.get(0)[1], obfuscatedLines.get(0)[1]);
        assertNotEquals(originalLines.get(0)[3], obfuscatedLines.get(0)[3]);
        assertNotEquals(originalLines.get(0)[5], obfuscatedLines.get(0)[5]);

        assertNotEquals(originalLines.get(1)[1], obfuscatedLines.get(1)[1]);
        assertNotEquals(originalLines.get(1)[3], obfuscatedLines.get(1)[3]);
        assertNotEquals(originalLines.get(1)[5], obfuscatedLines.get(1)[5]);

        assertEquals(obfuscatedLines.get(0)[1], obfuscatedLines.get(1)[5]);
        assertEquals(obfuscatedLines.get(0)[5], obfuscatedLines.get(1)[1]);

        assertNotEquals(obfuscatedLines.get(5)[1], obfuscatedLines.get(3)[3]);

    }

    @Test
    public void exception_thrown_if_any_scanned_keys_unmapped() throws IOException {

        Path originalFile = Paths.get("src", "test", "resources", "test.csv");

        Map<Integer, TestKeyType> layout = new HashMap<>();
        layout.put(1, TestKeyType.CUSTOMER);
        layout.put(3, TestKeyType.ACCOUNT);
        layout.put(5, TestKeyType.CUSTOMER);

        AtomicInteger count = new AtomicInteger(0);
        Function<String, String> brokenFileLocalUuidMapper = k -> {
            if (count.getAndIncrement() == 0) {
                return null;
            }
            return UUID.randomUUID().toString().replace("-", "");
        };
        Map<TestKeyType, Function<String, String>> typeMappers = Arrays.stream(TestKeyType.values()).collect(Collectors.toMap(t -> t, t -> brokenFileLocalUuidMapper));

        try {
            scanAndObfuscate(originalFile, layout, typeMappers);
        } catch (UnmappedKeyException e) {
            return;
        }

        fail();

    }

    private List<String[]> scanAndObfuscate(Path originalFile, Map<Integer, TestKeyType> layout, Map<TestKeyType, Function<String, String>> typeMappers) throws IOException {

        FileObfuscator<TestKeyType> obfuscator = new FileObfuscator<>(TestKeyType.class, layout, typeMappers);

        Path obfuscatedFile = null;
        try {
            obfuscatedFile = Files.createTempFile(null, null);
            obfuscator.obfuscate(",", originalFile, obfuscatedFile);
            return readLines(obfuscatedFile);
        } finally {
            if (obfuscatedFile != null) {
                Files.delete(obfuscatedFile);
            }
        }

    }

    private static List<String[]> readLines(Path path) throws IOException {
        List<String[]> collect = Files.lines(path, StandardCharsets.UTF_8)
                .map(line -> line.split(",", -1))
                .collect(Collectors.toList());
        System.err.println("\n" + collect.stream().map(fields -> String.join(",", fields)).collect(Collectors.joining("\n")));
        return collect;
    }
}
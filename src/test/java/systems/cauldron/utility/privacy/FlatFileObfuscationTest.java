package systems.cauldron.utility.privacy;

import org.junit.Test;
import systems.cauldron.utility.privacy.exception.KeyTypeMapperNotFoundException;
import systems.cauldron.utility.privacy.exception.ObfuscatedKeyNotFoundException;
import systems.cauldron.utility.privacy.obfuscator.FlatFileObfuscator;
import systems.cauldron.utility.privacy.obfuscator.KeyTransformer;
import systems.cauldron.utility.privacy.obfuscator.LocalKeyTransformer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FlatFileObfuscationTest {

    public enum TestEntityKeyType {
        CUSTOMER,
        PLANE,
        ACCOUNT,
        HORSE
    }

    @Test
    public void all_pii_must_be_obfuscated() throws IOException {

        Path originalFile = Paths.get("src", "test", "resources", "test.csv");

        Map<Integer, TestEntityKeyType> layout = new HashMap<>();
        layout.put(1, TestEntityKeyType.CUSTOMER);
        layout.put(3, TestEntityKeyType.ACCOUNT);
        layout.put(4, TestEntityKeyType.HORSE);
        layout.put(5, TestEntityKeyType.CUSTOMER);

        Function<String, String> fileLocalUuidMapper = k -> UUID.randomUUID().toString().replace("-", "");
        Function<String, String> passThroughMapper = k -> k;

        Map<TestEntityKeyType, Function<String, String>> typeMappers = new HashMap<>();
        typeMappers.put(TestEntityKeyType.CUSTOMER, fileLocalUuidMapper);
        typeMappers.put(TestEntityKeyType.ACCOUNT, fileLocalUuidMapper);
        typeMappers.put(TestEntityKeyType.HORSE, passThroughMapper);
        LocalKeyTransformer<TestEntityKeyType> transformer = new LocalKeyTransformer<>(layout, typeMappers);

        List<String[]> obfuscatedLines = scanAndObfuscateFile(originalFile, transformer);

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

        assertEquals(originalLines.get(0)[4], obfuscatedLines.get(0)[4]);

    }

    @Test
    public void exception_thrown_if_any_scanned_keys_unmapped() throws IOException {

        Path originalFile = Paths.get("src", "test", "resources", "test.csv");

        Map<Integer, TestEntityKeyType> layout = new HashMap<>();
        layout.put(1, TestEntityKeyType.CUSTOMER);
        layout.put(3, TestEntityKeyType.ACCOUNT);
        layout.put(5, TestEntityKeyType.CUSTOMER);


        Function<String, String> fileLocalUuidMapper = k -> UUID.randomUUID().toString().replace("-", "");

        AtomicInteger count = new AtomicInteger(0);
        Function<String, String> brokenFileLocalUuidMapper = k -> {
            if (count.getAndIncrement() == 0) {
                return null;
            }
            return UUID.randomUUID().toString().replace("-", "");
        };

        Map<TestEntityKeyType, Function<String, String>> typeMappers = new HashMap<>();
        typeMappers.put(TestEntityKeyType.CUSTOMER, fileLocalUuidMapper);
        typeMappers.put(TestEntityKeyType.ACCOUNT, brokenFileLocalUuidMapper);
        LocalKeyTransformer<TestEntityKeyType> transformer = new LocalKeyTransformer<>(layout, typeMappers);

        try {
            scanAndObfuscateFile(originalFile, transformer);
        } catch (ObfuscatedKeyNotFoundException e) {
            return;
        }

        fail();

    }

    @Test
    public void exception_thrown_if_any_missing_type_mapper() throws IOException {

        Path originalFile = Paths.get("src", "test", "resources", "test.csv");

        Map<Integer, TestEntityKeyType> layout = new HashMap<>();
        layout.put(1, TestEntityKeyType.CUSTOMER);
        layout.put(3, TestEntityKeyType.ACCOUNT);
        layout.put(5, TestEntityKeyType.CUSTOMER);

        Function<String, String> fileLocalUuidMapper = k -> UUID.randomUUID().toString().replace("-", "");

        Map<TestEntityKeyType, Function<String, String>> typeMappers = new HashMap<>();
        typeMappers.put(TestEntityKeyType.CUSTOMER, fileLocalUuidMapper);

        try {
            new LocalKeyTransformer<>(layout, typeMappers);
        } catch (KeyTypeMapperNotFoundException e) {
            return;
        }

        fail();

    }

    private List<String[]> scanAndObfuscateFile(Path originalFile, KeyTransformer<TestEntityKeyType> transformer) throws IOException {
        FlatFileObfuscator<TestEntityKeyType> obfuscator = new FlatFileObfuscator<>(transformer);
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
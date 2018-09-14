package systems.cauldron.utility;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PiiObfuscatorTest {

    @BeforeClass
    public static void setup() {
    }

    @AfterClass
    public static void cleanup() {
    }

    @Test
    public void basicTest() throws IOException {

        List<String[]> testRecords = Files.lines(Paths.get("src", "test", "resources", "test.csv"))
                .map(line -> line.split(",", -1))
                .collect(Collectors.toList());

        RecordKeyScanner<TestKeyType> scanner = RecordKeyScanner.createBuilder(TestKeyType.class)
                .withKeyColumn(1, TestKeyType.CUSTOMER)
                .withKeyColumn(3, TestKeyType.ACCOUNT)
                .withKeyColumn(5, TestKeyType.CUSTOMER)
                .build();

        testRecords.forEach(scanner::scan);

        Map<String, String> customerKeymap = mapKeysToUuid(scanner.getResults(TestKeyType.CUSTOMER));
        Map<String, String> accountKeymap = mapKeysToUuid(scanner.getResults(TestKeyType.ACCOUNT));

        RecordKeyRewriter rewriter = RecordKeyRewriter.createBuilder()
                .withKeyColumn(1, customerKeymap)
                .withKeyColumn(3, accountKeymap)
                .withKeyColumn(5, customerKeymap)
                .build();

        testRecords.forEach(rewriter::rewrite);

        Assert.assertNotEquals("Amann Malik", testRecords.get(0)[1]);
        Assert.assertNotEquals("1234567890", testRecords.get(0)[3]);
        Assert.assertNotEquals("George Washington", testRecords.get(0)[5]);

        Assert.assertNotEquals("George Washington", testRecords.get(1)[1]);
        Assert.assertNotEquals("1200000", testRecords.get(1)[3]);
        Assert.assertNotEquals("Amann Malik", testRecords.get(1)[5]);

        Assert.assertEquals(testRecords.get(0)[1], testRecords.get(1)[5]);
        Assert.assertEquals(testRecords.get(0)[5], testRecords.get(1)[1]);

    }

    private static Map<String, String> mapKeysToUuid(Set<String> keys) {
        return keys.stream().collect(
                Collectors.toMap(
                        k -> k,
                        k -> UUID.randomUUID().toString().replace("-", "")
                )
        );
    }
}
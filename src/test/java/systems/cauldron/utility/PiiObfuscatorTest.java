package systems.cauldron.utility;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class PiiObfuscatorTest {

    @BeforeClass
    public static void setup() {
    }

    @AfterClass
    public static void cleanup() {
    }

    @Test
    public void basicTest() {

        List<String[]> testRecords = Arrays.asList(
                new String[]{
                        "69",
                        "Amann Malik",
                        "fartmaster",
                        "1234567890",
                        "false",
                        "George Washington",
                        "helloworld"
                },
                new String[]{
                        "99",
                        "George Washington",
                        "president",
                        "1200000",
                        "true",
                        "Amann Malik",
                        "heyooo"
                },
                new String[]{
                        "123",
                        "Abraham Lincoln",
                        "test",
                        "1000000000",
                        "false",
                        "Abraham Lincoln",
                        "hello"
                },
                new String[]{
                        "420",
                        "Poopy McFartFace",
                        "test",
                        "1111111111",
                        "false",
                        "George Washington",
                        "helloworld"
                },
                new String[]{
                        "1337",
                        "Elite H4x0r",
                        "hacker",
                        "1234567890",
                        "true",
                        "Abraham Lincoln",
                        "hey"
                }
        );

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
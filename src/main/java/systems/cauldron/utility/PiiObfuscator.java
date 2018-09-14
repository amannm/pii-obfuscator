package systems.cauldron.utility;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PiiObfuscator<T extends Enum<T>> {

    public Map<T, Set<String>> scan(RecordKeyScanner<T> scanner, List<String[]> lines) {
        lines.forEach(scanner::scan);
        return scanner.getResults();
    }

    public void rewrite(RecordKeyRewriter rewriter, List<String[]> lines) {
        lines.forEach(rewriter::rewrite);
    }
}

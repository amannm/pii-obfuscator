package systems.cauldron.utility;

import java.util.List;

public class PiiObfuscator<T extends Enum<T>> {

    public void scan(RecordKeyScanner<T> scanner, List<String[]> lines) {
        lines.forEach(scanner::scan);
    }

    public void rewrite(RecordKeyRewriter rewriter, List<String[]> lines) {
        lines.forEach(rewriter::rewrite);
    }
}

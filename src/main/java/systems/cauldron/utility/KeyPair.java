package systems.cauldron.utility;

public class KeyPair {

    private final String originalKey;
    private final String obfuscatedKey;

    public KeyPair(String originalKey, String obfuscatedKey) {
        this.originalKey = originalKey;
        this.obfuscatedKey = obfuscatedKey;
    }

    public String getOriginalKey() {
        return originalKey;
    }

    public String getObfuscatedKey() {
        return obfuscatedKey;
    }
}

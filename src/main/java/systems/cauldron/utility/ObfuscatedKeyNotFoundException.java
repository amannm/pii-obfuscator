package systems.cauldron.utility;

public class ObfuscatedKeyNotFoundException extends RuntimeException {
    public ObfuscatedKeyNotFoundException(String scannedKey) {
        super("no obfuscated key found for scanned key: " + scannedKey);
    }
}

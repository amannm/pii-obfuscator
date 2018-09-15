package systems.cauldron.utility.privacy;

public class ObfuscatedKeyNotFoundException extends RuntimeException {
    public ObfuscatedKeyNotFoundException(String scannedKey) {
        super("no obfuscated key found for scanned key: " + scannedKey);
    }
}

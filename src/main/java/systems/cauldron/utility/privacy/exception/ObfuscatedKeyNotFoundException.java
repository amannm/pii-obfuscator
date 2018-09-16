package systems.cauldron.utility.privacy.exception;

public class ObfuscatedKeyNotFoundException extends RuntimeException {
    public ObfuscatedKeyNotFoundException(String scannedKey) {
        super("no obfuscated key found for scanned key: " + scannedKey);
    }

    public ObfuscatedKeyNotFoundException() {
        super("obfuscated key mismatch");
    }
}

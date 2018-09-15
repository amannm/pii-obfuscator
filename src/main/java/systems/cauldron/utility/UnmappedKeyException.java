package systems.cauldron.utility;

public class UnmappedKeyException extends RuntimeException {
    public UnmappedKeyException(String scannedKey) {
        super("configured obfuscated key mapper returned null value for scanned key: " + scannedKey);
    }
}

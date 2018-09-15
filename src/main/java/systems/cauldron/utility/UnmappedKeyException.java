package systems.cauldron.utility;

public class UnmappedKeyException extends RuntimeException {

    private final int scannedKeyCount;
    private final int mappedKeyCount;

    public UnmappedKeyException(int scannedKeyCount, int mappedKeyCount) {
        super("scanned keyset size [" + scannedKeyCount + "] exceeds mapped keyset size [" + mappedKeyCount + "]");
        this.scannedKeyCount = scannedKeyCount;
        this.mappedKeyCount = mappedKeyCount;
    }

    public int getScannedKeyCount() {
        return scannedKeyCount;
    }

    public int getMappedKeyCount() {
        return mappedKeyCount;
    }

}

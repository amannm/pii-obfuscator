package systems.cauldron.utility.privacy;

public class KeyTypeMapperNotFoundException extends RuntimeException {
    public KeyTypeMapperNotFoundException(String typeString) {
        super("no key type mapper found for column key type: " + typeString);
    }
}
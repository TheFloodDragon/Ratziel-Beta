package cn.fd.ratziel.core.exception;

/**
 * ArgumentNotFoundException
 *
 * @author TheFloodDragon
 * @since 2024/8/16 18:28
 */
public final class ArgumentNotFoundException extends Exception {

    private final Class<?> type;

    public ArgumentNotFoundException(Class<?> type) {
        super("Cannot found the argument: " + type.getName() + " !");
        this.type = type;
    }

    public Class<?> getMissingType() {
        return type;
    }

}

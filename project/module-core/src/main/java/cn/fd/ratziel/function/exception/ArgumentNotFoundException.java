package cn.fd.ratziel.function.exception;

/**
 * ArgumentNotFoundException
 *
 * @author TheFloodDragon
 * @since 2024/8/16 18:28
 */
public final class ArgumentNotFoundException extends Exception {

    public ArgumentNotFoundException(Class<?> type) {
        super("Cannot found the argument: " + type.getName() + " !");
    }

}

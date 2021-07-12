package cc.javajobs.buildtools;

import org.jetbrains.annotations.NotNull;

/**
 * With thanks to:
 *  - SpigotMC for BuildTools.
 *  - Mojang for Minecraft.
 *  - AdoptOpenJDK for the Java Development Kits.
 *
 * @author Callum Johnson
 * @since 11/07/2021 - 09:16
 */
public class Main {

    /**
     * Main method to run the program.
     * <p>
     *     To run this program, open CMD, and type 'java -jar "BuildTools-Master-1.0-SNAPSHOT.jar"'
     * </p>
     * @param args of the execution.
     */
    public static void main(String[] args) throws InterruptedException {
        Processor processor = new Processor();
        processor.start();
    }

    /**
     * Method to log the message to the console.
     *
     * @param message to log.
     * @throws IllegalArgumentException if the message is blank.
     */
    public static void log(@NotNull String message) {
        if (message.isEmpty()) throw new IllegalArgumentException("Message cannot be blank");
        System.out.println("[INFO] " + message);
    }

    /**
     * Method to log the message to the console with the [ERROR] prefix.
     *
     * @param message to log.
     * @throws IllegalArgumentException if the message is blank.
     */
    public static void error(@NotNull String message) {
        if (message.isEmpty()) throw new IllegalArgumentException("Message cannot be blank");
        System.out.println("[ERROR] " + message);
    }

}

package cc.javajobs.buildtools;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

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
    public static String[] args;
    static Processor processor = new Processor();

    public static boolean debug = false;
    /**
     * Main method to run the program.
     * <p>
     *     To run this program, open CMD, and type 'java -jar "BuildTools-Master-1.0-SNAPSHOT.jar"'
     * </p>
     * @param args of the execution.
     */
    public static void main(String[] args) throws InterruptedException {
        Main.args = args;
        processor.checkArgs();
        processor.done();
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

    /**
     * Method to log the message to the console with the [DEBUG] prefix.
     *
     * @param message to log.
     * @throws IllegalArgumentException if the message is blank.
     */
    public static void debug(@NotNull String message) {
        if(debug) {
            System.out.println("[DEBUG] " + message);
        }
    }

    //create folder function


}

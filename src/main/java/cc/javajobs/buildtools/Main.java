package cc.javajobs.buildtools;

import org.jetbrains.annotations.NotNull;
import org.apache.commons.cli.*;

import java.io.File;

/**
 * With thanks to:
 * - SpigotMC for BuildTools.
 * - Mojang for Minecraft.
 * - AdoptOpenJDK for the Java Development Kits.
 *
 * @author Callum Johnson
 * @since 11/07/2021 - 09:16
 */
public class Main {

    public static Processor processor = new Processor();
    public static boolean debug = false;

    /**
     * Main method to run the program.
     * <p>
     * To run this program, open CMD, and type 'java -jar "BuildTools-Master-1.0-SNAPSHOT.jar"'
     * </p>
     *
     * @param args of the execution.
     */
    public static void main(String[] args) throws Exception {
        String jarName = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getName();
        if (jarName.equals("classes")) jarName = "BuildTools-Master-1.0-SNAPSHOT.jar";
        final String hf = "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=";
        final Options options = new Options();
        options.addOption("msj", "move-server-jars", true,
                "Select a Path/Directory to move Server Jars into.");
        options.addOption("mnj", "move-nms-jars", true,
                "Select a Path/Directory to move NMS Jars into.");
        options.addOption("k", "keep", false,
                "Toggle Overwriting of Files.");
        options.addOption("d", "debug", false,
                "Toggle Debug Mode.");
        options.addOption("h", "help", false, "Help Menu");
        options.addOption("r", "reverse", false, "Reverse the BuildTools order (start at 1.8).");
        try {
            final CommandLineParser parser = new DefaultParser();
            final CommandLine parse = parser.parse(options, args);
            if (parse.hasOption("h")) {
                final HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(150, "java -jar " + jarName, hf, options, hf, true);
                return;
            }
            if (parse.hasOption("d")) debug = true;
            for (Option option : parse.getOptions()) {
                final String optionProperties = parse.getOptionValue(option);
                debug("Found options: ");
                debug(option + " : " + optionProperties);
            }
            processor.setupArguments(parse);
            processor.start();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
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
        if (debug) {
            System.out.println("[DEBUG] " + message);
        }
    }

}

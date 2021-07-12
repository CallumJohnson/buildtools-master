package cc.javajobs.buildtools;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * The Thread which calls each version-specific BuildTools jar.
 *
 * @author Callum Johnson
 * @since 11/07/2021 - 10:19
 */
public class BuildToolsThread implements Runnable {

    /**
     * The working files for the thread.
     */
    private final File java, workingDirectory, buildTools;

    /**
     * Version that this thread stands for.
     */
    private final String version;

    public BuildToolsThread(@NotNull File java, @NotNull String version,
                            @NotNull File workingDirectory, @NotNull File buildTools) {
        this.java = java;
        this.version = version;
        this.workingDirectory = workingDirectory;
        this.buildTools = buildTools;
    }

    /**
     * The function of this thread is to run the BuildTools Jar linked to the version.
     * <p>
     *     Using {@link ProcessBuilder}, the command is specified as:
     *     <br> 'java -jar -Xmx512M {@link #buildTools} --rev {@link #version}
     *     <br>This thread also consumes the IO of the Java Process, enabling the output of
     *     the BuildTools jar to be sent through this one.
     * </p>
     */
    @Override
    public void run() {
        try {
            final ProcessBuilder builder = new ProcessBuilder(java.toString(), "-jar", "-Xmx512M", buildTools.getName(), "--rev", version);
            builder.directory(workingDirectory);
            builder.inheritIO();
            final Process process = builder.start();
            process.waitFor(); // Delay the completion of this task until its done.
        } catch (Exception e) {
            Main.error("Experienced an error during BuildTools execution!");
            Main.error("Experienced:\t" + e.getClass().getSimpleName());
            if (e.getMessage() != null) {
                Main.error("Provided Error Message:\t" + e.getMessage());
            }
        }
    }

}

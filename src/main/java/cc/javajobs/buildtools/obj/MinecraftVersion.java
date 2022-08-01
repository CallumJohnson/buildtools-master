package cc.javajobs.buildtools.obj;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The MinecraftVersion class stands for an object which represents the numerical versioning system used by Minecraft.
 * <p>
 *     There are three parts of a minecraft version, a revision number (only '1'), a major version (e.g. '19') and a
 *     minor version (e.g. '1'). Some versions of Minecraft do not have a minor versions and these are considered
 *     major releases - (e.g. 1.13).<br>
 *     When combined, a version example could be '1.19.1'.
 * </p>
 */
public class MinecraftVersion implements Comparable<MinecraftVersion> {

    /**
     * MinecraftVersion data for the given version.
     */
    private final int rev, major, minor;

    /**
     * The file name - used for variable extraction.
     */
    private final String file;

    /**
     * CraftBukkit and NMS Data, used for sorting.
     */
    private String cb, nms;

    /**
     * The Version of Java the BuildTools executable has to be executed with to compile the SpigotMC Jar.
     */
    private JavaVersion java = null;

    /**
     * Constructor to create a Minecraft Version.
     *
     * @param name of the file (.json extension included) - pulled from SpigotMC.
     * @param url to resolve the variables from (SpigotMC Hub).
     * @throws IOException if the data cannot be extracted from the SpigotMC Hub.
     */
    public MinecraftVersion(String name, String url) throws IOException {
        this.file = name;
        name = name.replace(".json", "");
        final String[] split = name.split("\\.");
        if (split.length == 3) { // 1.1.1
            this.rev = Integer.parseInt(split[0]);
            this.major = Integer.parseInt(split[1]);
            this.minor = Integer.parseInt(split[2]);
        } else { // 1.1
            this.rev = Integer.parseInt(split[0]);
            this.major = Integer.parseInt(split[1]);
            this.minor = 0;
        }
        resolveVariables(url);
    }

    /**
     * Method to determine the CraftBukkit commit data and the JavaVersion for the MinecraftVersion.
     *
     * @param url pointing to the SpigotMC Hub - for data extraction.
     * @throws IOException if the connection cannot be created.
     */
    private void resolveVariables(String url) throws IOException {
        final URL connect = new URL(url + file);
        final BufferedReader in = new BufferedReader(new InputStreamReader(connect.openStream()));
        String str;
        while ((str = in.readLine()) != null) {
            if (str.contains("CraftBukkit")) {
                this.cb = str.replace("\"CraftBukkit\":", "")
                        .replaceAll("\"", "")
                        .replaceAll(",", "")
                        .trim();
            } else if (str.contains("javaVersions")) {
                final List<JavaVersion> versionsSupported = Arrays.stream(str.replace("\"javaVersions\":", "")
                        .replaceAll("[\\[\\]]", "")
                        .trim().split(", ")).map(Integer::parseInt).map(JavaVersion::getByIndex).collect(Collectors.toList());
                this.java = versionsSupported.get(0);
            }
        }
        // Default the JavaVersion to JDK8 if it's not found (1.11.2 and prior do not list it).
        if (this.java == null) java = JavaVersion.JAVA_8;
    }

    // Getters

    public String getCraftBukkit() {
        return cb;
    }

    public JavaVersion getJava() {
        return java;
    }

    public String getNMS() {
        return nms;
    }

    // Setters

    public void setNMS(String nms) {
        this.nms = nms;
    }

    // Overridden functions.

    @Override
    public int compareTo(@NotNull MinecraftVersion o) {
        // Sort by Revision, Major and then Minor versions.
        int result = Integer.compare(o.rev, rev);
        if (result == 0) {
            result = Integer.compare(o.major, major);
            if (result == 0) result = Integer.compare(o.minor, minor);
        }
        return result;
    }

    // Print the version like Minecraft does (1.18.2 etc. etc.).
    @Override
    public String toString() {
        return rev + "." + major + (minor == 0 ? "" : "." + minor);
    }

}

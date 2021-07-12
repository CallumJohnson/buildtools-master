package cc.javajobs.buildtools;

import org.jetbrains.annotations.NotNull;

/**
 * Spigot Versions provided by SpigotMC.
 * <p>
 *     For a list of versions and their respective build-JDK, see: https://hub.spigotmc.org/versions/
 * </p>
 *
 * @author Callum Johnson
 * @since 11/07/2021 - 12:48
 */
public enum SpigotVersion {

    /**
     * NMS Version: 1_17_R1
     * Bukkit Version: 1.17-R0.1-SNAPSHOT
     */
    Spigot_1_17_1(JavaVersion.JAVA_16),

    /**
     * NMS Version: 1_16_R3
     * Bukkit Version: 1.16.5-R0.1-SNAPSHOT
     */
    Spigot_1_16_5(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_16_R2
     * Bukkit Version: 1.16.3-R0.1-SNAPSHOT
     */
    Spigot_1_16_3(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_16_R1
     * Bukkit Version: 1.16.1-R0.1-SNAPSHOT
     */
    Spigot_1_16_1(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_15_R1
     * Bukkit Version: 1.15.2-R0.1-SNAPSHOT
     */
    Spigot_1_15_2(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_14_R1
     * Bukkit Version: 1.14.4-R0.1-SNAPSHOT
     */
    Spigot_1_14_4(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_13_R2
     * Bukkit Version: 1.13.2-R0.1-SNAPSHOT
     */
    Spigot_1_13_2(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_13_R1
     * Bukkit Version: 1.13-R0.1-SNAPSHOT
     */
    Spigot_1_13(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_12_R1
     * Bukkit Version: 1.12.2-R0.1-SNAPSHOT
     */
    Spigot_1_12_2(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_11_R1
     * Bukkit Version: 1.11.2-R0.1-SNAPSHOT
     */
    Spigot_1_11_2(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_10_R1
     * Bukkit Version: 1.10.2-R0.1-SNAPSHOT
     */
    Spigot_1_10_2(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_9_R2
     * Bukkit Version: 1.9.4-R0.1-SNAPSHOT
     */
    Spigot_1_9_4(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_9_R1
     * Bukkit Version: 1.9.2-R0.1-SNAPSHOT
     */
    Spigot_1_9_2(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_8_R3
     * Bukkit Version: 1.8.8-R0.1-SNAPSHOT
     */
    Spigot_1_8_8(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_8_R2
     * Bukkit Version: 1.8.3-R0.1-SNAPSHOT
     */
    Spigot_1_8_3(JavaVersion.JAVA_8),

    /**
     * NMS Version: 1_8_R1
     * Bukkit Version: 1.8-R0.1-SNAPSHOT
     */
    Spigot_1_8(JavaVersion.JAVA_8);

    /**
     * The earliest version required to build the Spigot Version.
     * <p>
     *     This is gathered from the 'javaVersions' JSON object which states an upper and lower bound of Java Versions.
     *     For example, as of 12/07/2021 - 14:32, 1.17.1 is listed as '"javaVersions": [60, 60]',
     *     which suggests that the version required is 'Java 16' -> 'Java 16'.
     *     <br>Visit https://hub.spigotmc.org/versions/ for more information (Updated by the Spigot Team).
     * </p>
     * @see JavaVersion
     */
    private final JavaVersion version;

    /**
     * Constructor to create a SpigotVersion linked to a JavaVersion.
     *
     * @param version required to build the SpigotVersion.
     */
    SpigotVersion(@NotNull JavaVersion version) {
        this.version = version;
    }

    /**
     * Method to obtain the required JavaVersion.
     *
     * @return {@link JavaVersion}.
     */
    @NotNull
    public JavaVersion getJavaVersionRequired() {
        return version;
    }

    /**
     * Method to obtain the Version name from the enumeration name.
     * <p>
     *     To reduce the amount of variables in this class for maintenance,
     *     I will use Regex to modify the name of the Enumeration to return the respective String title of the version.
     *     <br>
     *     Spigot_1_17_1 -> 1.17.1
     * </p>
     *
     * @return {@link String} version name.
     */
    @NotNull
    public String getVersionTitle() {
        return name().replace("Spigot_", "").replaceAll("_", "\\.").trim();
    }

}

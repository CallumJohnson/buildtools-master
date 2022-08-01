package cc.javajobs.buildtools.obj;

/**
 * An index of Java version names to their respective class versions.
 * <p>
 *     Thanks to: <a href="https://en.wikipedia.org/wiki/Java_class_file#General_layout">Wikipedia</a>
 * </p>
 *
 * @author Callum Johnson
 * @since 11/07/2021 - 11:39
 */
public enum JavaVersion {

    /**
     * Java 18 - JDK 18
     */
    JAVA_18(62),

    /**
     * Java 17 - JDK 17
     */
    JAVA_17(61),

    /**
     * Java 16 - JDK 16
     */
    JAVA_16(60),

    /**
     * Java 16 - JDK 15
     */
    JAVA_15(59),

    /**
     * Java 14 - JDK 14
     */
    JAVA_14(58),

    /**
     * Java 13 - JDK 13
     */
    JAVA_13(57),

    /**
     * Java 12 - JDK 12
     */
    JAVA_12(56),

    /**
     * Java 11 - JDK 11 (LTS)
     */
    JAVA_11(55),

    /**
     * Java 10 - JDK 10
     */
    JAVA_10(54),

    /**
     * Java 9 - JDK 9
     */
    JAVA_9(53),

    /**
     * Java 1.8 / 8 - JDK 8 / 1.8 (LTS)
     */
    JAVA_8(52),

    /**
     * Unsupported Java Version for this Project.
     */
    UNSUPPORTED(-1);

    /**
     * The Version linked to the Java/JDK version.
     */
    private final int versionIndex;

    /**
     * Constructor to initialise a JavaVersion.
     *
     * @param index of the version.
     * @see JavaVersion
     */
    JavaVersion(int index) {
        this.versionIndex = index;
    }

    /**
     * Method to obtain the Version Index for the given Java Version.
     *
     * @return {@link #versionIndex}
     */
    public int getVersionIndex() {
        return versionIndex;
    }

    /**
     * Method to obtain JavaVersion by its represented index.
     *
     * @param index to search for.
     * @return JavaVersion (defaults to JDK 8).
     */
    public static JavaVersion getByIndex(int index) {
        for (JavaVersion value : JavaVersion.values()) {
            if (value.getVersionIndex() == index) {
                return value;
            }
        }
        return JavaVersion.JAVA_8;
    }

}

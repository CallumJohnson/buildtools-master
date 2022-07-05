package cc.javajobs.buildtools;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * The Processor class handles all of the Program's functionality.
 * <p>
 * {@link #BUILDTOOLS_LOCATION} is the download url for BuildTools provided by SpigotMC.
 * {@link #JDK_16_DOWNLOAD} is the download url for JDK-16 provided by AdoptOpenJDK.
 * {@link #JDK_8_DOWNLOAD} is the download url for JDK-8 provided by AdoptOpenJDK.
 *
 * @author Callum Johnson
 * @since 11/07/2021 - 09:17
 */
public class Processor {

    /**
     * The download for BuildTools.
     */
    private static final String BUILDTOOLS_LOCATION = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";

    /**
     * The download for the JDK_17
     */
    private static final String JDK_17_DOWNLOAD = "https://github.com/AdoptOpenJDK/openjdk17-binaries/releases/download/jdk-2021-05-07-13-31/OpenJDK-jdk_x64_windows_hotspot_2021-05-06-23-30.zip";

    /**
     * The download for the JDK_16
     */
    private static final String JDK_16_DOWNLOAD = "https://github.com/AdoptOpenJDK/openjdk16-binaries/releases/download/jdk-16.0.1%2B9/OpenJDK16U-jdk_x64_windows_hotspot_16.0.1_9.zip";

    /**
     * The download for the JDK_8
     */
    private static final String JDK_8_DOWNLOAD = "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u292-b10/OpenJDK8U-jdk_x64_windows_hotspot_8u292b10.zip";

    /**
     * Method to <em>start</em> the process.
     *
     * @throws InterruptedException if the delay between messages fails.
     */
    public void start() throws InterruptedException {
        Main.log("  ____        _ _     _ _______          _     ");
        Main.log(" |  _ \\      (_) |   | |__   __|        | |    ");
        Main.log(" | |_) |_   _ _| | __| |  | | ___   ___ | |___ ");
        Main.log(" |  _ <| | | | | |/ _` |  | |/ _ \\ / _ \\| / __|");
        Main.log(" | |_) | |_| | | | (_| |  | | (_) | (_) | \\__ \\");
        Main.log(" |____/ \\__,_|_|_|\\__,_|  |_|\\___/ \\___/|_|___/");
        Main.log("                                               ");
        Thread.sleep(3000);
        Main.log("'BuildTools - Master' is now processing, downloading the most up-to-date BuildTools Jar.");
        Thread.sleep(5000);
        final File buildTools = attemptDownloadBuildTools();
        if (buildTools == null) return;
        Thread.sleep(3000);
        final File jdk17exe = locateJDKExecutable(JDK_17_DOWNLOAD, "17");
        if (jdk17exe == null) return;
        final File jdk16exe = locateJDKExecutable(JDK_16_DOWNLOAD, "16");
        if (jdk16exe == null) return;
        Thread.sleep(3000);
        final File jdk8exe = locateJDKExecutable(JDK_8_DOWNLOAD, "8");
        if (jdk8exe == null) return;
        Thread.sleep(10000);
        for (SpigotVersion value : SpigotVersion.values()) {
            final long start = System.currentTimeMillis();
            final String version = value.getVersionTitle();
            final File versionFolder = createVersionFolder(buildTools.getParentFile(), version);
            if (versionFolder == null) continue;
            final File versionSpecificBuildTools = copyBuildToolsToVersion(version, versionFolder, buildTools);
            if (versionSpecificBuildTools == null) continue;
            if (value.equals(SpigotVersion.Spigot_1_8) || value.equals(SpigotVersion.Spigot_1_8_3)) {
                if (!attemptDeleteOldWork(versionFolder, version)) {
                    Main.error("Failed to delete /work/ for " + version + ", skipping this version.");
                    Main.log("You can fix this by deleting '" + versionFolder.getAbsolutePath() + "\\work\\' manually.");
                    Thread.sleep(5000);
                    continue;
                }
            }
            final BuildToolsThread thread;
            if(value.getJavaVersionRequired().equals(JavaVersion.JAVA_17)) {
                thread = new BuildToolsThread(jdk17exe, version, versionFolder, versionSpecificBuildTools);
            } else if (value.getJavaVersionRequired().equals(JavaVersion.JAVA_16)) {
                thread = new BuildToolsThread(jdk16exe, version, versionFolder, versionSpecificBuildTools);
            } else {
                thread = new BuildToolsThread(jdk8exe, version, versionFolder, versionSpecificBuildTools);
            }
            final Thread task = new Thread(thread);
            task.start();
            task.join();
            final File producedFile = new File(versionFolder, "spigot-" + version + ".jar");
            if (!producedFile.exists()) {
                Main.error("Failed to produce the Spigot Artifact, this probably means there was an error.");
            } else {
                final long finish = System.currentTimeMillis();
                final long diff = finish - start;
                Main.log("Took " + TimeUnit.MILLISECONDS.toMinutes(diff) + " minutes to compile " + producedFile.getName());
            }
        }
    }

    /**
     * Method to locate the JDK Executable.
     * <p>
     *     If the JDK is not installed, it will install the JDK from the given url.
     *     <br>If the JDK is installed, it will skip to extraction.
     *     <br>Once the JDK has been extracted, it will attempt to find the /bin/java.exe executable.
     * </p>
     *
     * @param url to download the JDK from.
     * @param name of the JDK ("16"/"8").
     * @return {@link File} or {@code null} depending on if the '.exe' is found.
     * @throws InterruptedException if the message delay fails.
     */
    @Nullable
    private File locateJDKExecutable(@NotNull String url, @NotNull String name) throws InterruptedException {
        Main.log("Downloading JDK " + name + ".");
        final File jdk = attemptJDKDownload(url, name);
        if (jdk == null) return null;
        Thread.sleep(3000);
        if (!extractJDKZip(jdk)) return null;
        Thread.sleep(1000);
        return resolveExecutable(jdk.getParentFile(), jdk);
    }

    /**
     * Method to delete the /work/ folder for the given Versions.
     * <p>
     *     I'm not sure if its just my side, but BuildTools #131 fails to hash the old mc-server file.
     *     <br>This method therefore clears the /work/ folder, forcing BuildTools to create it manually again.
     * </p>
     *
     * @param versionFolder to delete /work/ from.
     * @param version to notify console on the success/failure of the process.
     * @return {@code true} if the /work/ folder is deleted.
     */
    private boolean attemptDeleteOldWork(@NotNull File versionFolder, @NotNull String version) {
        final File work = new File(versionFolder, "work");
        if (work.exists()) {
            if (!recursivelyDelete(work)) {
                Main.error("Failed to delete /work/ for " + version);
                return false;
            } else {
                Main.log("Deleted /work/ for " + version + ", I'm not sure why, but this is required for " + version + "!");
                return true;
            }
        }
        return false;
    }

    /**
     * Method to create the Version folder for the given version.
     *
     * @param parentFile to create the folder within.
     * @param version to create the folder for.
     * @return {@link File} or {@code null} based on the processes' success.
     */
    @Nullable
    private File createVersionFolder(File parentFile, String version) {
        final File file = new File(parentFile, version);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Main.error("Failed to create the version file for " + version);
                return null;
            }
        }
        Main.log("Found the folder:\t" + file.getAbsolutePath());
        return file;
    }

    /**
     * Method to attempt to download {@link #BUILDTOOLS_LOCATION}.
     * <p>
     *     Due to the nature of SpigotMC's BuildTools, ensuring you have the most up-to-date Jar is important,
     *     this method therefore forces the download regardless of the existence of the Jar.
     * </p>
     *
     * @return {@link File} which was downloaded or {@code null} if failure occurred.
     */
    @Nullable
    private File attemptDownloadBuildTools() {
        final FileDownloader buildToolsDownloader = new FileDownloader();
        final String folder = "BuildTools";
        final String filename = "BuildTools.jar";
        if (!buildToolsDownloader.downloadFile(BUILDTOOLS_LOCATION, filename, folder)) {
            Main.error("Failed to download the BuildTools Jar file.");
            return null;
        } else {
            Main.log("Downloaded the BuildTools Jar file.");
            return new File("./" + folder, filename);
        }
    }

    /**
     * Method to resolve the 'java.exe' file from the given file.
     * <p>
     *     This method loops through the parent folder (./JDK/), looking for the folder which matches the top-level
     *     entry in the Zip file (also provided to this method).
     * </p>
     *
     * @param parentFile to look through.
     * @param jdkZip to read and compare the files found.
     * @return {@link File} if the 'java.exe' file is found and {@code null} if it is not.
     */
    @Nullable
    private File resolveExecutable(@NotNull File parentFile, @NotNull File jdkZip) {
        try {
            final ZipFile zip = new ZipFile(jdkZip);
            final FileHeader topLevel = zip.getFileHeaders().get(0);
            Main.log("Resolving JDK Executable 'java.exe' (Top Level Folder:\t" +
                    topLevel.getFileName().replaceAll("\\\\", "") + ")");
            for (File file : Objects.requireNonNull(parentFile.listFiles())) {
                if (file.isDirectory() && (file.getName() + "/").equals(topLevel.getFileName())) {
                    final File exe = new File(file, "/bin/java.exe");
                    if (exe.exists()) {
                        Main.log("Found 'java.exe' at " + exe.getAbsolutePath());
                        return exe;
                    }
                }
            }
        } catch (ZipException ignored) {
        }
        Main.error("Failed to resolve JDK Executable.");
        return null;
    }

    /**
     * Method to extract the Zip file.
     * <p>
     *     This method uses <em>Zip4J</em> to unzip the given Zip-File.
     *     <br>Credit: https://github.com/srikanth-lingala/zip4j
     *     <br>If the zip has already been unzipped, then the process doesn't unzip it again.
     * </p>
     *
     * @param file to extract.
     * @return {@code true} if the extraction was successful.
     */
    private boolean extractJDKZip(@NotNull File file) {
        try {
            Main.log("Extracting Downloaded JDK Contents.");
            final ZipFile zip = new ZipFile(file);
            final FileHeader fileHeader = zip.getFileHeaders().get(0);
            final File outputFolder = new File(file.getParentFile(), fileHeader.getFileName());
            if (!outputFolder.exists()) {
                zip.extractAll(file.getParentFile().getAbsolutePath());
            } else {
                Main.log("Zip has already been extracted.");
                return true;
            }
            Main.log("Successfully Extracted JDK.");
            return true;
        } catch (IOException exception) {
            Main.error("Failed to extract JDK.");
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Method to attempt to download the JDK from the given path.
     * <p>
     *     If the downloaded zip already exists, then the download is skipped.
     * </p>
     *
     * @param path of the download (url).
     * @param niceName of the JDK, probably "16" or "8".
     * @return {@link File} if the JDK Zip downloads successfully.
     * @see #JDK_8_DOWNLOAD
     * @see #JDK_16_DOWNLOAD
     */
    @Nullable
    private File attemptJDKDownload(@NotNull String path, @NotNull String niceName) {
        final FileDownloader jdkDownloader = new FileDownloader();
        final String folder = "JDK";
        final String filename = "jdk-" + niceName + ".zip";
        final File file = new File("./" + folder, filename);
        if (file.exists()) return file;
        if (!jdkDownloader.downloadFile(path, filename, folder)) {
            Main.error("Failed to download the JDK.");
            return null;
        } else {
            Main.log("JDK Downloaded.");
            return file;
        }
    }

    /**
     * Method to copy 'BuildTools.jar' to the given Version Folder.
     *
     * @param version to copy it for (used when the Jar is renamed).
     * @param folder to copy it to.
     * @param buildTools to copy.
     * @return the copied {@link File} or {@code null} if the copy transaction fails.
     */
    @Nullable
    private File copyBuildToolsToVersion(@NotNull String version, @NotNull File folder, @NotNull File buildTools) {
        Main.log("Processing Version:\t" + version);
        try {
            final File versionBuildTools = new File(folder, "BuildTools - " + version + ".jar");
            if (versionBuildTools.exists()) {
                if (!versionBuildTools.delete()) {
                    Main.error("Failed to delete the old BuildTools Jar, to ensure that this " +
                            "process works flawlessly, we require the most up-to-date BuildTools Jar.");
                    return null;
                }
            }
            Files.copy(buildTools.toPath(), versionBuildTools.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return versionBuildTools;
        } catch (IOException e) {
            Main.error("Experienced an " + e.getClass().getSimpleName() + " during version execution.");
            if (e.getMessage() != null) {
                Main.error("Exception's Provided Message:\t" + e.getMessage());
            }
            return null;
        }
    }

    /**
     * Method to recursively delete a file and its contents.
     * <p>
     *     Due to the nature of the Java {@link File#delete()} method, a directory have to be empty before they can
     *     be deleted.
     *     <br>If the file doesn't exist, this method returns true.
     * </p>
     * @param file to delete
     * @return {@code true} if everything was deleted properly.
     */
    public static boolean recursivelyDelete(@NotNull File file) {
        if (!file.exists()) return true;
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!(recursivelyDelete(f))) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

}

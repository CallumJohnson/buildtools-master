package cc.javajobs.buildtools;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * FileDownloader stands for an object used to download specified files.
 *
 * @author Callum Johnson
 * @since 11/07/2021 - 09:23
 */
public class FileDownloader {

    /**
     * Method to download the file specified at the url.
     * <p>
     *     The fileName specified is the finalised output of the download.
     *     <br>The niceName provided is the name which is sent to the console when/if errors occur.
     * </p>
     *
     * @param url of the file.
     * @param fileName of the downloaded/local file.
     * @param niceName of the download, for example JDK 16, or BuildTools.
     * @return {@code true} if the download is successful.
     */
    public boolean downloadFile(@NotNull String url, @NotNull String fileName, @NotNull String niceName) {
        try {
            if (fileName.isEmpty()) throw new IllegalArgumentException("Filename cannot be blank");
            final File parent = new File("./", niceName);
            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    Main.error("Failed to create the parent directory for the Program-Data.");
                    return false;
                }
            } else Main.log("Parent folder " + niceName + " already exists.");
            final File file = new File(parent, fileName);
            if (file.exists()) {
                Main.log(niceName + " already exists, Welcome back! Deleting that Jar now.");
                if (!file.delete()) {
                    Main.error(niceName + " couldn't be deleted. Please manually delete it and re-run the process.");
                    return false;
                }
            }
            if (!file.createNewFile()) {
                Main.error(niceName + " couldn't be created. Please place this project into a Standalone Folder.");
                return false;
            }
            final URL location = new URL(url);
            Main.log("Downloading " + niceName + " from " + location);
            final OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            final URLConnection connection = location.openConnection();
            final InputStream input = connection.getInputStream();
            if (input == null) {
                Main.error("Failed to connect to the download source.");
                return false;
            }
            Main.log("Please wait for the download to complete.");
            final byte[] buffer = new byte[1024];
            int numRead;
            while ((numRead = input.read(buffer)) != -1) out.write(buffer, 0, numRead);
            input.close();
            out.close();
            return true;
        } catch (IOException e) {
            Main.error("Experienced an " + e.getClass().getSimpleName() + " during execution.");
            if (e.getMessage() != null) Main.error("Exception's Provided Message:\t" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}

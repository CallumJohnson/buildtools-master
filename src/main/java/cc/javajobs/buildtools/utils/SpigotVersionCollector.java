package cc.javajobs.buildtools.utils;

import cc.javajobs.buildtools.Main;
import cc.javajobs.buildtools.obj.MinecraftVersion;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The SpigotVersionCollector class pulls information from SpigotMCs' official listings to
 * automatically Build required versions.
 * <p>
 *
 * </p>
 *
 * @author Callum Johnson
 * @since 01/08/2022 - 13:26
 */
public class SpigotVersionCollector {

    /**
     * Map of Versions corresponding to their NMS Versions.
     */
    private final Map<String, MinecraftVersion> versionMap = new TreeMap<>();

    /**
     * List of all Versions discovered from SpigotMC Hub.
     *
     * @see MinecraftVersion
     */
    private final List<MinecraftVersion> minecraftVersions;

    /**
     * Constructor to scrape and allocate Versions into the {@link #versionMap}.
     *
     * @throws Exception upon error.
     */
    public SpigotVersionCollector(boolean reverseVersions) throws Exception {
        Main.log("Scraping for Versions from the SpigotMC Hub.");
        this.discoverVersionsFromSpigotMC();
        Main.log("Found Versions:");
        final List<MinecraftVersion> mvers = new ArrayList<>(versionMap.values());
        if (reverseVersions) mvers.sort(Comparator.reverseOrder());
        else mvers.sort(Comparator.naturalOrder());
        this.minecraftVersions = mvers;
        Main.log(minecraftVersions.stream().map(MinecraftVersion::toString).collect(Collectors.joining(", ")));
    }

    /**
     * Method to get all versions discovered and crafted by {@link #discoverVersionsFromSpigotMC()}.
     *
     * @return {@link #minecraftVersions}.
     */
    @NotNull
    public List<MinecraftVersion> getVersions() {
        return minecraftVersions;
    }

    /**
     * Method to 'discoverVersionsFromSpigotMC', collecting them via Jsoup and converting them into
     * {@link MinecraftVersion} objects.
     * <p>
     * The <a href="https://hub.spigotmc.org/versions/">URL</a> downloaded via Jsoup contains HTML 'a' tag elements.
     * Each 'a' element corresponds to a buildable version of SpigotMC, for the sake of this project,
     * versions which conform to 'x.x.x' or 'x.x' are collected, ignoring any other format.
     * </p>
     *
     * @throws Exception if the connection couldn't be made (Internet Connection Required).
     */
    private void discoverVersionsFromSpigotMC() throws Exception {
        final String url = "https://hub.spigotmc.org/versions/";
        // Connect and download the webpage's HTML.
        final Connection connect = Jsoup.connect(url);
        final Document document = connect.get();
        // Find all 'a' tags (links)
        final Elements aTagElements = document.getElementsByTag("a");
        final List<MinecraftVersion> minecraftVersions = new ArrayList<>();
        // For each link - check if it conforms to 'x.x.x' or 'x.x'.
        for (final Element aTagElement : aTagElements) {
            final String text = aTagElement.text();
            if (text.matches("\\d\\.\\d{1,2}(\\.json|\\.\\d{1,2}\\.json)")) {
                // Create a Version object for this 'a' tag.
                final MinecraftVersion minecraftVersion = new MinecraftVersion(text, url);
                minecraftVersions.add(minecraftVersion);
                minecraftVersion.setNMS(resolvePom(minecraftVersion.getCraftBukkit()));
            }
        }
        // Sort the versions, conforming to the Version classes' comparable implementation.
        minecraftVersions.sort(null);
        // Store each version in the VersionMap (NMS Versions)
        minecraftVersions.forEach(minecraftVersion -> versionMap.putIfAbsent(minecraftVersion.getNMS(), minecraftVersion));
    }

    /**
     * Method to temporarily download the POM of a given CraftBukkit Commit hash and resolve details from on the fly.
     * <p>
     * This method was originally created by <a href="https://hub.spigotmc.org/versions/">@MiniDigger</a> and
     * has been modified slightly to reflect the requirements for this project.
     * <br>View the original here: <a href="https://github.com/MiniDigger/spigot-resolver/blob/163d5d19484e22e4d25ae5bf55d3c0d44dc70a42/src/main/java/me/minidigger/spigotresolver/SpigotResolver.java#L183">here</a>.
     * </p>
     *
     * @param craftbukkitCommit to download and extract data from.
     * @return NMS Version for the given commit.
     * @throws Exception if the connection failed.
     */
    private String resolvePom(final String craftbukkitCommit) throws Exception {
        final URL url = new URL("https://hub.spigotmc.org/stash/projects/SPIGOT" +
                "/repos/craftbukkit/raw/pom.xml?at=" + craftbukkitCommit);
        final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String inputLine;
        String nmsVersion = "ERROR";
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.contains("minecraft_version")) {
                nmsVersion = inputLine.split("[><]")[2];
                break;
            }
        }
        return nmsVersion;
    }

}

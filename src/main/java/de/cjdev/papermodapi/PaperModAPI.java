package de.cjdev.papermodapi;

import com.sun.net.httpserver.HttpServer;
import de.cjdev.papermodapi.init.CommandInit;
import de.cjdev.papermodapi.listener.InventoryClickEventListener;
import de.cjdev.papermodapi.listener.PlayerInteractEventListener;
import net.kyori.adventure.text.Component;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class PaperModAPI extends JavaPlugin {
    private static PaperModAPI plugin;
    public static Logger LOGGER;

    private static HttpServer httpServer;
    private static int webServerPort;
    private static String publicIP;
    private static final String resPackUrl = "http://%s:%s";
    private static File resPackFile;
    private static String resPackHash;

    @Override
    public void onEnable() {
        plugin = this;
        LOGGER = getLogger();

        resPackFile = Path.of(getDataPath().toString(), "pack.zip").toFile();

        this.saveDefaultConfig();

        // Loading Config
        FileConfiguration config = getConfig();
        webServerPort = config.getInt("webserver.port");

        // Registering Event Listeners
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerInteractEventListener(), this);
        pluginManager.registerEvents(new InventoryClickEventListener(), this);

        // Registering Commands
        CommandInit.load(getLifecycleManager(), this);

        zipPack();

        // Starting Web Server
        StartWebServer();
    }

    @Override
    public void onDisable() {
        StopWebServer();
    }

    private record JarEntryData(JarEntry entry, byte[] content) { }

    public static PaperModAPI getPlugin(){
        return plugin;
    }

    ///
    /// Returns if the hash changed or not, or if it failed ¯\_(ツ)_/¯ (false)
    ///
    public boolean zipPack() {
        getDataFolder().mkdir();
        try {
            if (resPackFile.exists()) {
                resPackHash = calculateSHA1(resPackFile);
            }
        } catch (Exception e) {
            return false;
        }

        // Loading Assets
        List<JarEntryData> entries = new ArrayList<>();

        for (File file : getServer().getPluginsFolder().listFiles()) {
            if (!file.getName().endsWith(".jar"))
                continue;

            try (JarFile jarFile = new JarFile(file)) {
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    JarEntry entry = jarEntries.nextElement();

                    if (entry.getName().startsWith("assets/") && !entry.isDirectory()) {
                        try(InputStream is = jarFile.getInputStream(entry)){
                            byte[] content = is.readAllBytes();
                            entries.add(new JarEntryData(entry, content));
                        }
                    }
                }
            } catch (IOException e) {
                return false;
            }
        }

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(resPackFile))) {
            for (JarEntryData entryData : entries) {
                ZipEntry zipEntry = new ZipEntry(entryData.entry().getName());
                zos.putNextEntry(zipEntry);

                zos.write(entryData.content());
                zos.closeEntry();
            }
            ZipEntry packMCMetaEntry = new ZipEntry("pack.mcmeta");
            zos.putNextEntry(packMCMetaEntry);
            zos.write("{\"pack\":{\"description\":\"\",\"pack_format\":46}}".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (resPackFile.exists()) {
            try {
                String sha1Digest = calculateSHA1(resPackFile);
                if(resPackHash == null || !resPackHash.equals(sha1Digest)){
                    resPackHash = sha1Digest;
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static ResourceLocation getResourceLocation(String id){
        return ResourceLocation.fromNamespaceAndPath("papermodapi", id);
    }

    private static String calculateSHA1(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        StringBuilder hash = new StringBuilder();
        for (byte b : digest.digest()) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

    private static int getFreePort() {
        try (ServerSocket socket = new ServerSocket(0, 0, InetAddress.getByName("0.0.0.0"))) {
            return socket.getLocalPort(); // Returns an available port.
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Handle the error as needed.
        }
    }

    private void StartWebServer() {
        try {
            publicIP = getPublicIP();

            webServerPort = webServerPort < 0 || webServerPort > 65535 ? getFreePort() : webServerPort;
            httpServer = HttpServer.create(new InetSocketAddress(webServerPort), 0);

            httpServer.createContext("/", exchange -> {
                File file = resPackFile;

                if (!file.exists()) {
                    String response = "File not found!";
                    exchange.sendResponseHeaders(404, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }

                // Set headers
                exchange.getResponseHeaders().set("Content-Type", "application/zip");
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=" + file.getName());
                exchange.sendResponseHeaders(200, file.length());

                // Stream the file
                try (OutputStream os = exchange.getResponseBody()) {
                    InputStream is = new FileInputStream(file);
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            });

            httpServer.start();

            String hexColor = "\u001B[38;2;%d;%d;%dm";
            String blueColor = String.format(hexColor, 85, 85, 255);
            String grayColor = String.format(hexColor, 170, 170, 170);
            String darkGrayColor = String.format(hexColor, 85, 85, 85);
            LOGGER.info(blueColor + "\n ___   _    ___  ___  ___         _   _      _    ___      " + String.format(hexColor, 0, 170, 0) + "PaperModAPI " + grayColor + getPluginMeta().getVersion() + blueColor + "\n" +
                                           "|__/  /_\\  |__/ |__  |__/   |\\/| / \\ | \\    /_\\  |__/ |    " + darkGrayColor + "Paper " + Bukkit.getVersion() + blueColor +
                                         "\n|    /   \\ |    |___ |  \\   |  | \\_/ |_/   /   \\ |    |\n\u001B[0m");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void StopWebServer(){
        if(httpServer != null){
            httpServer.stop(0);
            LOGGER.info(ChatColor.BLUE + "Stopped Web Server");
        }
    }

    private boolean hasResourcePack(){
        return resPackFile.exists();
    }

    public static void refreshResourcePack(Player player) {
        if (!resPackFile.exists()) return;
        player.setResourcePack(String.format(resPackUrl, publicIP, PaperModAPI.webServerPort), PaperModAPI.resPackHash);
    }

    public static void refreshResourcePack(){
        Bukkit.getOnlinePlayers().forEach(PaperModAPI::refreshResourcePack);
    }

    private static String getPublicIP() throws IOException {
        URL url = new URL("https://checkip.amazonaws.com/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String publicIP = reader.readLine();
        reader.close();

        return publicIP;
    }
}

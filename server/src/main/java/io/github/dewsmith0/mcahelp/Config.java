package io.github.dewsmith0.mcahelp;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Subst;


public class Config {
    private static final MCAhelp plugin = JavaPlugin.getPlugin(MCAhelp.class);
    private static final FileConfiguration configFile = plugin.getConfig();

    public static boolean offlineAhelpsEnabled;
    public static int maxNotifications;

    public static boolean rateLimitEnabled;
    public static Integer rateLimitThreshold;
    public static Long rateLimitWindow;

    @Subst(value = "minecraft:entity.player.splash.high_speed") // required so IntelliJ IDEA doesn't blow up
    public static String bwoinkSoundId;
    public static Integer bwoinkStackCount;
    public static Float bwoinkVolume;
    public static Float bwoinkPitch;

    public static void load() {
        plugin.saveDefaultConfig();
        offlineAhelpsEnabled = configFile.getBoolean("offline-ahelps.enabled", true);
        maxNotifications = Math.max(configFile.getInt("offline-ahelps.max-notifications", 5), 0);

        rateLimitEnabled = configFile.getBoolean("rate-limit.enabled", true);
        rateLimitThreshold = Math.clamp(configFile.getInt("rate-limit.threshold", 5), 1, 100);
        rateLimitWindow = Math.max(configFile.getLong("rate-limit.window", 3000), 0);

        bwoinkSoundId = configFile.getString("bwoink.sound-id", "minecraft:entity.player.splash.high_speed");
        bwoinkStackCount = Math.clamp(configFile.getInt("bwoink.stack-count", 8), 1, 15);
        bwoinkVolume = (float) Math.clamp(configFile.getDouble("bwoink.volume", 1), 0, 1);
        bwoinkPitch = (float) Math.clamp(configFile.getDouble("bwoink.pitch", 1), 0.5, 2);
    }

}
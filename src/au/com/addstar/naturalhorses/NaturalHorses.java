package au.com.addstar.naturalhorses;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.logging.Logger;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import org.bukkit.Location;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class NaturalHorses extends JavaPlugin {
	public static NaturalHorses instance;
	public PluginDescriptionFile pdfFile = null;
	public PluginManager pm = null;
	private static final Logger logger = Logger.getLogger("Minecraft");
	public ConfigManager cfg = new ConfigManager(this);
	public WorldGuardPlugin WG;
	public RegionManager RM;
	public long LastSpawn = 0;
	public static Random RandomGen = new Random();

	// Pluing settings
	public static boolean DebugEnabled = false;
	public static boolean BroadcastLocation = false;
	public static String HorseWorld = "survival";
	public static int SpawnDelay = 30;
	public static int ChunkRadius = 2;
	public static int SpawnChance = 1;
	public static int DonkeyChance = 10;
	
	@Override
	public void onEnable() {
		// Register necessary events
		pdfFile = this.getDescription();
		pm = this.getServer().getPluginManager();

		// Read (or initialise) plugin config file
		cfg.LoadConfig(getConfig());

		// Save the default config (if one doesn't exist)
		saveDefaultConfig();

		WG = getWorldGuard();
		if (WG == null) {
			Log("WorldGuard not detected, integration disabled.");
		} else {
			RM = WG.getRegionManager(getServer().getWorld(HorseWorld));
			Log("WorldGuard integration successful.");
			
		}

		try{
            @SuppressWarnings("rawtypes")
            Class[] args = new Class[3];
            args[0] = Class.class;
            args[1] = String.class;
            args[2] = int.class;
 
            Method a = net.minecraft.server.v1_6_R1.EntityTypes.class.getDeclaredMethod("a", args);
            a.setAccessible(true);
            a.invoke(a, MyHorse.class, "Horse", 100);
        }catch (Exception e){
            e.printStackTrace();
            this.setEnabled(false);
        }

		pm.registerEvents(new ChunkListener(this), this);
		Log(pdfFile.getName() + " " + pdfFile.getVersion() + " has been enabled");
	}
	
	@Override
	public void onDisable() {
		// Nothing yet
	}

	public void Log(String data) {
		logger.info(pdfFile.getName() + " " + data);
	}

	public void Warn(String data) {
		logger.warning(pdfFile.getName() + " " + data);
	}
	
	public void Debug(String data) {
		if (DebugEnabled) {
			logger.info(pdfFile.getName() + " " + data);
		}
	}

	private WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) plugin;
	}
	
	public boolean CanSpawnMob(Location loc) {
		ApplicableRegionSet set = RM.getApplicableRegions(loc);
		if (set == null) { return true; }
		return set.allows(DefaultFlag.MOB_SPAWNING);
	}
}
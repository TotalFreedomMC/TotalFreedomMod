package me.totalfreedom.totalfreedommod.banning;

import com.google.common.collect.Sets;
import java.util.Set;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.config.YamlConfig;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

public class PermbanList extends FreedomService
{

    public static final String CONFIG_FILENAME = "permbans.yml";

    @Getter
    private final Set<String> permbannedNames = Sets.newHashSet();
    @Getter
    private final Set<String> permbannedIps = Sets.newHashSet();

    public PermbanList()
    {
        super();
    }

    @Override
    public void start()
    {
        permbannedNames.clear();
        permbannedIps.clear();

        final YamlConfig config = new YamlConfig(plugin, CONFIG_FILENAME);
        config.load();

        for (String name : config.getKeys(false))
        {
            permbannedNames.add(name.toLowerCase().trim());
            permbannedIps.addAll(config.getStringList(name));
        }

        FLog.info("Loaded " + permbannedIps.size() + " perm IP bans and " + permbannedNames.size() + " perm username bans.");
    }

    @Override
    public void stop()
    {
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        final String username = event.getPlayer().getName();
        final String ip = event.getAddress().getHostAddress().trim();

        // Permbanned IPs
        for (String testIp : getPermbannedIps())
        {
            if (FUtil.fuzzyIpMatch(testIp, ip, 4))
            {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        ChatColor.RED + "Your IP address is permanently banned from this server.\n"
                                + "Release procedures are available at\n"
                                + ChatColor.GOLD + ConfigEntry.SERVER_PERMBAN_URL.getString());
                return;
            }
        }

        // Permbanned usernames
        for (String testPlayer : getPermbannedNames())
        {
            if (testPlayer.equalsIgnoreCase(username))
            {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        ChatColor.RED + "Your username is permanently banned from this server.\n"
                                + "Release procedures are available at\n"
                                + ChatColor.GOLD + ConfigEntry.SERVER_PERMBAN_URL.getString());
                return;
            }
        }

    }

    public Set<String> getPermbannedNames()
    {
        return this.permbannedNames;
    }

    public Set<String> getPermbannedIps()
    {
        return this.permbannedIps;
    }
}
